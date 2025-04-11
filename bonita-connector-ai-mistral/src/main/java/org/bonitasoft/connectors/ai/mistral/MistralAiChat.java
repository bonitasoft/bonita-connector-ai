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
package org.bonitasoft.connectors.ai.mistral;

import dev.langchain4j.model.mistralai.MistralAiChatModel;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.bonitasoft.connectors.ai.AiChat;
import org.bonitasoft.connectors.ai.AiConfiguration;

public interface MistralAiChat extends AiChat<MistralAiChatModel> {

    String PIXTRAL_12_B_2409 = "pixtral-12b-2409";

    @Override
    default MistralAiChatModel getChatModel() {
        AiConfiguration configuration = getConfiguration();
        var chatModelBuilder = MistralAiChatModel.builder().logRequests(true).logResponses(true);
        // API Key
        chatModelBuilder.apiKey(configuration.getApiKey());
        // Url override
        configuration.getBaseUrl().ifPresent(chatModelBuilder::baseUrl);
        // Chat model name
        chatModelBuilder.modelName(configuration.getChatModelName().orElse(PIXTRAL_12_B_2409));
        // Temperature
        configuration.getModelTemperature().ifPresent(chatModelBuilder::temperature);
        // Req timeout
        configuration
                .getRequestTimeout()
                .ifPresent(timeout -> chatModelBuilder.timeout(Duration.of(timeout, ChronoUnit.MILLIS)));
        return chatModelBuilder.build();
    }
}
