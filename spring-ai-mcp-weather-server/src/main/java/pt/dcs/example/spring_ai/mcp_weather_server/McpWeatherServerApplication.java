package pt.dcs.example.spring_ai.mcp_weather_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpWeatherServerApplication {

	private final Logger logger = LoggerFactory.getLogger(McpWeatherServerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(McpWeatherServerApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(ChatClient.Builder builder, EmbeddingModel embeddingModel) {

		var chatClient = builder.build();

		return _ -> {
			// Chat
			var response = chatClient
					.prompt("Tell me a joke")
					.call()
					.chatResponse();

			logger.info("Thinking: " + response.getResult().getMetadata().get("thinking"));
			logger.info("Answer: " + response.getResult().getOutput().getText());

			// Embedding
			var embedding = embeddingModel.embed("The World is Big and Salvation Lurks Around the Corner");
			logger.info("Embedding dimensions: " + embedding.length);
		};
	}
}
