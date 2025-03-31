package org.bonitasoft.connectors.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;

public interface AiChat<T extends ChatLanguageModel> {

    AiConfiguration getConfiguration();

    T getChatModel();
}
