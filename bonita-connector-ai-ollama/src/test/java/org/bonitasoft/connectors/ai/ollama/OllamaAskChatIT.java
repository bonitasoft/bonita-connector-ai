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

import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.ask.AskChat;
import org.bonitasoft.connectors.ai.ask.AskChatIT;

@Slf4j
class OllamaAskChatIT extends AskChatIT {

    @Override
    protected void customize(AiConfiguration.AiConfigurationBuilder builder) {
        // Use environment variable for base URL if provided, otherwise default to localhost
        String baseUrl = System.getenv("OLLAMA_BASE_URL");
        if (baseUrl != null && !baseUrl.isEmpty()) {
            builder.baseUrl(baseUrl);
        }
        // Use environment variable for model name if provided, otherwise use default
        String modelName = System.getenv("OLLAMA_MODEL_NAME");
        if (modelName != null && !modelName.isEmpty()) {
            builder.chatModelName(modelName);
        }
    }

    @Override
    protected AskChat getChat(AiConfiguration configuration) {
        return new OllamaAskAiChat(configuration);
    }
}
