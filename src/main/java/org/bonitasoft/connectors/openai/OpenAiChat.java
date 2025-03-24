package org.bonitasoft.connectors.openai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public abstract class OpenAiChat {

    protected final OpenAiConfiguration configuration;

    protected OpenAiChat(OpenAiConfiguration configuration) {
        this.configuration = configuration;
    }

    protected OpenAiChatModel.OpenAiChatModelBuilder getChatModelBuilder(OpenAiConfiguration configuration) {
        var chatModelBuilder = OpenAiChatModel.builder();
        // API Key
        chatModelBuilder.apiKey(configuration.getApiKey());
        // Url override
        configuration.getBaseUrl().ifPresent(chatModelBuilder::baseUrl);
        // Chat model name
        chatModelBuilder.modelName(configuration.getChatModelName());
        // Temperature
        configuration.getModelTemperature().ifPresent(chatModelBuilder::temperature);
        // Req timeout
        configuration
                .getRequestTimeout()
                .ifPresent(timeout -> chatModelBuilder.timeout(Duration.of(timeout, ChronoUnit.MILLIS)));
        return chatModelBuilder;
    }
}
