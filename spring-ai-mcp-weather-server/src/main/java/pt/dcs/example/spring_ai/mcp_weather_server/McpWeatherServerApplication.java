package pt.dcs.example.spring_ai.mcp_weather_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class McpWeatherServerApplication {

	private final Logger logger = LoggerFactory.getLogger(McpWeatherServerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(McpWeatherServerApplication.class, args);
	}

}
