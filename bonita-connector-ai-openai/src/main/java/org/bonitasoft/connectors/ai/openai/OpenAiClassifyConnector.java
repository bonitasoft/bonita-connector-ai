package org.bonitasoft.connectors.ai.openai;

import org.bonitasoft.connectors.ai.classify.ClassifyAiConnector;

public class OpenAiClassifyConnector extends ClassifyAiConnector<OpenAiClassifyChat> {

    @Override
    public void connect() {
        chat = new OpenAiClassifyChat(configuration);
    }
}
