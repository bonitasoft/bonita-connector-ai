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

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for working with document references across configuration classes.
 */
public final class DocumentRefUtils {

    private DocumentRefUtils() {
        // utility class
    }

    /**
     * Merge a single document reference and a list of document references into a single
     * deduplicated list.
     *
     * @param sourceDocumentRef  a single document reference (may be null or blank)
     * @param sourceDocumentRefs a list of document references (may be null)
     * @return a deduplicated list of non-blank document references
     */
    public static List<String> getAllDocumentRefs(String sourceDocumentRef, List<String> sourceDocumentRefs) {
        var refs = new ArrayList<String>();
        if (sourceDocumentRef != null && !sourceDocumentRef.isBlank()) {
            refs.add(sourceDocumentRef);
        }
        if (sourceDocumentRefs != null) {
            refs.addAll(sourceDocumentRefs);
        }
        return refs.stream().distinct().toList();
    }
}
