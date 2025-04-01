package org.bonitasoft.connectors.ai.openai;

import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.classify.ClassifyChat;
import org.bonitasoft.connectors.ai.classify.ClassifyChatIT;

class OpenClassifyChatIT extends ClassifyChatIT {

    @Override
    protected void customize(AiConfiguration.AiConfigurationBuilder builder) {
        builder.apiKey(System.getenv("OPENAI_API_KEY"));
    }

    @Override
    protected ClassifyChat getChat(AiConfiguration configuration) {
        return new OpenAiClassifyChat(configuration);
    }
}
