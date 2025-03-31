package org.bonitasoft.connectors.ai.mistral;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConfiguration;
import org.bonitasoft.connectors.ai.AiConnectorException;
import org.bonitasoft.connectors.ai.doc.UserDocument;
import org.bonitasoft.connectors.ai.extract.AiExtractChat;

@Slf4j
public class MistralAiExtractChat extends AiExtractChat<MistralAiChatModel> implements MistralAiChat {
    public MistralAiExtractChat(AiConfiguration configuration) {
        super(configuration);
    }

    @Override
    public AiConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    protected ChatMessage newDocMessage(UserDocument document) {
        if (List.of("image/png", "image/jpg", "image/jpeg").contains(document.mimeType())) {
            throw new AiConnectorException("Image types not supported yet by Mistral AI chat/completion API");
        }
        return super.newDocMessage(document);
    }
}
