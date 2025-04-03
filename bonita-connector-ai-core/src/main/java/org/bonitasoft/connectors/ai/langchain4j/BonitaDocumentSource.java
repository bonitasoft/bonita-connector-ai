/**
 * Copyright (C) 2025 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.connectors.ai.langchain4j;

import dev.langchain4j.data.document.DocumentSource;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.internal.ValidationUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.bonitasoft.connectors.ai.AiConnectorException;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.document.Document;
import org.bonitasoft.engine.bpm.document.DocumentNotFoundException;
import org.bonitasoft.engine.connector.EngineExecutionContext;

public class BonitaDocumentSource implements DocumentSource {

    public enum Metadatas {
        DOC_REF,
        FILE_NAME,
        AUTHOR,
        MIME_TYPE,
        DESCRIPTION,
        CREATION_DATE
    }

    private final Metadata metadata = new Metadata();
    private byte[] data = new byte[0];

    public BonitaDocumentSource(ProcessAPI processAPI, EngineExecutionContext executionContext, String docRef) {
        ValidationUtils.ensureNotNull(executionContext, "executionContext");
        ValidationUtils.ensureNotNull(processAPI, "processAPI");
        ValidationUtils.ensureNotNull(docRef, Metadatas.DOC_REF.name());
        metadata.put(Metadatas.DOC_REF.name(), docRef);
        if (!docRef.isEmpty()) {
            try {
                long processInstanceId = executionContext.getProcessInstanceId();
                Document document = processAPI.getLastDocument(processInstanceId, docRef);
                metadata.put(Metadatas.FILE_NAME.name(), document.getContentFileName());
                metadata.put(Metadatas.AUTHOR.name(), document.getAuthor());
                metadata.put(Metadatas.MIME_TYPE.name(), document.getContentMimeType());
                metadata.put(Metadatas.DESCRIPTION.name(), document.getDescription());
                metadata.put(
                        Metadatas.CREATION_DATE.name(),
                        document.getCreationDate().toString());
                data = processAPI.getDocumentContent(document.getContentStorageId());
            } catch (final DocumentNotFoundException e) {
                throw new AiConnectorException("Document not found for ref: " + docRef, e);
            }
        }
    }

    @Override
    public InputStream inputStream() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public Metadata metadata() {
        return metadata;
    }
}
