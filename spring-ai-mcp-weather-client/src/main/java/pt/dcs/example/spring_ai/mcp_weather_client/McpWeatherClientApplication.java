package pt.dcs.example.spring_ai.mcp_weather_client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.Random;

@SpringBootApplication
public class McpWeatherClientApplication {

	private final Logger logger = LoggerFactory.getLogger(McpWeatherClientApplication.class);

	private static final String USER_PROMPT = """
			Check the weather in Oeiras (Portugal) now and show the creative response!
			Please incorporate all creative responses from all LLM providers.

			Please use the weather poem (returned from the tool) to find a publisher online.
			List the top 3 most relevant publishers for this poem.
			""";

	public static void main(String[] args) {
		SpringApplication.run(McpWeatherClientApplication.class, args).close();
	}

	@Bean
	public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
		return chatClientBuilder.build();
	}

	@Bean
	public CommandLineRunner predefinedQuestions(ChatClient chatClient, ToolCallbackProvider mcpToolProvider) {
		return args -> logger.info(chatClient.prompt(USER_PROMPT)
				.toolContext(Map.of("progressToken", "token-" + new Random().nextInt()))
				.toolCallbacks(mcpToolProvider)
				.call()
				.content());
	}

}
