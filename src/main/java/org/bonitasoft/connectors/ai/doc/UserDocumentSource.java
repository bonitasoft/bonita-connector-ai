package org.bonitasoft.connectors.ai.doc;

import dev.langchain4j.data.document.DocumentSource;
import dev.langchain4j.data.document.Metadata;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class UserDocumentSource implements DocumentSource {

    private final UserDocument document;
    private final Metadata metadata;

    public UserDocumentSource(UserDocument document) {
        this.document = document;
        this.metadata = new Metadata();
        document.metadata().forEach((key, value) -> metadata.put(key, String.valueOf(value)));
    }

    @Override
    public InputStream inputStream() {
        return new ByteArrayInputStream(document.data());
    }

    @Override
    public Metadata metadata() {
        return metadata;
    }
}
