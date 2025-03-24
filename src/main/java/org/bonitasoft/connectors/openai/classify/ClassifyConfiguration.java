package org.bonitasoft.connectors.openai.classify;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ClassifyConfiguration {

    public static final String SOURCE_DOCUMENT_REF = "sourceDocumentRef";
    public static final String CATEGORIES = "categories";

    private final String sourceDocumentRef;

    @Builder.Default
    private List<String> categories = new ArrayList<>();

}
