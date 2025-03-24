package org.bonitasoft.connectors.openai.classify;

import org.bonitasoft.connectors.openai.doc.UserDocument;

import java.util.List;

public interface ClassifyChat {

    String classify(List<String> categories, UserDocument userDocument);
}
