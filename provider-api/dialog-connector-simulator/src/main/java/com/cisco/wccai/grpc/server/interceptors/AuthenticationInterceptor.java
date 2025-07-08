package com.cisco.wccai.grpc.server.interceptors;

import com.cisco.wccai.grpc.server.ValidateToken;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationInterceptor implements ServerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    private static final String HEALTH_SERVICE_NAME = "com.cisco.wcc.ccai.v1.Health";
    private static final String REFLECTION_SERVICE_NAME = "grpc.reflection.v1alpha.ServerReflection";
    public static final Metadata.Key<String> AUTHORIZATION_KEY = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {

        String methodName = serverCall.getMethodDescriptor().getFullMethodName();

        // Bypass authentication for health and reflection services
        if (methodName.startsWith(HEALTH_SERVICE_NAME) || methodName.startsWith(REFLECTION_SERVICE_NAME)) {
            LOGGER.debug("Bypassing authentication for internal service: {}", methodName);
            return serverCallHandler.startCall(serverCall, metadata);
        }

        LOGGER.info("Authenticating call to method: {}", methodName);

        String authHeader = metadata.get(AUTHORIZATION_KEY);
        LOGGER.info("The auth header is {}", authHeader);

        // Use the validation result to control access
        if (ValidateToken.validateKey(authHeader)) {
            LOGGER.info("Authentication successful for method: {}", methodName);
            return serverCallHandler.startCall(serverCall, metadata);
        } else {
            // If validation fails, deny the call.
            LOGGER.warn("Authentication failed for method: {}. Denying access.", methodName);
            serverCall.close(Status.UNAUTHENTICATED.withDescription("Invalid or missing Bearer token"), new Metadata());
            return new ServerCall.Listener<>() {}; // Return a no-op listener
        }
    }
}