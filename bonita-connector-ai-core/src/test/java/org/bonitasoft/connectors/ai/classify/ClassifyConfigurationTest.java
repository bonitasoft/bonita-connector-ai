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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ClassifyConfigurationTest {

    @Test
    void should_return_empty_list_when_no_document_refs() {
        var config = ClassifyConfiguration.builder().build();

        assertThat(config.getAllDocumentRefs()).isEmpty();
    }

    @Test
    void should_return_single_ref_when_only_sourceDocumentRef_set() {
        var config = ClassifyConfiguration.builder().sourceDocumentRef("doc1").build();

        assertThat(config.getAllDocumentRefs()).containsExactly("doc1");
    }

    @Test
    void should_return_list_when_only_sourceDocumentRefs_set() {
        var config = ClassifyConfiguration.builder()
                .sourceDocumentRefs(List.of("doc1", "doc2"))
                .build();

        assertThat(config.getAllDocumentRefs()).containsExactly("doc1", "doc2");
    }

    @Test
    void should_merge_and_deduplicate_refs() {
        var config = ClassifyConfiguration.builder()
                .sourceDocumentRef("doc1")
                .sourceDocumentRefs(List.of("doc1", "doc2", "doc3"))
                .build();

        assertThat(config.getAllDocumentRefs()).containsExactly("doc1", "doc2", "doc3");
    }

    @Test
    void should_ignore_blank_sourceDocumentRef() {
        var config = ClassifyConfiguration.builder()
                .sourceDocumentRef("  ")
                .sourceDocumentRefs(List.of("doc1"))
                .build();

        assertThat(config.getAllDocumentRefs()).containsExactly("doc1");
    }
}
