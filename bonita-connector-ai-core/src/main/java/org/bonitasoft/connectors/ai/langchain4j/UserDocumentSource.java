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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.bonitasoft.connectors.ai.UserDocument;

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
