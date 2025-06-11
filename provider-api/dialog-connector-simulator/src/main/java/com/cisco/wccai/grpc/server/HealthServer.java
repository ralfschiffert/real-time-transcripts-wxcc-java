package com.cisco.wccai.grpc.server;

import com.cisco.wccai.grpc.server.interceptors.AuthenticationInterceptor;
import com.cisco.wccai.grpc.server.interceptors.ServiceExceptionHandler;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Simple gRPC server that includes the Health service accessible without authentication.
 */
public class HealthServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthServer.class);
    private static final int PORT = 8087; // Using a different port to avoid conflicts

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(PORT)
                .addService(new HealthServiceImpl())
                 //.addService(new GrpcServerImpl()) // Add the main service too
                .intercept(new AuthenticationInterceptor())
                .intercept(new ServiceExceptionHandler())
                .build()
                .start();

        LOGGER.info("Health-enabled server started at port: {}", PORT);
        LOGGER.info("Health service is accessible without authentication at com.cisco.wcc.ccai.v1.Health/Check");
        LOGGER.info("All other services require Bearer token authorization");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Received Shutdown Request");
            server.shutdown();
            LOGGER.info("Successfully Stopped, Shutting down the server");
        }));

        server.awaitTermination();
    }
}

