package com.cisco.wccai.grpc.server.interceptors;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authentication interceptor that requires authorization for all services
 * except for the Health service which can be called without authentication.
 */
public class AuthenticationInterceptor implements ServerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    private static final String HEALTH_SERVICE_NAME = "com.cisco.wcc.ccai.v1.Health";
    private static final String REFLECTION_SERVICE_NAME = "grpc.reflection.v1alpha.ServerReflection";
    private static final String AUTHORIZATION_HEADER = "authorization";

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {

        String methodName = serverCall.getMethodDescriptor().getFullMethodName();
        LOGGER.info("Intercepting call to method: {}", methodName);

        // Allow Health service calls without authentication
        if (methodName.startsWith(HEALTH_SERVICE_NAME)) {
            LOGGER.info("Health service call detected, bypassing authentication for: {}", methodName);
            return serverCallHandler.startCall(serverCall, metadata);
        }

        // Allow reflection service calls without authentication (needed for grpcurl)
        if (methodName.startsWith(REFLECTION_SERVICE_NAME)) {
            LOGGER.info("Reflection service call detected, bypassing authentication for: {}", methodName);
            return serverCallHandler.startCall(serverCall, metadata);
        }

        // For all other services, check for authorization header
        String authHeader = metadata.get(Metadata.Key.of(AUTHORIZATION_HEADER, Metadata.ASCII_STRING_MARSHALLER));
        
        if (authHeader == null || authHeader.isEmpty()) {
            LOGGER.warn("Missing authorization header for method: {}", methodName);
            serverCall.close(Status.UNAUTHENTICATED.withDescription("Missing authorization header"), metadata);
            return new ServerCall.Listener<ReqT>() {};
        }

        // Basic validation - in a real implementation, you would validate the token properly
        if (!authHeader.startsWith("Bearer ")) {
            LOGGER.warn("Invalid authorization header format for method: {}", methodName);
            serverCall.close(Status.UNAUTHENTICATED.withDescription("Invalid authorization header format"), metadata);
            return new ServerCall.Listener<ReqT>() {};
        }

        LOGGER.info("Authentication successful for method: {}", methodName);
        return serverCallHandler.startCall(serverCall, metadata);
    }
}

