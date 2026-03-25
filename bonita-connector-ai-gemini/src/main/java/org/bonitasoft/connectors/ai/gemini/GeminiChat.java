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
package org.bonitasoft.connectors.ai.gemini;

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import java.time.Duration;
import org.bonitasoft.connectors.ai.AiChat;
import org.bonitasoft.connectors.ai.AiConfiguration;

public interface GeminiChat extends AiChat<GoogleAiGeminiChatModel> {

    String DEFAULT_MODEL = "gemini-2.0-flash";

    @Override
    default GoogleAiGeminiChatModel getChatModel() {
        AiConfiguration configuration = getConfiguration();
        boolean enableDebugLogging = configuration.isEnableDebugLogging();
        var chatModelBuilder = GoogleAiGeminiChatModel.builder().logRequestsAndResponses(enableDebugLogging);
        // API Key
        chatModelBuilder.apiKey(configuration.getApiKey());
        // Chat model name (default to gemini-2.0-flash)
        chatModelBuilder.modelName(configuration.getChatModelName().orElse(DEFAULT_MODEL));
        // Temperature
        configuration.getModelTemperature().ifPresent(chatModelBuilder::temperature);
        // Request timeout
        configuration.getRequestTimeout().ifPresent(timeout -> chatModelBuilder.timeout(Duration.ofMillis(timeout)));
        return chatModelBuilder.build();
    }
}
