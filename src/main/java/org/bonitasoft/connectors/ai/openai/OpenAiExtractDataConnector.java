package org.bonitasoft.connectors.ai.openai;

import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.extract.AiExtractDataConnector;

@Slf4j
public class OpenAiExtractDataConnector extends AiExtractDataConnector<OpenAiExtractChat> {
    @Override
    public void connect() {
        chat = new OpenAiExtractChat(configuration);
    }
}
