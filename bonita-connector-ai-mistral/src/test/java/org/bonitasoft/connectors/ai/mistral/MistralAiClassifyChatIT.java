package org.bonitasoft.connectors.ai.mistral;

import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.classify.ClassifyChat;
import org.bonitasoft.connectors.ai.classify.ClassifyChatIT;

class MistralAiClassifyChatIT extends ClassifyChatIT {

    @Override
    protected void customize(AiConfiguration.AiConfigurationBuilder builder) {
        builder.apiKey(System.getenv("MISTRAL_API_KEY"));
    }

    @Override
    protected ClassifyChat getChat(AiConfiguration configuration) {
        return new MistralAiClassifyChat(configuration);
    }
}
