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
package org.bonitasoft.connectors.ai.azure;

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
@DisplayName("Azure OpenAI Connector Definition Tests")
class AzureOpenAiConnectorDefinitionTest {

    private static final Path RESOURCES_FILTERED = Path.of("src/main/resources-filtered");

    record DefFile(String fileName, String expectedIdPrefix) {}

    static Stream<Named<DefFile>> definitionFiles() {
        return Stream.of(
                Named.of("azure-ask.def", new DefFile("azure-ask.def", "azure.ask")),
                Named.of("azure-extract.def", new DefFile("azure-extract.def", "azure.extract")),
                Named.of("azure-classify.def", new DefFile("azure-classify.def", "azure.classify")));
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
    @DisplayName("Definition should have Azure-specific apiVersion input")
    void should_have_api_version_input(DefFile defFile) throws Exception {
        Document doc = parseDefFile(defFile.fileName());
        List<String> inputs = getInputNames(doc);
        assertThat(inputs).contains("apiVersion");
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
    @DisplayName("All page widget inputs should reference existing inputs")
    void should_reference_valid_inputs_in_widgets(DefFile defFile) throws Exception {
        Document doc = parseDefFile(defFile.fileName());
        List<String> inputNames = getInputNames(doc);

        Set<String> widgetInputNames = new HashSet<>();
        NodeList widgets = doc.getElementsByTagNameNS("*", "widget");
        for (int i = 0; i < widgets.getLength(); i++) {
            Element widget = (Element) widgets.item(i);
            String inputName = widget.getAttribute("inputName");
            if (inputName != null && !inputName.isBlank()) {
                widgetInputNames.add(inputName);
            }
        }

        for (String widgetInput : widgetInputNames) {
            assertThat(inputNames)
                    .as("Widget input '%s' should reference a declared input", widgetInput)
                    .contains(widgetInput);
        }
    }

    @Test
    @DisplayName("Ask definition should have prompt inputs")
    void should_have_ask_prompt_inputs() throws Exception {
        Document doc = parseDefFile("azure-ask.def");
        List<String> inputs = getInputNames(doc);
        assertThat(inputs).contains("userPrompt", "systemPrompt", "outputJsonSchema");
    }

    @Test
    @DisplayName("Ask definition should have mandatory url input")
    void should_have_ask_mandatory_url() throws Exception {
        Document doc = parseDefFile("azure-ask.def");
        NodeList inputs = doc.getElementsByTagNameNS("*", "input");
        for (int i = 0; i < inputs.getLength(); i++) {
            Element input = (Element) inputs.item(i);
            if ("url".equals(input.getAttribute("name"))) {
                assertThat(input.getAttribute("mandatory")).isEqualTo("true");
            }
        }
    }

    @Test
    @DisplayName("Extract definition should have extraction inputs")
    void should_have_extract_inputs() throws Exception {
        Document doc = parseDefFile("azure-extract.def");
        List<String> inputs = getInputNames(doc);
        assertThat(inputs).contains("fieldsToExtract", "outputJsonSchema");
    }

    @Test
    @DisplayName("Classify definition should have document ref input")
    void should_have_classify_inputs() throws Exception {
        Document doc = parseDefFile("azure-classify.def");
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
        assertThat(AzureOpenAiAskConnector.class).isNotNull();
        assertThat(AzureOpenAiExtractDataConnector.class).isNotNull();
        assertThat(AzureOpenAiClassifyConnector.class).isNotNull();
    }
}
