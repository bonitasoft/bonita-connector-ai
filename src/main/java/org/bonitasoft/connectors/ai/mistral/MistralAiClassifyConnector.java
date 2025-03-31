package org.bonitasoft.connectors.ai.mistral;

import org.bonitasoft.connectors.ai.classify.AiClassifyConnector;

public class MistralAiClassifyConnector extends AiClassifyConnector<MistralAiClassifyChat> {

    @Override
    public void connect() {
        chat = new MistralAiClassifyChat(configuration);
    }
}
