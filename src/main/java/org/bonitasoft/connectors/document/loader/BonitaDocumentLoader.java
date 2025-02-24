package org.bonitasoft.connectors.document.loader;

import org.bonitasoft.connectors.AiConnectorException;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.document.Document;
import org.bonitasoft.engine.bpm.document.DocumentNotFoundException;
import org.bonitasoft.engine.connector.EngineExecutionContext;

public class BonitaDocumentLoader implements DocumentLoader {

    private final ProcessAPI processAPI;
    private final EngineExecutionContext executionContext;

    public BonitaDocumentLoader(ProcessAPI processAPI, EngineExecutionContext executionContext) {
        this.processAPI = processAPI;
        this.executionContext = executionContext;
    }

    @Override
    public byte[] load(String docRef) {
        if (docRef == null || docRef.isEmpty()) {
            return new byte[0];
        }
        try {
            long processInstanceId = executionContext.getProcessInstanceId();
            Document document = processAPI.getLastDocument(processInstanceId, docRef);
            return processAPI.getDocumentContent(document.getContentStorageId());
        } catch (final DocumentNotFoundException e) {
            throw new AiConnectorException("Document not found for ref: " + docRef, e);
        }
    }
}
