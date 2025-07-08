package com.cisco.wccai.grpc.server;

import com.cisco.wcc.ccai.media.v1.ConversationAudioGrpc;
import com.cisco.wcc.ccai.media.v1.Conversationaudioforking;
import com.google.cloud.storage.*;
import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
// CORRECTED: This import must point to the standard Java NIO package.
import com.google.cloud.WriteChannel;
import java.util.HashMap;
import java.util.Map;

public class ConversationAudioForkServiceImpl extends ConversationAudioGrpc.ConversationAudioImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationAudioForkServiceImpl.class);

    private final Storage storage;
    private final String bucketName;

    /**
     * Constructor initializes the Google Cloud Storage client and gets the bucket name.
     */
    public ConversationAudioForkServiceImpl() {
        // This will use the application's default credentials.
        // On Cloud Run, it uses the service account. Locally, `gcloud auth application-default login`.
        LOGGER.info("ConversationAudioForkServiceImpl constructor called");

        this.storage = StorageOptions.getDefaultInstance().getService();

        if (this.storage == null) {
            LOGGER.error("Could not get default storage instance. This service will not be able to save audio.");
        }

        // Get the GCS bucket name from an environment variable for flexibility.
        this.bucketName = System.getenv("GCS_BUCKET_NAME");
        if (this.bucketName == null || this.bucketName.isEmpty()) {
            LOGGER.error("GCS_BUCKET_NAME environment variable not set. Audio will not be saved.");
            // To fail fast, you could throw an exception to prevent the service from starting without configuration.
            // throw new IllegalStateException("GCS_BUCKET_NAME environment variable is required.");
        } else {
            LOGGER.info("GCS_BUCKET_NAME environment variable set to {}", bucketName);
        }
    }

    @Override
    public StreamObserver<Conversationaudioforking.ConversationAudioForkingRequest> streamConversationAudio(
            StreamObserver<Conversationaudioforking.ConversationAudioForkingResponse> responseObserver) {

        // If the bucket or storage client isn't configured, return an error to the client immediately.
        if (bucketName == null || storage == null) {
            responseObserver.onError(
                    Status.FAILED_PRECONDITION
                            .withDescription("Server is not configured to save audio streams.")
                            .asRuntimeException()
            );
            return new NoOpStreamObserver(); // Return a no-op observer.
        }

        // By returning a dedicated class, the main service method stays clean and testable.
        return new AudioStreamToGcsHandler(responseObserver, storage, bucketName);
    }

    /**
     * A dedicated handler for the incoming audio stream that writes it to Google Cloud Storage.
     */
    private static class AudioStreamToGcsHandler implements StreamObserver<Conversationaudioforking.ConversationAudioForkingRequest> {
        private final StreamObserver<Conversationaudioforking.ConversationAudioForkingResponse> responseObserver;
        private final Storage storage;
        private final String bucketName;

        // MODIFIED: Use a Map to hold a writer for each roleId.
        private final Map<String, WriteChannel> gcsWriters = new HashMap<>();
        private final Map<String, String> gcsObjectNames = new HashMap<>();

        public AudioStreamToGcsHandler(StreamObserver<Conversationaudioforking.ConversationAudioForkingResponse> responseObserver, Storage storage, String bucketName) {
            this.responseObserver = responseObserver;
            this.storage = storage;
            this.bucketName = bucketName;
            LOGGER.info("AudioStreamToGcsHandler initialized");
        }

        @Override
        public void onNext(Conversationaudioforking.ConversationAudioForkingRequest request) {
            String conversationId = request.getConversationId();
            String roleId = request.getAudio().getRoleId();
            ByteString audioData = request.getAudio().getAudioData();

            LOGGER.info("Received audio chunk for conversationId: {}, roleId: {}", conversationId, roleId);

            try {
                // MODIFIED: Get or create the writer for the specific roleId.
                if (!gcsWriters.containsKey(roleId)) {
                    initializeGcsWriter(conversationId, roleId);
                }

                WriteChannel writer = gcsWriters.get(roleId);

                // Write the audio data to the correct GCS object.
                if (writer != null && audioData != null && !audioData.isEmpty()) {
                    writer.write(ByteBuffer.wrap(audioData.toByteArray()));
                    LOGGER.info("Wrote {} bytes to file for roleId {}", audioData.size(), roleId);
                }

                // Acknowledge the message.
                responseObserver.onNext(Conversationaudioforking.ConversationAudioForkingResponse.newBuilder()
                        .setStatusMessage("Processed chunk for conversationId: " + conversationId)
                        .build());

            } catch (IOException | IllegalArgumentException e) {
                LOGGER.error("Failed to process audio stream for roleId: {}", roleId, e);
                responseObserver.onError(
                        Status.INTERNAL
                                .withDescription("Failed to write audio stream to storage: " + e.getMessage())
                                .withCause(e)
                                .asRuntimeException()
                );
                // Clean up all writers on failure.
                closeAllWriters();
            }
        }

        @Override
        public void onError(Throwable t) {
            LOGGER.error("Client stream produced an error", t);
            closeAllWriters();
            // The responseObserver is likely already closed by the transport, so no need to call it.
        }

        @Override
        public void onCompleted() {
            LOGGER.info("Client has finished sending audio. Finalizing all GCS objects.");
            closeAllWriters();
            responseObserver.onCompleted();
        }

        // MODIFIED: This method now creates a writer for a specific role.
        private void initializeGcsWriter(String conversationId, String roleId) {
            if (conversationId == null || conversationId.isEmpty() || roleId == null || roleId.isEmpty()) {
                throw new IllegalArgumentException("Conversation ID and Role ID are required to create a storage object.");
            }
            // Define the object name in GCS, including the roleId to make it unique.
            String gcsObjectName = String.format("audio/%s-%s.raw", conversationId, roleId);
            gcsObjectNames.put(roleId, gcsObjectName);

            BlobId blobId = BlobId.of(bucketName, gcsObjectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    // Set the content type for better handling by other applications.
                    .setContentType("application/octet-stream")
                    .build();

            WriteChannel newWriter = storage.writer(blobInfo);
            gcsWriters.put(roleId, newWriter);
            LOGGER.info("Starting to write audio stream to gs://{}/{}", bucketName, gcsObjectName);
        }

        // MODIFIED: This method now closes all open writers.
        private void closeAllWriters() {
            for (Map.Entry<String, WriteChannel> entry : gcsWriters.entrySet()) {
                String roleId = entry.getKey();
                WriteChannel writer = entry.getValue();
                String objectName = gcsObjectNames.get(roleId);

                if (writer != null && writer.isOpen()) {
                    try {
                        writer.close();
                        LOGGER.info("Successfully closed GCS writer for {}", objectName);
                    } catch (IOException e) {
                        LOGGER.error("Error closing GCS write channel for {}", objectName, e);
                    }
                }
            }
            gcsWriters.clear();
            gcsObjectNames.clear();
        }
    }

    /**
     * A simple no-op observer to handle cases where the service can't start.
     */
    private static class NoOpStreamObserver implements StreamObserver<Conversationaudioforking.ConversationAudioForkingRequest> {
        @Override
        public void onNext(Conversationaudioforking.ConversationAudioForkingRequest value) {}
        @Override
        public void onError(Throwable t) {}
        @Override
        public void onCompleted() {}
    }
}