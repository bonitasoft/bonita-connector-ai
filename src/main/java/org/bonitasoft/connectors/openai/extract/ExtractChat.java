package org.bonitasoft.connectors.openai.extract;

import java.util.List;
import org.bonitasoft.connectors.openai.doc.UserDocument;

public interface ExtractChat {

    String extract(UserDocument document, List<String> fields);

    String extract(UserDocument document, String jsonSchema);
}
