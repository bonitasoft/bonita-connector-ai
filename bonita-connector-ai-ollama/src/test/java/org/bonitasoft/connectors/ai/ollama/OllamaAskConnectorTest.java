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
package org.bonitasoft.connectors.ai.ollama;

import static org.assertj.core.api.Assertions.assertThat;

import org.bonitasoft.connectors.ai.AiConfiguration;
import org.junit.jupiter.api.Test;

class OllamaAskConnectorTest {

    @Test
    void should_create_connector() throws Exception {
        OllamaAskConnector connector = new OllamaAskConnector();
        assertThat(connector).isNotNull();
    }

    @Test
    void should_use_default_base_url() {
        AiConfiguration config = AiConfiguration.builder().build();
        OllamaAskAiChat chat = new OllamaAskAiChat(config);

        assertThat(chat.getChatModel()).isNotNull();
    }
}
