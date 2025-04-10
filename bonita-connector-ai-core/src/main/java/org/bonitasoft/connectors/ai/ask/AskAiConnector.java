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
package org.bonitasoft.connectors.ai.ask;

import static org.bonitasoft.connectors.ai.ask.AskConfiguration.*;

import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.connectors.ai.AiConnector;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

@Slf4j
public abstract class AskAiConnector extends AiConnector {

    protected AskConfiguration askConfiguration;
    protected AskChat chat;

    @Override
    protected void validateConfiguration() throws ConnectorValidationException {
        var builder = AskConfiguration.builder();
        getInputValue(SYSTEM_PROMPT, String.class).ifPresent(builder::systemPrompt);
        getInputValue(USER_PROMPT, String.class).ifPresent(builder::userPrompt);
        getInputValue(SOURCE_DOCUMENT_REF, String.class).ifPresent(builder::sourceDocumentRef);
        getInputValue(OUTPUT_JSON_SCHEMA, String.class).ifPresent(builder::outputJsonSchema);
        this.askConfiguration = builder.build();

        // Specific validation
        if (askConfiguration.getUserPrompt() == null) {
            throw new ConnectorValidationException("UserPrompt is required");
        }
        if (askConfiguration.getUserPrompt().isBlank()) {
            throw new ConnectorValidationException("UserPrompt is blank or empty");
        }
    }

    /**
     * @return
     * @throws ConnectorException
     */
    @Override
    protected Object doExecute() throws ConnectorException {

        // Try to read doc if any
        UserDocument userDocument = askConfiguration
                .getSourceDocumentRef()
                .map(this::getUserDocument)
                .orElse(null);

        return chat.ask(
                askConfiguration.getSystemPrompt(),
                askConfiguration.getUserPrompt(),
                askConfiguration.getOutputJsonSchema().orElse(null),
                userDocument);
    }
}
