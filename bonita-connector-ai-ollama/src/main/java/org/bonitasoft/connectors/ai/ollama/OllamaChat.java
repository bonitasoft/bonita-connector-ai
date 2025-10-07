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

import dev.langchain4j.model.ollama.OllamaChatModel;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.bonitasoft.connectors.ai.AiChat;
import org.bonitasoft.connectors.ai.AiConfiguration;

public interface OllamaChat extends AiChat<OllamaChatModel> {

    String DEFAULT_BASE_URL = "http://localhost:11434";
    String DEFAULT_MODEL_NAME = "llama3.1";

    @Override
    default OllamaChatModel getChatModel() {
        AiConfiguration configuration = getConfiguration();
        var chatModelBuilder = OllamaChatModel.builder().logRequests(true).logResponses(true);
        // Base URL (default to localhost:11434)
        chatModelBuilder.baseUrl(configuration.getBaseUrl().orElse(DEFAULT_BASE_URL));
        // Chat model name (default to llama3.1)
        chatModelBuilder.modelName(configuration.getChatModelName().orElse(DEFAULT_MODEL_NAME));
        // Temperature
        configuration.getModelTemperature().ifPresent(chatModelBuilder::temperature);
        // Request timeout
        configuration
                .getRequestTimeout()
                .ifPresent(timeout -> chatModelBuilder.timeout(Duration.of(timeout, ChronoUnit.MILLIS)));
        return chatModelBuilder.build();
    }
}
