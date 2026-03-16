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
package org.bonitasoft.connectors.ai.azure.toolkit;

import static com.bonitasoft.test.toolkit.predicate.ConnectorPredicates.*;
import static com.bonitasoft.test.toolkit.predicate.ProcessInstancePredicates.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.bonitasoft.test.toolkit.BonitaTestToolkit;
import com.bonitasoft.test.toolkit.BonitaTestToolkitFactory;
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
import org.bonitasoft.web.client.services.policies.ProcessImportPolicy;
import org.junit.jupiter.api.*;

/**
 * Integration tests using BTT against Bonita Studio runtime (localhost:8080).
 *
 * <p>Automatically builds a BAR with the Azure OpenAI connector, deploys it to the Studio,
 * and runs the tests using BTT.
 *
 * <p>Run with:
 * <pre>{@code
 * mvn verify -PITs -Dbonita.target=studio
 * }</pre>
 *
 * <p>Requires: Bonita Studio running at localhost:8080, AZURE_OPENAI_API_KEY and AZURE_OPENAI_ENDPOINT env vars set
 */
@Slf4j
@Tag("btt")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AzureConnectorStudioIT {

    private static final String PROCESS_NAME = "AI_CONNECTOR_TEST";
    private static final String CONNECTOR_DEF_ID = "azure-ask";
    private static final String CONNECTOR_DEF_VERSION = "1.0.0";
    private static final String ARTIFACT_ID = "bonita-connector-ai-azure";

    private static final String DEFAULT_STUDIO_URL = "http://localhost:8080/bonita";
    private static final String DEFAULT_TECH_USER = "install";
    private static final String DEFAULT_TECH_PASSWORD = "install";

    private static String studioUrl() {
        String url = System.getProperty("bonita.url", "");
        return url.isEmpty() || url.startsWith("$") ? DEFAULT_STUDIO_URL : url;
    }

    private static String techUser() {
        String user = System.getProperty("bonita.tech.user", "");
        return user.isEmpty() || user.startsWith("$") ? DEFAULT_TECH_USER : user;
    }

    private static String techPassword() {
        String pass = System.getProperty("bonita.tech.password", "");
        return pass.isEmpty() || pass.startsWith("$") ? DEFAULT_TECH_PASSWORD : pass;
    }

    private static BonitaClient bonitaClient;
    private static BonitaTestToolkit toolkit;

    @BeforeAll
    static void setUp() throws Exception {
        Assumptions.assumeTrue(System.getenv("AZURE_OPENAI_API_KEY") != null, "AZURE_OPENAI_API_KEY env var required");
        Assumptions.assumeTrue(
                System.getenv("AZURE_OPENAI_ENDPOINT") != null, "AZURE_OPENAI_ENDPOINT env var required");
        // Verify Studio is reachable
        String url = studioUrl();
        String user = techUser();
        String pass = techPassword();
        try {
            bonitaClient = BonitaClient.builder(url).build();
            bonitaClient.login(user, pass);
        } catch (Exception e) {
            Assumptions.abort("Studio not reachable at " + url + ": " + e.getMessage());
            return;
        }

        log.info("Deploying connector process to Studio at {}", url);
        deployConnectorProcess(bonitaClient);
        log.info("Connector process deployed successfully");

        // Initialize BTT
        System.setProperty("bonita.url", url);
        System.setProperty("bonita.tech.user", user);
        System.setProperty("bonita.tech.password", pass);
        toolkit = BonitaTestToolkitFactory.INSTANCE.get(AzureConnectorStudioIT.class);
    }

    @AfterAll
    static void tearDown() {
        if (bonitaClient != null) {
            bonitaClient.logout();
        }
    }

    @Test
    @Order(1)
    @DisplayName("[Studio] Connector should execute and produce output")
    void should_execute_ask_connector_in_studio() {
        ProcessDefinition processDef = toolkit.getProcessDefinition(PROCESS_NAME);
        User user = toolkit.getUser("walter.bates");

        ProcessInstance processInstance = processDef.startProcessFor(user);

        await("Process started").atMost(Duration.ofMinutes(3)).until(processInstance, processInstanceStarted());

        await("Connector completed")
                .atMost(Duration.ofMinutes(3))
                .until(processInstance.getConnector("ai-connector-under-test"), connectorIsDone());

        var askResult = processInstance.getVariable("askResult");
        assertThat(askResult).isNotNull();
        log.info("[Studio] Connector output: {}", askResult.getValue());
        assertThat(askResult.getValue()).asString().isNotEmpty().contains("4");
    }

    @Test
    @Order(2)
    @DisplayName("[Studio] Process tasks should be tracked")
    void should_track_tasks_in_studio() {
        ProcessDefinition processDef = toolkit.getProcessDefinition(PROCESS_NAME);
        User user = toolkit.getUser("walter.bates");

        ProcessInstance processInstance = processDef.startProcessFor(user);
        await().atMost(Duration.ofMinutes(3)).until(processInstance, processInstanceStarted());

        List<Task> tasks = processInstance.searchTasks();
        assertThat(tasks).isNotEmpty();
        log.info("[Studio] Tasks: {}", tasks.stream().map(Task::getName).toList());
    }

    @Test
    @Order(10)
    @DisplayName("[Studio] Load test: 5 concurrent instances")
    void should_handle_concurrent_executions_in_studio() {
        ProcessDefinition processDef = toolkit.getProcessDefinition(PROCESS_NAME);
        User user = toolkit.getUser("walter.bates");
        int concurrentInstances = 5;

        long startTime = System.currentTimeMillis();
        List<ProcessInstance> instances = IntStream.range(0, concurrentInstances)
                .mapToObj(i -> processDef.startProcessFor(user))
                .toList();

        assertThat(instances).hasSize(concurrentInstances);

        instances.forEach(instance -> {
            await("Instance started").atMost(Duration.ofMinutes(5)).until(instance, processInstanceStarted());
        });

        long totalTime = System.currentTimeMillis() - startTime;
        log.info(
                "[Studio] Load test: {} instances in {} ms (avg {} ms/instance)",
                concurrentInstances,
                totalTime,
                totalTime / concurrentInstances);
    }

    @Test
    @Order(11)
    @DisplayName("[Studio] Performance: connector within 30s threshold")
    void should_execute_within_threshold_in_studio() {
        ProcessDefinition processDef = toolkit.getProcessDefinition(PROCESS_NAME);
        User user = toolkit.getUser("walter.bates");

        long startTime = System.currentTimeMillis();
        ProcessInstance processInstance = processDef.startProcessFor(user);
        await().atMost(Duration.ofMinutes(3)).until(processInstance, processInstanceStarted());
        await().atMost(Duration.ofMinutes(3))
                .until(processInstance.getConnector("ai-connector-under-test"), connectorIsDone());
        long executionTime = System.currentTimeMillis() - startTime;

        log.info("[Studio] Connector execution time: {} ms", executionTime);
        assertThat(executionTime).isLessThan(30_000);
    }

    private static void deployConnectorProcess(BonitaClient client) throws Exception {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("userPrompt", "What is 2+2? Answer with just the number.");
        inputs.put("systemPrompt", "You are a helpful assistant. Be concise.");
        inputs.put("url", System.getenv("AZURE_OPENAI_ENDPOINT"));
        inputs.put("apiKey", System.getenv("AZURE_OPENAI_API_KEY"));
        inputs.put("chatModelName", Objects.requireNonNullElse(System.getenv("AZURE_OPENAI_MODEL"), "gpt-4o"));

        Map<String, AiConnectorTestToolkit.Output> outputs = new HashMap<>();
        outputs.put("askResult", AiConnectorTestToolkit.Output.create("output", String.class.getName()));

        var barArchive = AiConnectorTestToolkit.buildConnectorToTest(
                CONNECTOR_DEF_ID, CONNECTOR_DEF_VERSION, inputs, outputs, ARTIFACT_ID);

        File barFile = null;
        try {
            barFile = Files.createTempFile("azure-studio-test", ".bar").toFile();
            barFile.delete();
            BusinessArchiveFactory.writeBusinessArchiveToFile(barArchive, barFile);
            client.processes().importProcess(barFile, ProcessImportPolicy.REPLACE_DUPLICATES);
            log.info("BAR deployed: {}", barFile.getName());
        } finally {
            if (barFile != null) {
                barFile.delete();
            }
        }

        var process = barArchive.getProcessDefinition();
        client.processes().getProcess(process.getName(), process.getVersion()).getId();
    }
}
