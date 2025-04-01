package org.bonitasoft.connectors.ai.openai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.bonitasoft.connectors.ai.AiChat;
import org.bonitasoft.connectors.ai.AiConfiguration;

public interface OpenAiChat extends AiChat<OpenAiChatModel> {

    @Override
    default OpenAiChatModel getChatModel() {
        AiConfiguration configuration = getConfiguration();
        var chatModelBuilder = OpenAiChatModel.builder().logRequests(true).logResponses(true);
        // API Key
        chatModelBuilder.apiKey(configuration.getApiKey());
        // Url override
        configuration.getBaseUrl().ifPresent(chatModelBuilder::baseUrl);
        // Chat model name
        chatModelBuilder.modelName(configuration.getChatModelName().orElse("gpt-4o-mini"));
        // Temperature
        configuration.getModelTemperature().ifPresent(chatModelBuilder::temperature);
        // Req timeout
        configuration
                .getRequestTimeout()
                .ifPresent(timeout -> chatModelBuilder.timeout(Duration.of(timeout, ChronoUnit.MILLIS)));
        return chatModelBuilder.build();
    }
}
