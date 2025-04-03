package org.bonitasoft.connectors.ai.openai;

import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.extract.ExtractChat;
import org.bonitasoft.connectors.ai.extract.ExtractChatIT;

@Slf4j
class OpenAiExtractChatIT extends ExtractChatIT {

    @Override
    protected void customize(AiConfiguration.AiConfigurationBuilder builder) {
        builder.apiKey(System.getenv("OPENAI_API_KEY"));
    }

    @Override
    protected ExtractChat getChat(AiConfiguration configuration) {
        return new OpenAiExtractChat(configuration);
    }
}
