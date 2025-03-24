package org.bonitasoft.connectors.openai.classify;

import java.util.List;
import org.bonitasoft.connectors.openai.doc.UserDocument;

public interface ClassifyChat {

    String classify(List<String> categories, UserDocument userDocument);
}
