package org.bonitasoft.connectors.ai.mistral;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.AiConnectorException;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.connectors.ai.classify.ClassifyAiChat;

@Slf4j
public class MistralAiClassifyChat extends ClassifyAiChat<MistralAiChatModel> implements MistralAiChat {
    public MistralAiClassifyChat(AiConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected ChatMessage newDocMessage(UserDocument document) {
        if (List.of("image/png", "image/jpg", "image/jpeg").contains(document.mimeType())) {
            throw new AiConnectorException("Image types not supported yet by Mistral AI chat/completion API");
        }
        return super.newDocMessage(document);
    }
}
