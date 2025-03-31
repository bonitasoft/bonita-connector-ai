package org.bonitasoft.connectors.ai.openai;

import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.ask.AiAskConnector;
import org.bonitasoft.engine.connector.ConnectorException;

@Slf4j
public class OpenAiAskConnector extends AiAskConnector {
    @Override
    public void connect() throws ConnectorException {
        chat = new OpenAiAskChat(configuration);
    }
}
