package org.bonitasoft.connectors.ai.mistral;

import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.ask.AskChat;
import org.bonitasoft.connectors.ai.ask.AskChatIT;

@Slf4j
class MistralAskAiChatIT extends AskChatIT {
    @Override
    protected void customize(AiConfiguration.AiConfigurationBuilder builder) {
        builder.apiKey(System.getenv("MISTRAL_API_KEY"));
    }

    @Override
    protected AskChat getChat(AiConfiguration configuration) {
        return new MistralAskAiChat(configuration);
    }
}
