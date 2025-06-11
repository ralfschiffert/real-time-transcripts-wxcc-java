# Health Service Configuration

This document describes the modifications made to enable the Health service to be called without authorization.

## Changes Made

### 1. Health Service Implementation
Created `HealthServiceImpl.java` that extends `HealthGrpc.HealthImplBase` and provides:
- `check()` method: Returns SERVING status for health checks
- `watch()` method: Returns SERVING status for health monitoring

### 2. Authentication Interceptor
Created `AuthenticationInterceptor.java` that:
- Allows all calls to `com.cisco.wcc.ccai.v1.Health/*` without authentication
- Requires Bearer token authorization for all other services
- Logs all intercepted calls for debugging

### 3. Server Configuration
Modified `GrpcServer.java` to:
- Add the Health service implementation
- Add the authentication interceptor before the exception handler
- Maintain backward compatibility with existing services

## Usage

### Starting the Server
```bash
cd provider-api/dialog-connector-simulator

# Option 1: Use the existing server (modified)
mvn compile exec:java -Dexec.mainClass="com.cisco.wccai.grpc.server.GrpcServer"

# Option 2: Use the dedicated health server (on port 8087)
mvn compile exec:java -Dexec.mainClass="com.cisco.wccai.grpc.server.HealthServer"
```

### Testing with grpcurl

#### Health Service (No Authentication Required)
```bash
# Basic health check
grpcurl -plaintext localhost:8086 com.cisco.wcc.ccai.v1.Health/Check

# Health check with service parameter
grpcurl -plaintext -d '{"service": "my-service"}' localhost:8086 com.cisco.wcc.ccai.v1.Health/Check

# Health watch (streaming)
grpcurl -plaintext localhost:8086 com.cisco.wcc.ccai.v1.Health/Watch
```

#### Other Services (Authentication Required)
```bash
# This will fail with UNAUTHENTICATED error
grpcurl -plaintext localhost:8086 com.cisco.wcc.ccai.v1.AnalyzeContentService/ListVirtualAgents

# This will pass authentication check
grpcurl -plaintext -H "authorization: Bearer your-token" localhost:8086 com.cisco.wcc.ccai.v1.AnalyzeContentService/ListVirtualAgents
```

### Running the Test Script
```bash
# Make sure the server is running first, then:
./test-health.sh
```

## Configuration

### Customizing Authentication
You can modify the `AuthenticationInterceptor.java` to:
- Change which services bypass authentication
- Implement proper token validation
- Add additional authentication logic

### Adding More Health Checks
You can enhance the `HealthServiceImpl.java` to:
- Check actual service dependencies
- Return different status based on service health
- Implement more sophisticated health monitoring

## Service Details

### Health Service Proto
The Health service is defined in `health.proto` and provides:
- **Check**: Unary RPC for immediate health status
- **Watch**: Server streaming RPC for continuous health monitoring

### Status Values
- `UNKNOWN` (0): Status unknown
- `SERVING` (1): Service is healthy and serving requests
- `NOT_SERVING` (2): Service is not healthy

### Server Ports
- Original server: 8086
- Health-only server: 8087

## Troubleshooting

### Common Issues
1. **Server not starting**: Check for port conflicts or missing dependencies
2. **Authentication errors**: Verify the interceptor order and service name matching
3. **Health service not found**: Ensure protobuf classes are generated with `mvn compile`

### Debug Logging
The authentication interceptor logs all intercepted calls. Check the server logs to verify:
- Which methods are being called
- Whether authentication is being bypassed for Health service
- Any authentication failures for other services

## Security Notes

- The Health service bypasses all authentication by design
- Other services still require proper Bearer token authentication
- In production, consider implementing proper token validation in the interceptor
- Health endpoints should not expose sensitive information

