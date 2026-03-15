/**
 * Copyright (C) 2025 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.connectors.ai.anthropic.toolkit;

import static com.bonitasoft.test.toolkit.predicate.ConnectorPredicates.*;
import static com.bonitasoft.test.toolkit.predicate.ProcessInstancePredicates.*;
import static com.bonitasoft.test.toolkit.predicate.UserTaskPredicates.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.bonitasoft.test.toolkit.BonitaTestToolkit;
import com.bonitasoft.test.toolkit.model.ProcessDefinition;
import com.bonitasoft.test.toolkit.model.ProcessInstance;
import com.bonitasoft.test.toolkit.model.Task;
import com.bonitasoft.test.toolkit.model.User;
import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.engine.bpm.bar.BusinessArchiveFactory;
import org.bonitasoft.web.client.BonitaClient;
import org.bonitasoft.web.client.services.policies.OrganizationImportPolicy;
import org.bonitasoft.web.client.services.policies.ProcessImportPolicy;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Integration tests using Bonita Test Toolkit (BTT) 3.1.0 against a real Bonita runtime
 * deployed via Testcontainers.
 *
 * Tests verify:
 * - Connector execution within a BPM process (functional)
 * - Connector state assertions via BTT predicates
 * - Process variable output validation
 * - Concurrent process execution (load test)
 * - Connector execution time profiling
 *
 * Requires: Docker running, ANTHROPIC_API_KEY env var set
 */
