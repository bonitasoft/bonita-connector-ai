package org.bonitasoft.connectors.openai.extract;

import java.util.List;
import org.bonitasoft.connectors.openai.doc.UserDocument;
import org.bonitasoft.engine.connector.ConnectorException;

public interface ExtractChat {

    String extract(UserDocument document, List<String> fields) throws ConnectorException;

    String extract(UserDocument document, String jsonSchema) throws ConnectorException;
}
