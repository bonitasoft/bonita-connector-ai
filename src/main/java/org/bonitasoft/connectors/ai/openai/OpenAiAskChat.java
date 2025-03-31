package org.bonitasoft.connectors.ai.openai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.ask.AiAskChat;

@Slf4j
public class OpenAiAskChat extends AiAskChat<OpenAiChatModel> implements OpenAiChat {
    public OpenAiAskChat(AiConfiguration configuration) {
        super(configuration);
    }
}
