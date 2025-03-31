package org.bonitasoft.connectors.ai.mistral;

import dev.langchain4j.model.mistralai.MistralAiChatModel;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.bonitasoft.connectors.ai.AiChat;
import org.bonitasoft.connectors.ai.AiConfiguration;

public interface MistralAiChat extends AiChat<MistralAiChatModel> {

    @Override
    default MistralAiChatModel getChatModel() {
        AiConfiguration configuration = getConfiguration();
        var chatModelBuilder = MistralAiChatModel.builder().logRequests(true).logResponses(true);
        // API Key
        chatModelBuilder.apiKey(configuration.getApiKey());
        // Url override
        configuration.getBaseUrl().ifPresent(chatModelBuilder::baseUrl);
        // Chat model name
        // chatModelBuilder.modelName(configuration.getChatModelName());
        chatModelBuilder.modelName("mistral-ocr-latest");
        // Temperature
        configuration.getModelTemperature().ifPresent(chatModelBuilder::temperature);
        // Req timeout
        configuration
                .getRequestTimeout()
                .ifPresent(timeout -> chatModelBuilder.timeout(Duration.of(timeout, ChronoUnit.MILLIS)));
        return chatModelBuilder.build();
    }
}
