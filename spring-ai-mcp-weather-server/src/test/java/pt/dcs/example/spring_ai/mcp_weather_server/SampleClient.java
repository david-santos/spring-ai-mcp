package pt.dcs.example.spring_ai.mcp_weather_server;

import java.util.Map;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.CreateMessageResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;

import org.springframework.ai.chat.model.ToolContext;

public class SampleClient {

	private final McpClientTransport transport;

	public SampleClient(McpClientTransport transport) {
		this.transport = transport;
	}

	public void run() {

		var client = McpClient.sync(this.transport).sampling(request -> {
			System.out.println("Received a new message: " + request);
			return CreateMessageResult.builder().content(new McpSchema.TextContent("Bla bla bla")).build();
		}).build();

		client.initialize();

		client.ping();

		// List and demonstrate tools
		ListToolsResult toolsList = client.listTools();
		System.out.println("Available Tools = " + toolsList);

		CallToolResult weatherForcastResult = client.callTool(new CallToolRequest("getWeatherForecast",
				Map.of("latitude", "38.6875", "longitude", "-9.3125", "toolContext", new ToolContext(Map.of()))));
		System.out.println("Weather Forcast: " + weatherForcastResult);

		client.closeGracefully();

	}

}
