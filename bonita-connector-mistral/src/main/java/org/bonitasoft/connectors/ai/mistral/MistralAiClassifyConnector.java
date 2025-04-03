package org.bonitasoft.connectors.ai.mistral;

import org.bonitasoft.connectors.ai.classify.ClassifyAiConnector;

public class MistralAiClassifyConnector extends ClassifyAiConnector<MistralAiClassifyChat> {

    @Override
    public void connect() {
        chat = new MistralAiClassifyChat(configuration);
    }
}
