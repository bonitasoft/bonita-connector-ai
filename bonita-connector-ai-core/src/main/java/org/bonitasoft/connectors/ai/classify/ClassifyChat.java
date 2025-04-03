package org.bonitasoft.connectors.ai.classify;

import java.util.List;
import org.bonitasoft.connectors.ai.UserDocument;

public interface ClassifyChat {

    String classify(List<String> categories, UserDocument userDocument);
}
