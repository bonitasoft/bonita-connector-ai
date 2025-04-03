package org.bonitasoft.connectors.ai.mistral;

import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.ask.AskAiConnector;
import org.bonitasoft.engine.connector.ConnectorException;

@Slf4j
public class MistralAiAskConnector extends AskAiConnector {
    @Override
    public void connect() throws ConnectorException {
        chat = new MistralAskAiChat(configuration);
    }
}
