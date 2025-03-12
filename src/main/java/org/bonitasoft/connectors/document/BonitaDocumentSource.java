package org.bonitasoft.connectors.document;

import dev.langchain4j.data.document.DocumentSource;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.internal.ValidationUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bonitasoft.connectors.openai.OpenAiConnectorException;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.document.Document;
import org.bonitasoft.engine.bpm.document.DocumentNotFoundException;
import org.bonitasoft.engine.connector.EngineExecutionContext;

public class BonitaDocumentSource implements DocumentSource {

    private final ProcessAPI processAPI;
    private final EngineExecutionContext executionContext;
    private final String docRef;

    private final Metadata metadata = new Metadata();

    public BonitaDocumentSource(ProcessAPI processAPI, EngineExecutionContext executionContext, String docRef) {
        this.processAPI = ValidationUtils.ensureNotNull(processAPI, "processAPI");
        this.executionContext = ValidationUtils.ensureNotNull(executionContext, "executionContext");
        this.docRef = ValidationUtils.ensureNotNull(docRef, "docRef");
    }

    @Override
    public InputStream inputStream() throws IOException {
        metadata.put("docRef", docRef);
        var emptyStream = new ByteArrayInputStream(new byte[0]);
        if (docRef == null || docRef.isEmpty()) {
            return emptyStream;
        }

        try {
            long processInstanceId = executionContext.getProcessInstanceId();

            Document document = processAPI.getLastDocument(processInstanceId, docRef);
            if (document == null) {
                return emptyStream;
            }
            metadata.put("FileName", document.getContentFileName());
            metadata.put("Author", document.getAuthor());
            metadata.put("ContentMimeType", document.getContentMimeType());
            metadata.put("Description", document.getDescription());
            metadata.put("CreationDate", document.getCreationDate().toString());
            byte[] content = processAPI.getDocumentContent(document.getContentStorageId());
            return new ByteArrayInputStream(content);

        } catch (final DocumentNotFoundException e) {
            throw new OpenAiConnectorException("Document not found for ref: " + docRef, e);
        }
    }

    @Override
    public Metadata metadata() {
        return metadata;
    }
}
