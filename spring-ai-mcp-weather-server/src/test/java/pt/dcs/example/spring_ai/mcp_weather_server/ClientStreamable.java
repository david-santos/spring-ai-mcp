package pt.dcs.example.spring_ai.mcp_weather_server;

import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;

public class ClientStreamable {

	public static void main(String[] args) {
		var transport = HttpClientStreamableHttpTransport.builder("http://localhost:8080").build();
		new SampleClient(transport).run();
	}

}
