package org.bonitasoft.connectors.ai.mistral;

import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.extract.AiExtractDataConnector;

@Slf4j
public class MistralAiExtractDataConnector extends AiExtractDataConnector<MistralAiExtractChat> {
    @Override
    public void connect() {
        chat = new MistralAiExtractChat(configuration);
    }
}
