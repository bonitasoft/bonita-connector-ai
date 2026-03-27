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
package org.bonitasoft.connectors.ai.cohere;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Tests that the connector definition XML files (.def) are valid and consistent.
 * Definitions are in resources-filtered/ and contain Maven placeholders,
 * so we read them from the filesystem rather than the classpath.
 */
@DisplayName("Cohere Connector Definition Tests")
class CohereConnectorDefinitionTest {

    private static final Path RESOURCES_FILTERED = Path.of("src/main/resources-filtered");

    record DefFile(String fileName, String expectedIdPrefix) {}

    static Stream<Named<DefFile>> definitionFiles() {
        return Stream.of(
                Named.of("cohere-ask.def", new DefFile("cohere-ask.def", "cohere.ask")),
                Named.of("cohere-extract.def", new DefFile("cohere-extract.def", "cohere.extract")),
                Named.of("cohere-classify.def", new DefFile("cohere-classify.def", "cohere.classify")));
    }

    private Document parseDefFile(String fileName) throws Exception {
        Path defPath = RESOURCES_FILTERED.resolve(fileName);
        assertThat(defPath).as("Definition file '%s' should exist", fileName).exists();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        try (InputStream is = Files.newInputStream(defPath)) {
            return builder.parse(is);
        }
    }

    private List<String> getInputNames(Document doc) {
        List<String> names = new ArrayList<>();
        NodeList inputs = doc.getElementsByTagNameNS("*", "input");
        for (int i = 0; i < inputs.getLength(); i++) {
            names.add(((Element) inputs.item(i)).getAttribute("name"));
        }
        return names;
    }

    private List<String> getOutputNames(Document doc) {
        List<String> names = new ArrayList<>();
        NodeList outputs = doc.getElementsByTagNameNS("*", "output");
        for (int i = 0; i < outputs.getLength(); i++) {
            names.add(((Element) outputs.item(i)).getAttribute("name"));
        }
        return names;
    }

    @ParameterizedTest
    @MethodSource("definitionFiles")
    @DisplayName("Definition should have a valid connector ID placeholder")
    void should_have_valid_connector_id(DefFile defFile) throws Exception {
        Document doc = parseDefFile(defFile.fileName());
        NodeList ids = doc.getElementsByTagNameNS("*", "id");
        assertThat(ids.getLength()).isGreaterThan(0);
        String connectorId = ids.item(0).getTextContent();
        assertThat(connectorId)
                .as("Connector ID should be a Maven property placeholder")
                .contains(defFile.expectedIdPrefix());
    }

    @ParameterizedTest
    @MethodSource("definitionFiles")
    @DisplayName("Definition should have output parameter")
    void should_have_output(DefFile defFile) throws Exception {
        Document doc = parseDefFile(defFile.fileName());
        List<String> outputs = getOutputNames(doc);
        assertThat(outputs).contains("output");
    }

    @ParameterizedTest
    @MethodSource("definitionFiles")
    @DisplayName("Definition should have common AI input parameters")
    void should_have_common_ai_inputs(DefFile defFile) throws Exception {
        Document doc = parseDefFile(defFile.fileName());
        List<String> inputs = getInputNames(doc);
        assertThat(inputs).contains("url", "apiKey");
    }

    @ParameterizedTest
    @MethodSource("definitionFiles")
    @DisplayName("Definition should have at least 2 UI pages")
    void should_have_ui_pages(DefFile defFile) throws Exception {
        Document doc = parseDefFile(defFile.fileName());
        NodeList pages = doc.getElementsByTagNameNS("*", "page");
        assertThat(pages.getLength()).isGreaterThanOrEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("definitionFiles")
    @DisplayName("Widget inputs should reference valid input parameters")
    void should_reference_valid_inputs_in_widgets(DefFile defFile) throws Exception {
        Document doc = parseDefFile(defFile.fileName());
        Set<String> inputNames = new HashSet<>(getInputNames(doc));

        NodeList widgets = doc.getElementsByTagNameNS("*", "widget");
        for (int i = 0; i < widgets.getLength(); i++) {
            Element widget = (Element) widgets.item(i);
            String inputName = widget.getAttribute("inputName");
            if (inputName != null && !inputName.isBlank()) {
                assertThat(inputNames)
                        .as("Widget references input '%s' which should exist", inputName)
                        .contains(inputName);
            }
        }
    }

    @Test
    @DisplayName("Ask definition should have prompt inputs")
    void should_have_ask_prompt_inputs() throws Exception {
        Document doc = parseDefFile("cohere-ask.def");
        List<String> inputs = getInputNames(doc);
        assertThat(inputs).contains("userPrompt", "systemPrompt", "outputJsonSchema");
    }

    @Test
    @DisplayName("Extract definition should have extraction inputs")
    void should_have_extract_inputs() throws Exception {
        Document doc = parseDefFile("cohere-extract.def");
        List<String> inputs = getInputNames(doc);
        assertThat(inputs).contains("fieldsToExtract", "outputJsonSchema");
    }

    @Test
    @DisplayName("Classify definition should have classification inputs")
    void should_have_classify_inputs() throws Exception {
        Document doc = parseDefFile("cohere-classify.def");
        List<String> inputs = getInputNames(doc);
        assertThat(inputs).contains("sourceDocumentRef");
    }

    @ParameterizedTest
    @MethodSource("definitionFiles")
    @DisplayName("Definition should have AI category")
    void should_have_ai_category(DefFile defFile) throws Exception {
        Document doc = parseDefFile(defFile.fileName());
        NodeList categories = doc.getElementsByTagNameNS("*", "category");
        boolean hasAiCategory = false;
        for (int i = 0; i < categories.getLength(); i++) {
            Element cat = (Element) categories.item(i);
            if ("AI".equals(cat.getAttribute("id"))) {
                hasAiCategory = true;
            }
        }
        assertThat(hasAiCategory).as("Should have AI parent category").isTrue();
    }

    @ParameterizedTest
    @MethodSource("definitionFiles")
    @DisplayName("Definition should have CohereAI subcategory")
    void should_have_cohere_ai_category(DefFile defFile) throws Exception {
        Document doc = parseDefFile(defFile.fileName());
        NodeList categories = doc.getElementsByTagNameNS("*", "category");
        boolean hasCohereCategory = false;
        for (int i = 0; i < categories.getLength(); i++) {
            Element cat = (Element) categories.item(i);
            if ("CohereAI".equals(cat.getAttribute("id"))) {
                hasCohereCategory = true;
            }
        }
        assertThat(hasCohereCategory).as("Should have CohereAI subcategory").isTrue();
    }

    @ParameterizedTest
    @MethodSource("definitionFiles")
    @DisplayName("Definition should have icon")
    void should_have_icon(DefFile defFile) throws Exception {
        Document doc = parseDefFile(defFile.fileName());
        NodeList icons = doc.getElementsByTagNameNS("*", "icon");
        assertThat(icons.getLength()).isGreaterThan(0);
        assertThat(icons.item(0).getTextContent()).isNotBlank();
    }

    @Test
    @DisplayName("Connector classes should exist and be loadable")
    void should_have_loadable_connector_classes() {
        assertThat(CohereAskConnector.class).isNotNull();
        assertThat(CohereExtractDataConnector.class).isNotNull();
        assertThat(CohereClassifyConnector.class).isNotNull();
    }
}
