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
package org.bonitasoft.connectors.ai.classify;

import static org.bonitasoft.connectors.ai.classify.ClassifyConfiguration.*;

import java.util.List;
import org.bonitasoft.connectors.ai.AiConnector;
import org.bonitasoft.connectors.ai.UserDocument;
import org.bonitasoft.engine.connector.ConnectorValidationException;

public class ClassifyAiConnector<T extends ClassifyChat> extends AiConnector {

    protected ClassifyConfiguration classifyConfiguration;
    protected T chat;

    @Override
    protected void validateConfiguration() throws ConnectorValidationException {
        if (classifyConfiguration == null) {
            var builder = ClassifyConfiguration.builder();
            getInputValue(SOURCE_DOCUMENT_REF, String.class).ifPresent(builder::sourceDocumentRef);
            getInputValue(SOURCE_DOCUMENT_REFS, List.class).ifPresent(builder::sourceDocumentRefs);
            getInputValue(CATEGORY_LIST, List.class).ifPresent(builder::categories);
            getInputValue(CATEGORY_DESCRIPTIONS, String.class).ifPresent(builder::categoryDescriptions);
            classifyConfiguration = builder.build();
        }
        if (classifyConfiguration.getAllDocumentRefs().isEmpty()) {
            throw new ConnectorValidationException("Source document ref is empty");
        }
        if (classifyConfiguration.getCategories() == null
                || classifyConfiguration.getCategories().isEmpty()) {
            throw new ConnectorValidationException("Classification categories are empty");
        }
    }

    @Override
    protected Object doExecute() {
        List<UserDocument> userDocuments = getUserDocuments(classifyConfiguration.getAllDocumentRefs());
        List<String> categories = classifyConfiguration.getCategories();
        String descriptions = classifyConfiguration.getCategoryDescriptions();
        if (descriptions != null && !descriptions.isBlank()) {
            // Enrich categories with descriptions for better classification
            categories = enrichCategoriesWithDescriptions(categories, descriptions);
        }
        return chat.classify(categories, userDocuments);
    }

    private List<String> enrichCategoriesWithDescriptions(List<String> categories, String descriptionsJson) {
        try {
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var descriptionsMap = mapper.readValue(descriptionsJson, java.util.Map.class);
            return categories.stream()
                    .map(cat -> {
                        Object desc = descriptionsMap.get(cat);
                        return desc != null ? cat + " (" + desc + ")" : cat;
                    })
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            // If parsing fails, return original categories
            return categories;
        }
    }
}
