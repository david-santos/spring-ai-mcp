# spring-ai-mcp

This project demonstrates the use of Spring AI with Ollama, leveraging Hugging Face GGUF models for both chat completion and embedding tasks.

This was the third step in my quest to gain knowledge about building AI applications, with RAG, MCP and agentic workflows, using Spring AI.

## Prerequisites

- Java 25 (installed on your system)
- [Ollama](https://ollama.com/download) (installed and running on your system)
- Maven 3.9.12 (pulled in automatically via `./mvnw`)
- Spring AI 2.0.0-M2 (pulled in automatically)

## Run

```bash
./mvnw spring-boot:run
```

> The Spring Boot Maven plugin is configured with `--enable-native-access=ALL-UNNAMED` so Netty’s native DNS resolver (used on macOS) can load without the Java 21+ restricted-method warning. If you run the app with `java -jar` or from an IDE, add that JVM argument so the warning does not appear.

## References
- []()
