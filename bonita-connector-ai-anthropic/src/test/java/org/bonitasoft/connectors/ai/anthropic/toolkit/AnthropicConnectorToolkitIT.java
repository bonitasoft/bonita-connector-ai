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

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.anthropic.toolkit.AiConnectorTestToolkit.Output;
import org.bonitasoft.web.client.BonitaClient;
import org.bonitasoft.web.client.api.ArchivedProcessInstanceApi;
import org.bonitasoft.web.client.api.ProcessInstanceApi;
import org.bonitasoft.web.client.exception.NotFoundException;
import org.bonitasoft.web.client.model.ArchivedProcessInstance;
import org.bonitasoft.web.client.services.policies.OrganizationImportPolicy;
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
 * Integration tests that deploy Anthropic AI connectors into a real Bonita runtime
 * using Testcontainers. Tests verify the full connector lifecycle:
 * VALIDATE -> CONNECT -> EXECUTE -> DISCONNECT within a real BPM process.
 *
 * Requires:
 * - Docker running
 * - ANTHROPIC_API_KEY environment variable set
 * - bonita.version system property set (e.g. -Dbonita.version=2025.1)
 */
@Slf4j
@Tag("btt")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnthropicConnectorToolkitIT {

    private static final String ANTHROPIC_ASK_DEF_ID = "anthropic-ask";
    private static final String ANTHROPIC_ASK_DEF_VERSION = "1.0.0";

    private static final String ARTIFACT_ID = "bonita-connector-ai-anthropic";

    private static final Logger LOGGER = LoggerFactory.getLogger(AnthropicConnectorToolkitIT.class);
    private static final String BONITA_VERSION =
            System.getProperty("bonita.version", Objects.requireNonNullElse(System.getenv("BONITA_VERSION"), "2025.2"));

    @Container
    static GenericContainer<?> BONITA_CONTAINER = new GenericContainer<>(
                    DockerImageName.parse(String.format("bonita:%s", BONITA_VERSION)))
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/bonita"))
            .withLogConsumer(new Slf4jLogConsumer(LOGGER));

    private static BonitaClient client;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @BeforeAll
    static void installOrganization() {
        client = BonitaClient.builder(String.format(
                        "http://%s:%s/bonita", BONITA_CONTAINER.getHost(), BONITA_CONTAINER.getFirstMappedPort()))
                .build();
        client.login("install", "install");
        var orgFile = AnthropicConnectorToolkitIT.class.getResource("/ACME.xml");
        if (orgFile != null) {
            client.users().importOrganization(new File(orgFile.getFile()), OrganizationImportPolicy.IGNORE_DUPLICATES);
        }
    }

    @BeforeEach
    void login() {
        client = BonitaClient.builder(String.format(
                        "http://%s:%s/bonita", BONITA_CONTAINER.getHost(), BONITA_CONTAINER.getFirstMappedPort()))
                .build();
        client.login("install", "install");
    }

    @AfterEach
    void logout() {
        if (client != null) {
            client.logout();
        }
    }

    @Test
    @Order(1)
    void should_execute_ask_connector_in_bonita_process() throws Exception {
        // Given - Configure the Ask connector inputs
        Map<String, String> inputs = new HashMap<>();
        inputs.put("userPrompt", "What is 2+2? Answer with just the number.");
        inputs.put("systemPrompt", "You are a helpful assistant. Be concise.");
        inputs.put("apiKey", System.getenv("ANTHROPIC_API_KEY"));

        Map<String, Output> outputs = new HashMap<>();
        outputs.put("askResult", Output.create("output", String.class.getName()));

        // When - Build, deploy, and execute the process
        var barFile = AiConnectorTestToolkit.buildConnectorToTest(
                ANTHROPIC_ASK_DEF_ID, ANTHROPIC_ASK_DEF_VERSION, inputs, outputs, ARTIFACT_ID);
        var processResponse = AiConnectorTestToolkit.importAndLaunchProcess(barFile, client);

        // Then - Wait for process to start (connector executed on enter)
        await().until(pollInstanceState(processResponse.getCaseId()), "started"::equals);

        String result =
                AiConnectorTestToolkit.getProcessVariableValue(client, processResponse.getCaseId(), "askResult");
        log.info("Ask connector result: {}", result);
        assertThat(result).isNotNull().contains("4");
    }

    @Test
    @Order(2)
    void should_execute_ask_connector_with_json_schema() throws Exception {
        // Given
        Map<String, String> inputs = new HashMap<>();
        inputs.put("userPrompt", "What is the capital of France?");
        inputs.put("systemPrompt", "You are a geography expert.");
        inputs.put("apiKey", System.getenv("ANTHROPIC_API_KEY"));
        inputs.put(
                "outputJsonSchema",
                """
                {"type":"object","required":["city","country"],"properties":{"city":{"type":"string"},"country":{"type":"string"}}}""");

        Map<String, Output> outputs = new HashMap<>();
        outputs.put("jsonResult", Output.create("output", String.class.getName()));

        // When
        var barFile = AiConnectorTestToolkit.buildConnectorToTest(
                ANTHROPIC_ASK_DEF_ID, ANTHROPIC_ASK_DEF_VERSION, inputs, outputs, ARTIFACT_ID);
        var processResponse = AiConnectorTestToolkit.importAndLaunchProcess(barFile, client);

        // Then
        await().until(pollInstanceState(processResponse.getCaseId()), "started"::equals);

        String result =
                AiConnectorTestToolkit.getProcessVariableValue(client, processResponse.getCaseId(), "jsonResult");
        log.info("JSON result: {}", result);
        assertThat(result).isNotNull();
        var city = mapper.readValue(result, CityResponse.class);
        assertThat(city.city()).containsIgnoringCase("Paris");
        assertThat(city.country()).containsIgnoringCase("France");
    }

    private Callable<String> pollInstanceState(String id) {
        return () -> {
            try {
                var instance = client.get(ProcessInstanceApi.class).getProcessInstanceById(id, (String) null);
                return instance.getState().name().toLowerCase();
            } catch (NotFoundException e) {
                var archived = getCompletedProcess(id);
                return archived != null ? archived.getState().name().toLowerCase() : "unknown";
            }
        };
    }

    private ArchivedProcessInstance getCompletedProcess(String id) {
        var archivedInstances = client.get(ArchivedProcessInstanceApi.class)
                .searchArchivedProcessInstances(
                        new ArchivedProcessInstanceApi.SearchArchivedProcessInstancesQueryParams()
                                .c(1)
                                .p(0)
                                .f(List.of("caller=any", "sourceObjectId=" + id)));
        return archivedInstances.isEmpty() ? null : archivedInstances.get(0);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record CityResponse(String city, String country) {}
}
