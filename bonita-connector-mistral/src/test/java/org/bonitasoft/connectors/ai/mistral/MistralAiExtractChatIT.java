package org.bonitasoft.connectors.ai.mistral;

import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.extract.ExtractChat;
import org.bonitasoft.connectors.ai.extract.ExtractChatIT;

@Slf4j
class MistralAiExtractChatIT extends ExtractChatIT {
    @Override
    protected void customize(AiConfiguration.AiConfigurationBuilder builder) {
        builder.apiKey(System.getenv("MISTRAL_API_KEY"));
    }

    @Override
    protected ExtractChat getChat(AiConfiguration configuration) {
        return new MistralAiExtractChat(configuration);
    }
}
