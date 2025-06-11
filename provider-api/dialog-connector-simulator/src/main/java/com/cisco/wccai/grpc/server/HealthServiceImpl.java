package com.cisco.wccai.grpc.server;

import com.cisco.wcc.ccai.v1.HealthGrpc;
import com.cisco.wcc.ccai.v1.HealthOuterClass;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Health service implementation that provides health check functionality
 * without requiring authorization tokens.
 */
public class HealthServiceImpl extends HealthGrpc.HealthImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthServiceImpl.class);

    @Override
    public void check(HealthOuterClass.HealthCheckRequest request, 
                     StreamObserver<HealthOuterClass.HealthCheckResponse> responseObserver) {
        LOGGER.info("Health check request received for service: {}", request.getService());
        
        // Create response indicating the service is serving
        HealthOuterClass.HealthCheckResponse response = HealthOuterClass.HealthCheckResponse.newBuilder()
                .setStatus(HealthOuterClass.HealthCheckResponse.ServingStatus.SERVING)
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        
        LOGGER.info("Health check response sent: SERVING");
    }

    @Override
    public void watch(HealthOuterClass.HealthCheckRequest request, 
                     StreamObserver<HealthOuterClass.HealthCheckResponse> responseObserver) {
        LOGGER.info("Health watch request received for service: {}", request.getService());
        
        // For the watch method, we'll send a SERVING status and keep the stream open
        HealthOuterClass.HealthCheckResponse response = HealthOuterClass.HealthCheckResponse.newBuilder()
                .setStatus(HealthOuterClass.HealthCheckResponse.ServingStatus.SERVING)
                .build();
        
        responseObserver.onNext(response);
        // Note: We don't call onCompleted() for watch as it's a streaming response
        // In a real implementation, you might want to periodically send status updates
        
        LOGGER.info("Health watch response sent: SERVING");
    }
}

