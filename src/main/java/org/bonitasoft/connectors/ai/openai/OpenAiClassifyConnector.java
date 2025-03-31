package org.bonitasoft.connectors.ai.openai;

import org.bonitasoft.connectors.ai.classify.AiClassifyConnector;

public class OpenAiClassifyConnector extends AiClassifyConnector<OpenAiClassifyChat> {

    @Override
    public void connect() {
        chat = new OpenAiClassifyChat(configuration);
    }
}
