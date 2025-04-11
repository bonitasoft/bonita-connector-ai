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
package org.bonitasoft.connectors.ai;

import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.ChatLanguageModel;
import java.util.Base64;
import org.bonitasoft.connectors.ai.langchain4j.UserDocumentSource;

public abstract class AbstractAiChat<T extends ChatLanguageModel> implements AiChat<T> {

    protected ChatMessage newDocMessage(UserDocument document) {
        return UserMessage.from(switch (document.mimeType()) {
            case "image/png", "image/jpg", "image/jpeg" ->
                    ImageContent.from(Base64.getEncoder().encodeToString(document.data()), document.mimeType());
            default -> {
                // Default to Tika parser support and extracting text.
                var doc = DocumentLoader.load(new UserDocumentSource(document), new ApacheTikaDocumentParser());
                yield TextContent.from(doc.text());
            }
        });
    }
}
