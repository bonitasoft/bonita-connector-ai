package org.bonitasoft.connectors.document;

import org.apache.tika.metadata.Metadata;

import java.util.Set;

class TikaAiDocument implements AiDocument {

    private final String content;
    private final Metadata metadata;

    TikaAiDocument(String content, Metadata metadata) {
        this.content = content;
        this.metadata = metadata;
    }

    @Override
    public Set<String> getMetadataNames() {
        return Set.of(metadata.names());
    }

    @Override
    public String getMetadata(String name) {
        return metadata.get(name);
    }

    @Override
    public String getContent() {
        return content;
    }
}
