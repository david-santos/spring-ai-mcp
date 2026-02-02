package pt.dcs.example.spring_ai.mcp_weather_server;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.CreateMessageRequest;
import io.modelcontextprotocol.spec.McpSchema.CreateMessageResult;
import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;
import io.modelcontextprotocol.spec.McpSchema.ModelPreferences;
import io.modelcontextprotocol.spec.McpSchema.ProgressNotification;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.SamplingMessage;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import org.springaicommunity.mcp.annotation.McpProgressToken;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WeatherService {

	private final RestClient restClient = RestClient.create();

	/**
	 * The response format from the Open-Meteo API
	 */
	public record WeatherResponse(Current current) {
		public record Current(
				LocalDateTime time, // ISO8601 date string (e.g. "2026-02-02T15:45") GMT default timezone
				int interval, // Default 900 seconds (15 minutes)
				double temperature_2m, // °C
				double apparent_temperature, // °C
				int cloud_cover, // %
				double wind_speed_10m, // km/h
				double wind_gusts_10m, // km/h
				int precipitation_probability, // %
				int weather_code, // WMO code
				boolean is_day // 1 if the current time step has daylight, 0 at night
		) {}
	}

	@McpTool(description = "Get the weather forecast for a specific location")
	public String getWeatherForecast(McpSyncServerExchange exchange, // The exchange parameter provides access to server-client communication capabilities. It allows the server to send notifications and make requests back to the client.
			@McpToolParam(description = "The location latitude") String latitude,
			@McpToolParam(description = "The location longitude") String longitude,
			@McpProgressToken Object progressToken) { // The progressToken parameter enables progress tracking. The client provides this token, and the server uses it to send progress updates.

		exchange.loggingNotification(LoggingMessageNotification.builder() // Sends structured log messages to the client for debugging and monitoring purposes.
			.level(LoggingLevel.DEBUG)
			.data("Call getWeatherForecast Tool with latitude: " + latitude + " and longitude: " + longitude)
			.build());

		// 0% progress
		if (progressToken != null) {
			exchange.progressNotification(
				new ProgressNotification(progressToken, 0.0, 1.0, "Retrieving weather forecast"));
		}

		WeatherResponse weatherResponse = restClient.get()
			.uri("https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&current=temperature_2m,apparent_temperature,cloud_cover,wind_speed_10m,wind_gusts_10m,precipitation_probability,weather_code,is_day",
					latitude, longitude)
			.retrieve()
			.body(WeatherResponse.class);

		String epicPoem = "MCP client doesn't provide sampling capability.";

		if (exchange.getClientCapabilities().sampling() != null) {

			// 50% progress
			if (progressToken != null) {
				exchange.progressNotification(new ProgressNotification(progressToken, 0.5, 1.0, "Start sampling")); // reports operation progress (50% in this case) to the client with a descriptive message.
			}

			String samplingMessage = """
					Write an epic poem about the following forecast using a Shakespearean style.
					Forecast:
					  - Temperature is %s degrees Celsius.
					  - Apparent temperature (feels like) is %s degrees Celsius.
					  - Total cloud cover %s percent.
					  - Wind speed 10 meters above ground is %s km/h with gusts at %s km/h.
					  - Precipitation probability is %s percent.
					  - Is day: %s.
					""".formatted(
							weatherResponse.current().temperature_2m(),
							weatherResponse.current().apparent_temperature(),
							weatherResponse.current().cloud_cover(),
							weatherResponse.current().wind_speed_10m(),
							weatherResponse.current().wind_gusts_10m(),
							weatherResponse.current().precipitation_probability(),
							weatherResponse.current().is_day());

			CreateMessageResult samplingResponse = exchange.createMessage(CreateMessageRequest.builder()
				.systemPrompt("You are a poet!")
				.messages(List.of(new SamplingMessage(Role.USER, new TextContent(samplingMessage))))
				.maxTokens(256)
				.modelPreferences(ModelPreferences.builder().addHint("ollama").build())
				.build()); // The most powerful feature - the server can request the client's LLM to generate content.

			epicPoem = ((TextContent) samplingResponse.content()).text();

		}

		// 100% progress
		if (progressToken != null) {
			exchange.progressNotification(new ProgressNotification(progressToken, 1.0, 1.0, "Task completed"));
		}

		return """
				Forecast:
				  - Temperature is %s degrees Celsius.
				  - Apparent temperature (feels like) is %s degrees Celsius.
				  - Total cloud cover %s percent.
				  - Wind speed 10 meters above ground is %s km/h with gusts at %s km/h.
				  - Precipitation probability is %s percent.
				  - Is day: %s.
				Weather Poem: %s
				""".formatted(
						weatherResponse.current().temperature_2m(),
						weatherResponse.current().apparent_temperature(),
						weatherResponse.current().cloud_cover(),
						weatherResponse.current().wind_speed_10m(),
						weatherResponse.current().wind_gusts_10m(),
						weatherResponse.current().precipitation_probability(),
						weatherResponse.current().is_day(),
						epicPoem);
	}

}
