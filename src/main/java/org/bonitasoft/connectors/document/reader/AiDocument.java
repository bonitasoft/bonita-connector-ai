package org.bonitasoft.connectors.document.reader;

import java.util.Set;

public interface AiDocument {

    Set<String> getMetadataNames();

    String getMetadata(String name);

    String getContent();
}
