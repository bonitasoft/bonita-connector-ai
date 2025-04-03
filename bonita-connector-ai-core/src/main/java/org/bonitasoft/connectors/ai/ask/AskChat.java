package org.bonitasoft.connectors.ai.ask;

import org.bonitasoft.connectors.ai.UserDocument;

public interface AskChat {

    String ask(String systemPrompt, String userPrompt, String jsonSchema, UserDocument document);
}
