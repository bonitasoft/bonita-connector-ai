package org.bonitasoft.connectors.ai.ask;

import org.bonitasoft.connectors.ai.doc.UserDocument;

public interface AskChat {

    String ask(String systemPrompt, String userPrompt, String jsonSchema, UserDocument document);
}
