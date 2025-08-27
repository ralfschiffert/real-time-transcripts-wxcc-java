# Webex Contact Center AI Streaming Insight Java Client

A comprehensive Java client for streaming AI insights from Webex Contact Center using gRPC and Protocol Buffers.

## Features

- **Streaming Insights**: Real-time streaming of AI insights including transcriptions, agent answers, messages, and virtual agent responses
- **One-time Insights**: Fetch specific insights on-demand
- **Multiple Response Handlers**: Console, JSON, and transcript-specific handlers
- **Configurable Client**: Support for TLS, authentication, and connection tuning
- **Interactive CLI**: Interactive command-line interface for testing
- **Robust Error Handling**: Comprehensive error handling and logging

## Quick Start

### Prerequisites

- **Java 17 or higher** (Required) - See [JAVA_SETUP.md](JAVA_SETUP.md) for installation guide
- Gradle 8.5 or higher (or use included wrapper)
- For optimal compatibility, we recommend OpenJDK 17 or Oracle JDK 17+

### Building the Project

```bash
# Clone and navigate to the project directory
cd java-client

# Build the project (generates protobuf classes and compiles)
./gradlew build

# Run the client interactively
./gradlew run --console=plain --quiet
```

### Running with Custom Parameters

```bash
# Run with command line arguments
./gradlew run --console=plain --quiet -Pargs="your-access-token your-org-id server-host port"

# Example with real parameters
./gradlew run --console=plain --quiet -Pargs="abc123token my-org-456 api.wxcc.ai 443"

# Or run the JAR directly
java -jar build/libs/java-client-1.0.0.jar your-access-token your-org-id your-server.com 443
```

## Usage Examples

### Basic Streaming Usage

```java
// Create client configuration
StreamingInsightClientConfig config = StreamingInsightClientConfig.newBuilder()
    .setServerHost("api.wxcc.ai")
    .setServerPort(443)
    .setUseTls(true)
    .setAccessToken("your-bearer-token")
    .build();

// Create client
try (StreamingInsightClient client = new StreamingInsightClient(config)) {
    
    // Start streaming insights
    StreamingInsightSession session = client.startStreamingInsights(
        "conversation-123",  // conversation ID
        "org-456",          // organization ID  
        "agent-789",        // agent ID
        ResponseHandler.createConsoleHandler(),  // response handler
        ResponseHandler.createErrorHandler()     // error handler
    );
    
    // Wait for insights...
    session.awaitCompletion(30, TimeUnit.SECONDS);
}
```

### Custom Request Configuration

```java
// Create custom insight request
InsightServingRequest request = InsightServingRequest.newBuilder()
    .setConversationId("conversation-123")
    .setOrgId("org-456")
    .setRealTimeTranscripts(true)
    .setHistoricalTranscripts(false)
    .setRealtimeAgentAssist(true)
    .setAgentDetails(AgentDetails.newBuilder().setAgentId("agent-789").build())
    .build();

// Start streaming with custom request
session = client.startStreamingInsights(
    request,
    response -> {
        // Custom response handling
        System.out.println("Received insight: " + response);
    },
    error -> {
        // Custom error handling
        System.err.println("Error: " + error.getMessage());
    }
);
```

### One-time Insights

```java
// Get specific insights
InsightsServingResponse response = client.getInsights(
    "conversation-123",
    "org-456", 
    InsightsServingRequest.InsightType.TRANSCRIPTION
);

System.out.println("Insights: " + response);
```

## Response Handlers

The client provides several pre-built response handlers:

### Console Handler
Detailed console output with structured formatting:
```java
ResponseHandler.createConsoleHandler()
```

### Transcript Handler  
Only processes and displays transcription insights:
```java
ResponseHandler.createTranscriptHandler()
```

### JSON Handler
Outputs responses as JSON for integration:
```java
ResponseHandler.createJsonHandler()
```

### Custom Handler
Create your own handler:
```java
Consumer<StreamingInsightServingResponse> customHandler = response -> {
    InsightServingResponse insight = response.getInsightServingResponse();
    
    // Process different insight types
    switch (insight.getInsightType()) {
        case TRANSCRIPTION:
            handleTranscript(insight);
            break;
        case AGENT_ANSWERS:
            handleAgentAnswer(insight);
            break;
        // ... other types
    }
};
```

## Configuration Options

### Client Configuration

```java
StreamingInsightClientConfig config = StreamingInsightClientConfig.newBuilder()
    .setServerHost("localhost")              // Server hostname
    .setServerPort(9090)                     // Server port  
    .setUseTls(false)                        // Enable TLS (use true for production)
    .setAccessToken("bearer-token")          // Authentication token
    .setMaxInboundMessageSize(4 * 1024 * 1024)  // Max message size (4MB)
    .setKeepAliveTimeoutMs(30000)           // Keep-alive timeout (30s)
    .setKeepAliveIntervalMs(10000)          // Keep-alive interval (10s)
    .build();
```

### Environment Variables

You can also configure using environment variables:

```bash
export WXCC_SERVER_HOST=api.wxcc.ai
export WXCC_SERVER_PORT=443  
export WXCC_ACCESS_TOKEN=your-token
export WXCC_USE_TLS=true
```

## Interactive CLI

Run the client without arguments for an interactive menu:

```bash
./gradlew run
```

The interactive CLI provides:
1. **Start streaming insights** - Interactive streaming with user input
2. **Get one-time insights** - Fetch specific insights  
3. **Get insights by interaction ID** - Fetch insights for specific interactions
4. **Streaming demo** - Demo with sample data
5. **Exit** - Close the application

