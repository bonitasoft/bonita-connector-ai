package org.bonitasoft.connectors.ai.mistral;

import dev.langchain4j.model.mistralai.MistralAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.classify.ClassifyAiChat;

@Slf4j
public class MistralAiClassifyChat extends ClassifyAiChat<MistralAiChatModel> implements MistralAiChat {
    public MistralAiClassifyChat(AiConfiguration configuration) {
        super(configuration);
    }
}
