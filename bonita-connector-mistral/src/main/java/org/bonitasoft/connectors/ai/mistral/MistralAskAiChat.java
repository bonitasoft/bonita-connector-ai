package org.bonitasoft.connectors.ai.mistral;

import dev.langchain4j.model.mistralai.MistralAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.ask.AskAiChat;

@Slf4j
public class MistralAskAiChat extends AskAiChat<MistralAiChatModel> implements MistralAiChat {
    public MistralAskAiChat(AiConfiguration configuration) {
        super(configuration);
    }
}
