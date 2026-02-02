# spring-ai-mcp

This project demonstrates the use Spring AI to build MCP servers and clients.

This was the third step in my quest to gain knowledge about building AI applications, with RAG, MCP and agentic workflows, using Spring AI.

## Prerequisites

- Java 25 (installed on your system)
- [Ollama](https://ollama.com/download) (installed and running on your system)
- Maven 3.9.12 (pulled in automatically via `./mvnw`)
- Spring AI 2.0.0-M2 (pulled in automatically)

## Build

Build the whole project (all modules):

```bash
./mvnw clean package
```

Build each module from the root:

```bash
./mvnw -pl spring-ai-mcp-weather-server clean package
./mvnw -pl spring-ai-mcp-weather-client clean package
```

## Run

Run each module from the root:

```bash
./mvnw -pl spring-ai-mcp-weather-server spring-boot:run
./mvnw -pl spring-ai-mcp-weather-client spring-boot:run
```

> The Spring Boot Maven plugin is configured with `--enable-native-access=ALL-UNNAMED` so Netty’s native DNS resolver (used on macOS) can load without the Java 21+ restricted-method warning. If you run the app with `java -jar` or from an IDE, add that JVM argument so the warning does not appear.

## References
- []()
