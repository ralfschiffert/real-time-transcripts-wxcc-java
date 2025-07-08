package com.cisco.wccai.grpc.server;

import com.cisco.wccai.grpc.server.interceptors.AuthenticationInterceptor;
import com.cisco.wccai.grpc.server.interceptors.ServiceExceptionHandler;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The type Grpc server.
 */
public class GrpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServer.class);
    private static final int PORT = 8086;
    //private static final int PORT


    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public static void main(String [] args) throws IOException, InterruptedException {

        String port = System.getenv("PORT");
        int listeningPort;

        if (port == null || port.isEmpty()) {
            listeningPort = PORT;
        } else {
            listeningPort = Integer.parseInt(port);
        }


       LOGGER.info("the environment variable port is : {}", port);

       Server server = ServerBuilder.forPort(listeningPort)
                .addService(new ConversationAudioForkServiceImpl())
                .addService(new HealthServiceImpl())
                .addService(ProtoReflectionService.newInstance())
                .intercept(new AuthenticationInterceptor())
                .intercept(new ServiceExceptionHandler())
                .build()
                .start();

        LOGGER.info("server started at port : {}", listeningPort );

        LOGGER.info("THIS IS AN ADDITIONAL LOG FROM RALF");

       LOGGER.info("Initializing the context");
        Context.init();

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            LOGGER.info("Received Shutdown Request");
            server.shutdown();
            LOGGER.info("Successfully Stopped, Shutting down the server");
        }));


        // await for Termination of Program
        server.awaitTermination();
    }
}
