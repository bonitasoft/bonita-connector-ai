package org.bonitasoft.connectors.ai.openai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.classify.AiClassifyChat;

@Slf4j
public class OpenAiClassifyChat extends AiClassifyChat<OpenAiChatModel> implements OpenAiChat {
    public OpenAiClassifyChat(AiConfiguration configuration) {
        super(configuration);
    }
}
