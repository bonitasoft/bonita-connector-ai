package org.bonitasoft.connectors.ai.openai;

import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.extract.ExtractAiConnector;

@Slf4j
public class OpenAiExtractDataConnector extends ExtractAiConnector<OpenAiExtractChat> {
    @Override
    public void connect() {
        chat = new OpenAiExtractChat(configuration);
    }
}
