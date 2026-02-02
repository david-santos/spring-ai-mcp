# spring-ai-mcp

This project demonstrates the use Spring AI to build MCP servers and clients.

This was the third step in my quest to gain knowledge about building AI applications, with RAG, MCP and agentic workflows, using Spring AI.

This example was heavily based on [this](https://spring.io/blog/2025/09/16/spring-ai-mcp-intro-blog) Spring blog post.
It builds on top of [spring-ai-ollama-hf-gguf](https://github.com/david-santos/spring-ai-ollama-hf-gguf).
Everything runs locally (application, models and MCP server).

## Overview

This example showcases:
- [MCP Weather Server](https://github.com/david-santos/spring-ai-mcp/tree/main/spring-ai-mcp-weather-server): A Spring Boot server that exposes weather forecast capabilities as MCP tools. Makes use of [Open-Meteo Weather Forecast API](https://open-meteo.com/en/docs).
- [MCP Weather Client](https://github.com/david-santos/spring-ai-mcp/tree/main/spring-ai-mcp-weather-client): A Spring Boot AI application that connects to MCP servers and uses an LLM (Ollama with Hugging Face GGUF models).

## Architecture

```plain
┌────────────────────────────────────────────────────────────┐
│                    MCP Weather Client                      │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────────┐   │
│  │  ChatClient │──│ MCP Client   │──│ Weather Server   │   │
│  │  (Claude)   │  │  Transport   │  │ (Streamable-HTTP)│   │
│  └─────────────┘  └──────────────┘  └──────────────────┘   │
│                           │                                │
│                   ┌──────────────┐                         │
│                   │ MCP Client   │                         │
│                   │  Transport   │                         │
│                   └──────────────┘                         │
└────────────────────────────────────────────────────────────┘
```

## Features Demonstrated

### MCP Server Features
- **Tools**: Weather forecast retrieval by coordinates
- **Logging**: Structured log messages sent to clients (advanced server feature)
- **Progress Tracking**: Real-time progress updates for operations (advanced server feature)
- **Sampling**: Request AI-generated content from the client's LLM (advanced server feature)

### MCP Client Features
- ~~**Multi-Server Connection**: Connect to multiple MCP servers simultaneously~~
- **Transport Support**: Both Streamable-HTTP and STDIO transports
- **Handler Annotations**: Process server notifications and requests
- **LLM Integration**: Seamless integration with Ollama

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
```

```bash
./mvnw -pl spring-ai-mcp-weather-client clean package
```

## Run

Run each module from the root:

```bash
./mvnw -pl spring-ai-mcp-weather-server spring-boot:run
```

```bash
./mvnw -pl spring-ai-mcp-weather-client spring-boot:run
```

> The Spring Boot Maven plugin is configured with `--enable-native-access=ALL-UNNAMED` so Netty’s native DNS resolver (used on macOS) can load without the Java 21+ restricted-method warning. If you run the app with `java -jar` or from an IDE, add that JVM argument so the warning does not appear.

## Test

## ClientStreamable & ClientStdio

In the `test` folder there's 2 runnable classes that make use the MCP Java SDK client to programmatically connect to the server:
- `ClientStreamable` - Connects via the `Streamable HTTP` Transport Type.
- `ClientStdio` - Connects via the `STDIO` Transport Type.

## MCP Inspector

The [MCP Inspector](https://modelcontextprotocol.io/legacy/tools/inspector) is an interactive developer tool for testing and debugging MCP servers. To start the inspector run:
```bash
npx @modelcontextprotocol/inspector
```
In the browser UI, set the Transport Type to Streamable HTTP and the URL to http://localhost:8080/mcp. Click Connect to establish the connection. Then list the tools and run `getWeatherForecast`.


## References
- [Spring AI Docs](https://docs.spring.io/spring-ai/reference/2.0/index.html):
    - [Model Context Protocol (MCP)](https://docs.spring.io/spring-ai/reference/2.0/api/mcp/mcp-overview.html)
- [Spring AI MCP Examples](https://github.com/spring-projects/spring-ai-examples/tree/main/model-context-protocol/weather)
