package org.bonitasoft.connectors.ai.mistral;

import dev.langchain4j.model.mistralai.MistralAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.ask.AiAskChat;

@Slf4j
public class MistralAiAskChat extends AiAskChat<MistralAiChatModel> implements MistralAiChat {
    public MistralAiAskChat(AiConfiguration configuration) {
        super(configuration);
    }
}
