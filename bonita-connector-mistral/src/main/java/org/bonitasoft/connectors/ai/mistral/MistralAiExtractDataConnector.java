package org.bonitasoft.connectors.ai.mistral;

import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.extract.ExtractAiConnector;

@Slf4j
public class MistralAiExtractDataConnector extends ExtractAiConnector<MistralAiExtractChat> {
    @Override
    public void connect() {
        chat = new MistralAiExtractChat(configuration);
    }
}
