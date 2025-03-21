package org.bonitasoft.connectors.openai.ask;

import org.bonitasoft.connectors.openai.doc.UserDocument;

public interface AskChat {

    String ask(String systemPrompt, String userPrompt, String jsonSchema, UserDocument document);
}
