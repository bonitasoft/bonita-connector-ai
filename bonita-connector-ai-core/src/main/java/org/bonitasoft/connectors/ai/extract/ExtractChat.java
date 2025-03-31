package org.bonitasoft.connectors.ai.extract;

import java.util.List;
import org.bonitasoft.connectors.ai.UserDocument;

public interface ExtractChat {

    String extract(UserDocument document, List<String> fields);

    String extract(UserDocument document, String jsonSchema);
}
