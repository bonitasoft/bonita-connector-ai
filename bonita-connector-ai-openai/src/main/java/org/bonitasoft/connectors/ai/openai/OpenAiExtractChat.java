package org.bonitasoft.connectors.ai.openai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.extract.ExtractAiChat;

@Slf4j
public class OpenAiExtractChat extends ExtractAiChat<OpenAiChatModel> implements OpenAiChat {
    public OpenAiExtractChat(AiConfiguration configuration) {
        super(configuration);
    }

    @Override
    public AiConfiguration getConfiguration() {
        return configuration;
    }
}