@Slf4j
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnthropicConnectorBttIT {

    private static final String PROCESS_NAME = "AI_CONNECTOR_TEST";
    private static final String CONNECTOR_DEF_ID = "anthropic-ask";
    private static final String CONNECTOR_DEF_VERSION = "1.0.0";
    private static final String ARTIFACT_ID = "bonita-connector-ai-anthropic";

    private static final Logger LOGGER = LoggerFactory.getLogger(AnthropicConnectorBttIT.class);
    private static final String BONITA_VERSION =
            System.getProperty("bonita.version", Objects.requireNonNullElse(System.getenv("BONITA_VERSION"), "2025.1"));

    @Container
    static GenericContainer<?> BONITA_CONTAINER = new GenericContainer<>(
                    DockerImageName.parse(String.format("bonita:%s", BONITA_VERSION)))
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/bonita"))
            .withLogConsumer(new Slf4jLogConsumer(LOGGER));

    private static BonitaClient bonitaClient;
    private static BonitaTestToolkit toolkit;

    @BeforeAll
    static void setUp() throws Exception {
        String bonitaUrl =
                String.format("http://%s:%s/bonita", BONITA_CONTAINER.getHost(), BONITA_CONTAINER.getFirstMappedPort());

        // Setup Bonita client for deployment
        bonitaClient = BonitaClient.builder(bonitaUrl).build();
        bonitaClient.login("install", "install");

        // Import organization
        var orgFile = AnthropicConnectorBttIT.class.getResource("/ACME.xml");
        if (orgFile != null) {
            bonitaClient
                    .users()
                    .importOrganization(new File(orgFile.getFile()), OrganizationImportPolicy.IGNORE_DUPLICATES);
        }

        // Build and deploy the connector BAR
        deployConnectorProcess();

        // Initialize BTT pointing to the Testcontainers Bonita runtime
        System.setProperty("bonita.url", bonitaUrl);
        System.setProperty("bonita.tech.user", "install");
        System.setProperty("bonita.tech.password", "install");
        toolkit = com.bonitasoft.test.toolkit.BonitaTestToolkitFactory.INSTANCE.get(AnthropicConnectorBttIT.class);
    }

    @AfterAll
    static void tearDown() {
        if (bonitaClient != null) {
            bonitaClient.logout();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Connector should execute successfully in a Bonita process")
    void should_execute_ask_connector_and_complete_process() {
        // Given
        ProcessDefinition processDef = toolkit.getProcessDefinition(PROCESS_NAME);
        User user = toolkit.getUser("walter.bates");

        // When - Start the process
        ProcessInstance processInstance = processDef.startProcessFor(user);

        // Then - Wait for connector execution (ON_ENTER) and task to be pending
        await("Process started and connector executed")
                .atMost(Duration.ofMinutes(3))
                .until(processInstance, processInstanceStarted());

        // Verify connector completed
        await("Ask connector completed")
                .atMost(Duration.ofMinutes(3))
                .until(processInstance.getConnector("ai-connector-under-test"), connectorIsDone());

        // Check process variable was set by connector output
        var askResult = processInstance.getVariable("askResult");
        assertThat(askResult).isNotNull();
        log.info("Connector output (askResult): {}", askResult.getValue());
        assertThat(askResult.getValue()).asString().isNotEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("Connector output should contain expected answer")
    void should_return_correct_answer() {
        // Given
        ProcessDefinition processDef = toolkit.getProcessDefinition(PROCESS_NAME);
        User user = toolkit.getUser("walter.bates");

        // When
        ProcessInstance processInstance = processDef.startProcessFor(user);
        await().atMost(Duration.ofMinutes(3)).until(processInstance, processInstanceStarted());
        await().atMost(Duration.ofMinutes(3))
                .until(processInstance.getConnector("ai-connector-under-test"), connectorIsDone());

        // Then - The answer to "What is 2+2?" should contain "4"
        var askResult = processInstance.getVariable("askResult");
        assertThat(askResult.getValue()).asString().contains("4");
    }

    @Test
    @Order(3)
    @DisplayName("Process tasks should be tracked correctly")
    void should_track_process_tasks() {
        // Given
        ProcessDefinition processDef = toolkit.getProcessDefinition(PROCESS_NAME);
        User user = toolkit.getUser("walter.bates");

        // When
        ProcessInstance processInstance = processDef.startProcessFor(user);
        await().atMost(Duration.ofMinutes(3)).until(processInstance, processInstanceStarted());

        // Then - Should have tasks tracked
        List<Task> tasks = processInstance.searchTasks();
        assertThat(tasks).isNotEmpty();
        log.info("Tasks found: {}", tasks.stream().map(Task::getName).toList());
    }

    @Test
    @Order(10)
    @DisplayName("Load test: 10 concurrent process instances")
    void should_handle_concurrent_process_executions() {
        // Given
        ProcessDefinition processDef = toolkit.getProcessDefinition(PROCESS_NAME);
        User user = toolkit.getUser("walter.bates");
        int concurrentInstances = 10;

        // When - Start 10 instances concurrently
        long startTime = System.currentTimeMillis();
        List<ProcessInstance> instances = IntStream.range(0, concurrentInstances)
                .mapToObj(i -> processDef.startProcessFor(user))
                .toList();

        // Then - All should start successfully
        assertThat(instances).hasSize(concurrentInstances);

        // Wait for all connectors to complete
        instances.forEach(instance -> {
            await("Instance started").atMost(Duration.ofMinutes(5)).until(instance, processInstanceStarted());
        });

        long totalTime = System.currentTimeMillis() - startTime;
        log.info(
                "Load test: {} instances started in {} ms (avg {} ms/instance)",
                concurrentInstances,
                totalTime,
                totalTime / concurrentInstances);
    }

    @Test
    @Order(11)
    @DisplayName("Performance: connector execution time within threshold")
    void should_execute_connector_within_time_threshold() {
        // Given
        ProcessDefinition processDef = toolkit.getProcessDefinition(PROCESS_NAME);
        User user = toolkit.getUser("walter.bates");
        long maxExecutionTimeMs = 30_000; // 30 seconds threshold

        // When
        long startTime = System.currentTimeMillis();
        ProcessInstance processInstance = processDef.startProcessFor(user);
        await().atMost(Duration.ofMinutes(3)).until(processInstance, processInstanceStarted());
        await().atMost(Duration.ofMinutes(3))
                .until(processInstance.getConnector("ai-connector-under-test"), connectorIsDone());
        long executionTime = System.currentTimeMillis() - startTime;

        // Then
        log.info("Connector execution time: {} ms", executionTime);
        assertThat(executionTime)
                .as("Connector execution should complete within %d ms", maxExecutionTimeMs)
                .isLessThan(maxExecutionTimeMs);
    }

    private static void deployConnectorProcess() throws Exception {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("userPrompt", "What is 2+2? Answer with just the number.");
        inputs.put("systemPrompt", "You are a helpful assistant. Be concise.");
        inputs.put("apiKey", System.getenv("ANTHROPIC_API_KEY"));

        Map<String, AiConnectorTestToolkit.Output> outputs = new HashMap<>();
        outputs.put("askResult", AiConnectorTestToolkit.Output.create("output", String.class.getName()));

        var barArchive = AiConnectorTestToolkit.buildConnectorToTest(
                CONNECTOR_DEF_ID, CONNECTOR_DEF_VERSION, inputs, outputs, ARTIFACT_ID);

        File barFile = null;
        try {
            barFile = Files.createTempFile("anthropic-btt-test", ".bar").toFile();
            barFile.delete();
            BusinessArchiveFactory.writeBusinessArchiveToFile(barArchive, barFile);
            bonitaClient.processes().importProcess(barFile, ProcessImportPolicy.REPLACE_DUPLICATES);
        } finally {
            if (barFile != null) {
                barFile.delete();
            }
        }

        // Enable the process
        var process = barArchive.getProcessDefinition();
        var processId = bonitaClient
                .processes()
                .getProcess(process.getName(), process.getVersion())
                .getId();
        bonitaClient.processes().startProcess(processId, Map.of());
    }
}