## Gradle Wrapper Usage

This project uses the Gradle Wrapper (gradlew), which means you don't need to install Gradle locally. The wrapper automatically downloads and uses the correct Gradle version.

### Running the Application

#### Interactive Mode
```bash
# Run interactively with clean console output
./gradlew run --console=plain --quiet

# Basic run (with more Gradle output)
./gradlew run
```

#### With Command Line Arguments
```bash
# Pass arguments using -Pargs
./gradlew run --console=plain --quiet -Pargs="token org-id host port"

# Examples
./gradlew run --console=plain --quiet -Pargs="my-token-123"
./gradlew run --console=plain --quiet -Pargs="my-token-123 my-org-456"
./gradlew run --console=plain --quiet -Pargs="my-token-123 my-org-456 api.wxcc.ai 443"
```

#### Using the runClient Task
```bash
# Alternative task with same functionality
./gradlew runClient --console=plain --quiet
./gradlew runClient --console=plain --quiet -Pargs="token org-id"
```

### Build Commands

```bash
# Full clean build
./gradlew clean build

# Just compile
./gradlew compileJava

# Run tests
./gradlew test

# Generate protobuf classes
./gradlew generateProto

# Create JAR file
./gradlew jar

# Create fat JAR with all dependencies
./gradlew shadowJar
```

### Command Line Flags

- `--console=plain` - Cleaner console output without progress bars
- `--quiet` - Reduces Gradle's verbose output
- `--no-daemon` - Disables Gradle daemon (useful for CI/CD)
- `--stacktrace` - Shows full stack traces on errors
- `--debug` - Enables debug output

### Examples

```bash
# Quick start for development
./gradlew run --console=plain --quiet

# Run with your credentials
./gradlew run --console=plain --quiet -Pargs="your-access-token your-org-id"

# Debug build issues
./gradlew build --stacktrace --debug

# Clean rebuild
./gradlew clean build --no-daemon
```

### Alternative Run Methods

You can also use the provided shell scripts that use gradlew internally:

```bash
# Uses gradlew build internally
./build.sh

# Runs the JAR directly
./run.sh

# Enhanced runner script
./run-client.sh your-access-token
```

## Building and Deployment

### Build JAR

```bash
./gradlew jar
```

### Build Fat JAR (with dependencies)

```bash
./gradlew shadowJar  # If shadow plugin is added
# Or use the built-in
./gradlew build
```

### Generate Protocol Buffer Classes

```bash
./gradlew generateProto
```

### Clean Build

```bash
./gradlew clean build
```

## Project Structure

```
java-client/
├── src/main/java/com/cisco/wcc/ccai/client/
│   ├── StreamingInsightClient.java          # Main client implementation
│   ├── StreamingInsightClientConfig.java    # Client configuration
│   ├── StreamingInsightClientMain.java      # CLI application
│   └── ResponseHandler.java                 # Response handling utilities
├── src/main/resources/
│   └── logback.xml                          # Logging configuration
├── build.gradle                             # Build configuration
├── gradlew                                  # Gradle wrapper script
└── README.md                               # This documentation
```

## Protocol Buffers

The client uses the following `.proto` files:
- `serving.proto` - Main service definitions
- `recognize.proto` - Speech recognition types
- `suggestions.proto` - Agent answer types  
- `messages.proto` - Message types
- `virtualagent.proto` - Virtual agent/NLU types

## Error Handling

The client provides comprehensive error handling:

- **Connection errors** - Automatic retry with exponential backoff
- **Authentication errors** - Clear error messages for token issues  
- **Timeout handling** - Configurable timeouts for operations
- **Stream errors** - Graceful handling of stream interruptions
- **Logging** - Detailed logging for troubleshooting

## Logging

Logs are written to:
- **Console** - Real-time log output
- **File** - `logs/streaming-insight-client.log` (rolling, max 100MB total)

Configure logging levels in `src/main/resources/logback.xml`.

## Production Considerations

### Security
- Always use TLS in production (`setUseTls(true)`)
- Protect access tokens (use environment variables)
- Validate certificates in production environments

### Performance  
- Tune `maxInboundMessageSize` based on expected response sizes
- Adjust keep-alive settings for network conditions
- Use appropriate logging levels in production

### Monitoring
- Monitor connection health using session status
- Implement custom metrics collection in response handlers
- Set up alerts for connection failures

## Troubleshooting

### Common Issues

**Connection refused:**
```
Check server host/port and network connectivity
Verify TLS settings match server requirements
```

**Authentication failed:**  
```
Verify access token is valid and not expired
Check token has required permissions for the org
```

**Proto compilation errors:**
```bash
./gradlew clean generateProto
```

**Java version issues:**
```
Ensure Java 17+ is installed and JAVA_HOME is set:
export JAVA_HOME=/path/to/java-17
java -version  # Should show 17 or higher
```

**OutOfMemory errors:**
```
Increase maxInboundMessageSize or JVM heap size:
java -Xmx2g -jar client.jar
```

**Gradlew run fails with "No line found":**
```bash
# Use the correct console flags
./gradlew run --console=plain --quiet

# Or use the runClient task
./gradlew runClient --console=plain --quiet
```

**Gradlew not executable:**
```bash
# Make gradlew executable
chmod +x ./gradlew
```

### Debug Mode

Enable debug logging:
```xml
<!-- In logback.xml -->
<logger name="com.cisco.wcc.ccai.client" level="DEBUG"/>
<logger name="io.grpc" level="DEBUG"/>
```

## Contributing

1. Fork the repository
2. Create a feature branch  
3. Add tests for new functionality
4. Submit a pull request

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
