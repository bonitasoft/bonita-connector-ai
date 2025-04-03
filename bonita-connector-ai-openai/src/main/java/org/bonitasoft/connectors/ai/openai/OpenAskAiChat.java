package org.bonitasoft.connectors.ai.openai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.ask.AskAiChat;

@Slf4j
public class OpenAskAiChat extends AskAiChat<OpenAiChatModel> implements OpenAiChat {
    public OpenAskAiChat(AiConfiguration configuration) {
        super(configuration);
    }
}
