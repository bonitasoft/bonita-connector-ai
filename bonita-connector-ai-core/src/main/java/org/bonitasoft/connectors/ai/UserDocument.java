package org.bonitasoft.connectors.ai;

import java.util.*;

public record UserDocument(String mimeType, byte[] data, Map<String, Object> metadata) {

    public UserDocument(String mimeType, byte[] data) {
        this(mimeType, data, null);
    }

    public UserDocument(String mimeType, byte[] data, Map<String, Object> metadata) {
        this.mimeType = Optional.ofNullable(mimeType).orElse("application/octet-stream");
        this.data = Optional.ofNullable(data).orElse(new byte[0]);
        this.metadata = Optional.ofNullable(metadata).orElse(new HashMap<>());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDocument that = (UserDocument) o;
        return Objects.deepEquals(data, that.data)
                && Objects.equals(mimeType, that.mimeType)
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mimeType, Arrays.hashCode(data), metadata);
    }

    @Override
    public String toString() {
        return "UserDocument{" + "mimeType='"
                + mimeType + '\'' + ", data="
                + Arrays.toString(data) + ", metadata="
                + metadata + '}';
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
