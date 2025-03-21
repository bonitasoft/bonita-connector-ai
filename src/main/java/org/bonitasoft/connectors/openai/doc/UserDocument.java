package org.bonitasoft.connectors.openai.doc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record UserDocument(String mimeType, byte[] data, Map<String, Object> metadata) {

    public UserDocument(String mimeType, byte[] data) {
        this(mimeType, data, null);
    }

    public UserDocument(String mimeType, byte[] data, Map<String, Object> metadata) {
        this.mimeType = Optional.ofNullable(mimeType).orElse("application/octet-stream");
        this.data = Optional.ofNullable(data).orElse(new byte[0]);
        this.metadata = Optional.ofNullable(metadata).orElse(new HashMap<>());
    }

    public enum Metadatas {
        DOC_REF,
        FILE_NAME,
        AUTHOR,
        MIME_TYPE,
        DESCRIPTION,
        CREATION_DATE
    }
}
