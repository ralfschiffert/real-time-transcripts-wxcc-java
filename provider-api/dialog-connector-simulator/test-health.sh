#!/bin/bash

# Test script to demonstrate Health service functionality
echo "Testing Health service without authentication..."

# Start the server in background (you'll need to start it manually first)
echo "Make sure the gRPC server is running on port 8086 or 8087"
echo ""

# Test Health/Check without authorization header
echo "1. Testing Health/Check without authorization (should work):"
grpcurl -plaintext localhost:8086 com.cisco.wcc.ccai.v1.Health/Check
echo ""

# Test Health/Check with a service parameter
echo "2. Testing Health/Check with service parameter (should work):"
grpcurl -plaintext -d '{"service": "test-service"}' localhost:8086 com.cisco.wcc.ccai.v1.Health/Check
echo ""

# Test another service without authorization (should fail)
echo "3. Testing AnalyzeContentService without authorization (should fail with authentication error):"
grpcurl -plaintext localhost:8086 com.cisco.wcc.ccai.v1.AnalyzeContentService/ListVirtualAgents || echo "Expected authentication error"
echo ""

# Test another service with authorization (should work if token is valid)
echo "4. Testing AnalyzeContentService with Bearer token (should work):"
grpcurl -plaintext -H "authorization: Bearer dummy-token" localhost:8086 com.cisco.wcc.ccai.v1.AnalyzeContentService/ListVirtualAgents || echo "Service may not be fully implemented"
echo ""

echo "Test completed. The Health service should be accessible without authentication, while other services require Bearer token."

