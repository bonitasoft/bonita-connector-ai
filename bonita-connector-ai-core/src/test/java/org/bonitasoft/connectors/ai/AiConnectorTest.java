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
package org.bonitasoft.connectors.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bonitasoft.connectors.ai.AiConfiguration.*;
import static uk.org.webcompere.systemstubs.SystemStubs.withEnvironmentVariables;

import java.util.Map;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AiConnectorTest {

    DummyAiConnector connector;

    @BeforeEach
    void setUp() {
        connector = new DummyAiConnector();
    }

    @Test
    void paramteters_should_be_mapped_to_configuration() throws ConnectorValidationException {
        // Given
        String url = "MyUrl";
        int timeout = 123456;
        String myApiKey = "MyApiKey";
        String chat = "LeChat";
        double temperature = 0.0;
        connector.setInputParameters(Map.of(
                URL, url,
                TIMEOUT_MS, timeout,
                API_KEY, myApiKey,
                CHAT_MODEL_NAME, chat,
                MODEL_TEMPERATURE, temperature));
        // When
        connector.validateInputParameters();

        // Then
        var configuration = connector.getConfiguration();
        assertThat(configuration).isNotNull();
        assertThat(configuration.getBaseUrl()).isNotEmpty().get().isEqualTo(url);
        assertThat(configuration.getRequestTimeout()).isNotEmpty().get().isEqualTo(timeout);
        assertThat(configuration.getApiKey()).isEqualTo(myApiKey);
        assertThat(configuration.getChatModelName()).isNotEmpty().get().isEqualTo(chat);
        assertThat(configuration.getModelTemperature()).isNotEmpty().get().isEqualTo(temperature);
    }

    @Test
    void optional_paramteters_should_be_mapped_to_empty() throws ConnectorValidationException {
        // Given
        String myApiKey = "MyApiKey";
        connector.setInputParameters(Map.of(API_KEY, myApiKey));
        // When
        connector.validateInputParameters();

        // Then
        var configuration = connector.getConfiguration();
        assertThat(configuration).isNotNull();
        assertThat(configuration.getBaseUrl()).isEmpty();
        assertThat(configuration.getRequestTimeout()).isEmpty();
        assertThat(configuration.getApiKey()).isEqualTo(myApiKey);
        assertThat(configuration.getChatModelName()).isEmpty();
        assertThat(configuration.getModelTemperature()).isEmpty();
    }

    @Test
    void env_api_key_should_be_mapped_to_configuration() throws Exception {
        // Given
        String myApiKey = "MyApiKey";
        withEnvironmentVariables(AI_API_KEY, myApiKey).execute(() -> {
            // When
            connector.validateInputParameters();

            // Then
            var configuration = connector.getConfiguration();
            assertThat(configuration.getApiKey()).isEqualTo(myApiKey);
        });
    }

    public static class DummyAiConnector extends AiConnector {

        @Override
        protected void validateConfiguration() throws ConnectorValidationException {}

        @Override
        protected Object doExecute() throws ConnectorException {
            return null;
        }
    }
}
