package pt.dcs.example.spring_ai.mcp_weather_client;

import io.modelcontextprotocol.spec.McpSchema.CreateMessageRequest;
import io.modelcontextprotocol.spec.McpSchema.CreateMessageResult;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;
import io.modelcontextprotocol.spec.McpSchema.ProgressNotification;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpLogging;
import org.springaicommunity.mcp.annotation.McpProgress;
import org.springaicommunity.mcp.annotation.McpSampling;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
// Key Design Patterns:
// - Annotation-Based Routing: The clients = "spring-ai-my-weather-server" attribute ensures handlers only process notifications from the
//   specific MCP server connection defined in your configuration: spring.ai.mcp.client.streamable-http.connections.[my-weather-server].url.
// - The @Lazy annotation on ChatClient prevents circular dependency issues that can occur when the ChatClient also depends on MCP components
// - Bidirectional AI Communication: The sampling handler creates a powerful pattern where:
//   - The server (domain expert) can leverage the client's AI capabilities
//   - The client's LLM generates creative content based on server-provided context
//   - This enables sophisticated AI-to-AI interactions beyond simple tool invocation
// This architecture makes the MCP Client a reactive participant in server operations, enabling sophisticated interactions rather than
// just passive tool consumption.
public class McpClientHandlers {

	private static final Logger logger = LoggerFactory.getLogger(McpClientHandlers.class);

	private final ChatClient chatClient;

	public McpClientHandlers(@Lazy ChatClient chatClient) { // Lazy is needed to avoid circular dependency
		this.chatClient = chatClient;
	}

	@McpProgress(clients = "spring-ai-my-weather-server")
	// Progress Handler - Receives real-time progress updates from the server's long-running operations.
	// Triggered when the server calls exchange.progressNotification(...).
	// For example the weather server sends 50% progress when starting sampling, then 100% when complete.
	// Commonly used to display progress bars, update UI status, or log operation progress.
	public void progressHandler(ProgressNotification progressNotification) {
		logger.info("MCP PROGRESS: [{}] progress: {} total: {} message: {}", progressNotification.progressToken(),
				progressNotification.progress(), progressNotification.total(), progressNotification.message());
	}

	@McpLogging(clients = "spring-ai-my-weather-server")
	// Logging Handler - Receives structured log messages from the server for debugging and monitoring.
	// Triggered when the server calls exchange.loggingNotification(...).
	// For example the weather server logs "Call getTemperature Tool with latitude: X and longitude: Y".
	// Used to debug server operations, audit trails, or monitoring dashboards.
	public void loggingHandler(LoggingMessageNotification loggingMessage) {
		logger.info("MCP LOGGING: [{}] {}", loggingMessage.level(), loggingMessage.data());
	}

	@McpSampling(clients = "spring-ai-my-weather-server")
	// Sampling Handler - The Most Powerful Feature.
	// It enables the server to request AI-generated content from the client's LLM.
	// Used for bidirectional AI interactions, creative content generation, dynamic responses.
	// Triggered when the server calls exchange.createMessage(...) with sampling capability check.
	// The execution flow looks like this:
	// - If client supports sampling, requests a poem about the weather
	// - Client handler receives the request and uses its ChatClient to interact with the LLM and generate the poem
	// - Generated poem is returned to the server and incorporated into the final tool response
	public CreateMessageResult samplingHandler(CreateMessageRequest llmRequest) {

		logger.info("MCP SAMPLING: {}", llmRequest);

		String llmResponse = chatClient.prompt()
			.system(llmRequest.systemPrompt())
			.user(((TextContent) llmRequest.messages().getFirst().content()).text())
			.call()
			.content();

		return CreateMessageResult.builder().content(new TextContent(llmResponse)).build();
	};

}
