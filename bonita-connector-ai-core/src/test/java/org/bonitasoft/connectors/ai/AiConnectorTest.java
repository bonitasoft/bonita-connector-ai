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

import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.junit.jupiter.api.Test;

class AiConnectorTest {

    @Test
    void apiKey_should_take_sys_prop_value() throws Exception {
        DummyAiConnector connector = new DummyAiConnector();

        String apiKey = "123456789";
        System.setProperty(AiConfiguration.AI_API_KEY, apiKey);

        connector.validateInputParameters();

        assertThat(connector.getConfiguration()).isNotNull();
        assertThat(connector.getConfiguration().getApiKey()).isEqualTo(apiKey);
    }

    @Test
    void default_ApiKey_should_be_changeMe() throws Exception {
        DummyAiConnector connector = new DummyAiConnector();

        connector.validateInputParameters();

        assertThat(connector.getConfiguration()).isNotNull();
        assertThat(connector.getConfiguration().getApiKey()).isEqualTo("changeMe");
    }

    public static class DummyAiConnector extends AiConnector {

        public DummyAiConnector() {}

        @Override
        protected void validateConfiguration() throws ConnectorValidationException {}

        @Override
        protected Object doExecute() throws ConnectorException {
            return null;
        }
    }
}
