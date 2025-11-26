# LangChain4J Agentic – Car Rental Service

A Quarkus-based Java service that demonstrates an agentic workflow using LangChain4J to assist a car rental company with post-rental operations (e.g., determining whether a car wash is required based on feedback) and simple fleet data access via REST.

This repository currently contains a single runnable module: `car-rental-service`.


## Overview

- Stack
  - Language: Java 21
  - Framework: Quarkus 3.28.x (RESTEasy Reactive, Hibernate ORM with Panache)
  - AI/LLM: Quarkus LangChain4J (agentic + OpenAI provider)
  - Database: PostgreSQL driver present; dev uses Quarkus Dev Services (Docker) unless configured
  - Build tool: Maven (with Maven Wrapper `mvnw`)
- Key features
  - Agent (`CarWashAgent`) orchestrates whether car wash services are needed based on rental/cleaning feedback.
  - REST endpoints to submit returns and to list/query cars.
  - Seed data via `import.sql` in dev/test.


## Requirements

- Java 21 (JDK 21)
- Maven Wrapper (provided) or Maven 3.9+
- Docker (optional but recommended for Quarkus Dev Services to auto-provision PostgreSQL in dev mode)
- OpenAI API key for the LLM integration

Environment variable(s):
- `OPENAI_API_KEY` – required for running agentic flows via OpenAI provider


## Setup

1. Clone the repository
   ```bash
   git clone <your-fork-or-clone-url>
   cd LangChain4J-agentic
   ```

2. Export your OpenAI API key (example for bash/zsh)
   ```bash
   export OPENAI_API_KEY=sk-... # replace with your key
   ```
   - Windows PowerShell:
     ```powershell
     $env:OPENAI_API_KEY="sk-..."
     ```

3. (Optional) Ensure Docker is running if you want Quarkus Dev Services to auto-start PostgreSQL for you in dev mode.


## Run (Dev Mode)

Run the service in Quarkus dev mode (hot reload):
```bash
cd car-rental-service
./mvnw quarkus:dev
```
- The HTTP server listens on port `8080` by default.
- Static UI assets (if any) are served from `src/main/resources/META-INF/resources/` at `/`.


## Build

- JVM build:
  ```bash
  cd car-rental-service
  ./mvnw package
  ```
  The runnable JAR will be produced under `car-rental-service/target/`.

- Native build (requires GraalVM Native Image tooling; may be done in a container by Quarkus):
  ```bash
  cd car-rental-service
  ./mvnw package -Dnative
  # or explicit profile
  ./mvnw package -Pnative
  ```


## Configuration

Main application config: `car-rental-service/src/main/resources/application.properties`

Relevant entries:
- HTTP and logging:
  - `quarkus.http.port=8080`
  - `%dev.quarkus.log.console.level=DEBUG`
- App name:
  - `quarkus.application.name=car-management`
- LangChain4J / OpenAI:
  - `quarkus.langchain4j.chat-model.provider=openai`
  - `quarkus.langchain4j.openai.api-key=${OPENAI_API_KEY}`
  - `quarkus.langchain4j.openai.chat-model.model-name=gpt-4o`
  - `quarkus.langchain4j.openai.chat-model.temperature=0`

Database:
- The project includes `quarkus-hibernate-orm-panache` and `quarkus-jdbc-postgresql`.
- No explicit datasource properties are checked in; in dev mode Quarkus can use Dev Services to start a PostgreSQL container automatically if Docker is available.
- `src/main/resources/import.sql` seeds demo data.

TODOs:
- Add explicit datasource configuration for non-dev environments (e.g., `quarkus.datasource.jdbc.url`, `quarkus.datasource.username`, `quarkus.datasource.password`).


## REST API

Base URL: `http://localhost:8080`

- List all cars
  - `GET /cars`
  - Response: JSON array of cars (Panache entity `CarInfo`).

- Get car by ID
  - `GET /cars/{id}`
  - Response: 200 with a single car JSON, or 404 if not found.

- Process rental return (may trigger agent)
  - `POST /car-management/rental-return/{carNumber}`
  - Query parameters: `rentalFeedback` (optional)
  - Response: plain text result string.
  - Example:
    ```bash
    curl -X POST "http://localhost:8080/car-management/rental-return/1?rentalFeedback=Car%20was%20returned%20clean"
    ```

- Process car wash return (may trigger agent)
  - `POST /car-management/car-wash-return/{carNumber}`
  - Query parameters: `carWashFeedback` (optional)
  - Response: plain text result string.
  - Example:
    ```bash
    curl -X POST "http://localhost:8080/car-management/car-wash-return/1?carWashFeedback=Smudges%20on%20rear%20window"
    ```

Notes:
- Endpoints are implemented in `CarManagementResource` and `CarResource`.
- The agent interface is `CarWashAgent`; it uses a `CarWashTool` via `@ToolBox` to request concrete washing operations.


## Scripts and Common Maven Commands

From `car-rental-service` directory:
- `./mvnw quarkus:dev` – start dev mode
- `./mvnw package` – build JAR
- `./mvnw test` – run unit tests (if/when present)
- `./mvnw clean` – clean build outputs
- `./mvnw package -Dnative` – build a native executable (requires native toolchain or containerized build)


## Tests

Test dependencies are configured (`quarkus-junit5`, `rest-assured`), but no test classes are currently present in the repository tree.

- Run tests (once tests exist):
  ```bash
  cd car-rental-service
  ./mvnw test
  ```

TODOs:
- Add unit and integration tests for resources, services, and the agent/tooling integration.


## Project Structure

```
LangChain4J-agentic/
├─ car-rental-service/
│  ├─ pom.xml
│  ├─ mvnw, mvnw.cmd
│  ├─ src/main/java/com/carmanagement/
│  │  ├─ agentic/
│  │  │  ├─ agents/CarWashAgent.java
│  │  │  └─ tools/CarWashTool.java
│  │  ├─ model/
│  │  │  ├─ CarInfo.java
│  │  │  └─ CarStatus.java
│  │  ├─ resource/
│  │  │  ├─ CarManagementResource.java
│  │  │  └─ CarResource.java
│  │  └─ service/
│  │     └─ CarManagementService.java
│  ├─ src/main/resources/
│  │  ├─ META-INF/resources/ (static web assets)
│  │  │  ├─ index.html, css/, js/
│  │  ├─ application.properties
│  │  └─ import.sql
│  └─ target/ (build outputs)
└─ LangChain4J-agentic.iml
```

Note: Another directory `disposition-a2a-agent/` appears to only contain `target/` build outputs in this snapshot and no source set; it is not part of the runnable code in this repository state.


## License

TODO: Add a license (e.g., Apache-2.0, MIT). Include a `LICENSE` file at the repository root.


## Notes & Future Work

- Dockerfile and containerization are not present; consider adding for runtime packaging.
- CI pipeline (e.g., GitHub Actions) is not included; consider adding for build/test.
- Production database configuration is not defined; add `quarkus.datasource.*` settings and secrets management.
- Observability (health, metrics, OpenAPI): consider adding Quarkus extensions (`smallrye-health`, `micrometer`, `smallrye-openapi`).
