# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This repository contains AI connectors for the Bonita BPM platform, enabling integration with OpenAI and MistralAI chat models. The connectors allow sending prompts and documents to AI providers and retrieving generated outputs within Bonita processes.

**Technology Stack:**
- Java 17
- Maven (multi-module project)
- Bonita Runtime 10.2.0
- LangChain4j 1.0.0-beta3
- Lombok for code generation

**Maven Module Structure:**
- `bonita-connector-ai-core`: Core abstractions and business logic shared across all AI providers
- `bonita-connector-ai-openai`: OpenAI-specific connector implementations
- `bonita-connector-ai-mistral`: MistralAI-specific connector implementations

## Build and Test Commands

### Building the Project
```bash
# Build all modules (skip tests)
./mvnw clean package

# Build with tests
./mvnw clean verify

# Build a specific module
./mvnw clean package -pl bonita-connector-ai-openai -am
```

### Running Tests
```bash
# Run unit tests only
./mvnw test

# Run integration tests (requires docker compose services)
docker compose up -d
./mvnw verify -PITs

# Run tests for a specific module
./mvnw test -pl bonita-connector-ai-core

# Run a single test class
./mvnw test -Dtest=AskAiConnectorTest -pl bonita-connector-ai-core

# Run a single test method
./mvnw test -Dtest=AskAiConnectorTest#testAskWithDocument -pl bonita-connector-ai-core
```

### Code Quality and Formatting
```bash
# Check code formatting (Spotless with Palantir Java Format)
./mvnw spotless:check

# Apply code formatting
./mvnw spotless:apply

# The build automatically runs spotless:check during compile phase
```

### Integration Test Environment
The project uses Ollama as a local replacement for AI providers during integration tests:
```bash
# Start Ollama container
docker compose up -d

# Download a model (e.g., llama3.2)
docker compose exec ollama bash -c 'ollama pull llama3.2'

# Check logs
docker compose logs -f ollama

# Stop services
docker compose down
```

Ollama API is available at `http://localhost:11434/v1` when running.

## Architecture

### Connector Hierarchy

The codebase follows a template method pattern with abstract base classes:

1. **Core Layer** (`bonita-connector-ai-core`):
   - `AiConnector`: Base abstract connector that extends Bonita's `AbstractConnector`
     - Handles configuration parsing (API key, URL, model name, timeout, temperature)
     - Manages document retrieval from Bonita process instances
     - Defines lifecycle: `validateInputParameters()` → `executeBusinessLogic()`

   - Specialized abstract connectors for each operation type:
     - `AskAiConnector`: Send user prompts (with optional documents and JSON schema)
     - `ExtractAiConnector`: Extract fields from documents
     - `ClassifyAiConnector`: Classify documents into categories

   - `AiChat` interface and `AbstractAiChat`: Define AI provider integration contract
   - Configuration classes: `AiConfiguration`, `AskConfiguration`, `ExtractConfiguration`, `ClassifyConfiguration`

2. **Provider Layer** (`bonita-connector-ai-openai`, `bonita-connector-ai-mistral`):
   - Each provider has three connector implementations:
     - `OpenAiAskConnector` / `MistralAiAskConnector`
     - `OpenAiExtractDataConnector` / `MistralAiExtractDataConnector`
     - `OpenAiClassifyConnector` / `MistralAiClassifyConnector`

   - Each provider implements `AiChat` using LangChain4j provider-specific APIs:
     - `OpenAiChat` uses `langchain4j-open-ai`
     - `MistralAiChat` uses `langchain4j-mistral-ai`

### Document Handling

The connectors can process Bonita process documents:
- `BonitaDocumentSource`: Custom LangChain4j document source for Bonita documents
- `UserDocumentSource`: Wraps user-provided documents
- Apache Tika integration for parsing various document formats (PDF, DOCX, etc.)

### Configuration Priority

API keys are resolved in this order:
1. Environment variable `AI_API_KEY`
2. JVM system property `-DAI_API_KEY=xxx`
3. Connector input parameter `apiKey`
4. Default value: `"changeMe"`

### Connector Definition Files

Each connector has two definition files in `src/main/resources-filtered/`:
- `.def` file: Connector definition (inputs, outputs, categories)
- `.impl` file: Implementation mapping (connector class, dependencies)
- `.properties` file: I18n labels

Maven property placeholders are filtered during build.

## Key Development Patterns

### Adding a New AI Provider

1. Create a new module `bonita-connector-ai-{provider}` following the existing structure
2. Add provider-specific LangChain4j dependency
3. Create `{Provider}AiChat` implementing the `AiChat` interface
4. Create three connector classes extending abstract connectors from core:
   - `{Provider}AiAskConnector`
   - `{Provider}AiExtractDataConnector`
   - `{Provider}AiClassifyConnector`
5. Create connector definition files (`.def`, `.impl`, `.properties`)
6. Configure maven properties for connector IDs and versions in `pom.xml`
7. Add Groovy script execution for dependency resolution

### Testing Strategy

- **Unit tests**: Test connector logic with mocked dependencies
- **Integration tests** (`*IT.java`): Test against real AI providers (Ollama for local testing)
  - Only run when `-PITs` profile is activated
  - Require docker compose services to be running
- **Test utilities**: The core module produces a test-jar with shared test utilities

### JSON Schema Support

All connectors support structured JSON output via JSON schema:
- Pass a JSON schema via `outputJsonSchema` parameter
- The `required` array in the schema determines which fields appear in the response
- LangChain4j handles the structured output generation

## Commit Message Format

Use conventional commits for changelog generation:
```
type(category): description [flags]
```

Types: `breaking`, `build`, `ci`, `chore`, `docs`, `feat`, `fix`, `other`, `perf`, `refactor`, `revert`, `style`, `test`

## Release Process

The project follows GitFlow branching strategy:
- Main branch: `main`
- Development branch: `develop`
- Feature branches: `feat/*`
- Release branches: `release/*`
- Hotfix branches: `hotfix/*`

Releases are managed via GitHub Actions workflows (manual triggers):
1. Run "Release" workflow to invoke gitflow-maven-plugin (merges, version updates, tagging)
2. Run "Publication" workflow to deploy artifacts to Maven Central
3. Manually create GitHub release associated with the tag
4. Update the Bonita marketplace repository with the new version

## Important Notes

- Never skip GPG signing or use `--no-verify` flags unless explicitly working on release tasks
- The project uses Spotless for code formatting (Palantir Java Format) - all code must pass formatting checks
- License headers are automatically managed by Spotless (see `src/format/header.txt`)
- Integration tests require environment variable or system property for API keys
- Connector packaging creates both JAR and ZIP archives in `target/` directories
