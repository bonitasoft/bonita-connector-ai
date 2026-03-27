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
package org.bonitasoft.connectors.ai.cohere;

import dev.langchain4j.model.openai.OpenAiChatModel;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.bonitasoft.connectors.ai.AiChat;
import org.bonitasoft.connectors.ai.AiConfiguration;

/**
 * Cohere AI chat integration using Cohere's OpenAI-compatible endpoint.
 * <p>
 * Since LangChain4j's {@code langchain4j-cohere} module only provides scoring/reranking
 * (not chat), this implementation uses {@link OpenAiChatModel} with Cohere's
 * OpenAI-compatible endpoint at {@code https://api.cohere.com/compatibility/v1/}.
 */
public interface CohereChat extends AiChat<OpenAiChatModel> {

    /** Cohere's OpenAI-compatible base URL. */
    String DEFAULT_BASE_URL = "https://api.cohere.com/compatibility/v1/";

    /** Default Cohere chat model. */
    String DEFAULT_MODEL = "command-r-plus";

    @Override
    default OpenAiChatModel getChatModel() {
        AiConfiguration configuration = getConfiguration();
        var chatModelBuilder = OpenAiChatModel.builder().logRequests(true).logResponses(true);
        // API Key
        chatModelBuilder.apiKey(configuration.getApiKey());
        // Url override — default to Cohere's OpenAI-compatible endpoint
        chatModelBuilder.baseUrl(configuration.getBaseUrl().orElse(DEFAULT_BASE_URL));
        // Chat model name — default to command-r-plus
        chatModelBuilder.modelName(configuration.getChatModelName().orElse(DEFAULT_MODEL));
        // Temperature
        configuration.getModelTemperature().ifPresent(chatModelBuilder::temperature);
        // Req timeout
        configuration
                .getRequestTimeout()
                .ifPresent(timeout -> chatModelBuilder.timeout(Duration.of(timeout, ChronoUnit.MILLIS)));
        return chatModelBuilder.build();
    }
}
