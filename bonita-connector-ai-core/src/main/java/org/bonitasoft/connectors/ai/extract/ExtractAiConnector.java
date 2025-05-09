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
package org.bonitasoft.connectors.ai.extract;

import static org.bonitasoft.connectors.ai.extract.ExtractConfiguration.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConnector;
import org.bonitasoft.connectors.ai.AiConnectorException;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

@Slf4j
public abstract class ExtractAiConnector<T extends ExtractChat> extends AiConnector {

    protected ExtractConfiguration extractConfiguration;
    protected T chat;

    @Override
    protected void validateConfiguration() throws ConnectorValidationException {
        var builder = ExtractConfiguration.builder();
        getInputValue(SOURCE_DOCUMENT_REF, String.class).ifPresent(builder::sourceDocumentRef);
        getInputValue(OUTPUT_JSON_SCHEMA, String.class).ifPresent(builder::outputJsonSchema);
        getInputValue(FIELD_LIST, List.class).ifPresent(builder::fieldsToExtract);
        extractConfiguration = builder.build();

        if (extractConfiguration.getSourceDocumentRef().isEmpty()) {
            throw new ConnectorValidationException("Source document ref is empty");
        }
        if (extractConfiguration.getFieldsToExtract().isEmpty()
                && extractConfiguration.getOutputJsonSchema().isEmpty()) {
            throw new ConnectorValidationException("Either field list or a jsonschema must be provided");
        }
    }

    /**
     * @return
     * @throws ConnectorException
     */
    @Override
    protected Object doExecute() throws ConnectorException {
        // Read doc
        UserDocument userDocument = getUserDocument(extractConfiguration.getSourceDocumentRef());

        var output = extractConfiguration
                .getOutputJsonSchema()
                .map(jsonSchema -> chat.extract(userDocument, jsonSchema))
                .or(() -> extractConfiguration.getFieldsToExtract().map(fields -> chat.extract(userDocument, fields)));

        return output.orElseThrow(() -> new AiConnectorException("Fields to extract or JSON schema is missing."));
    }
}
