package org.bonitasoft.connectors.openai.classify;

import static org.bonitasoft.connectors.openai.classify.ClassifyConfiguration.CATEGORIES;
import static org.bonitasoft.connectors.openai.classify.ClassifyConfiguration.SOURCE_DOCUMENT_REF;

import java.util.List;
import org.bonitasoft.connectors.openai.OpenAiConnector;
import org.bonitasoft.connectors.openai.doc.UserDocument;
import org.bonitasoft.engine.connector.ConnectorValidationException;

public class OpenAiClassifyConnector extends OpenAiConnector {

    private ClassifyChat chat;
    private ClassifyConfiguration classifyConfiguration;

    @Override
    protected void validateConfiguration() throws ConnectorValidationException {
        if (classifyConfiguration == null) {
            var builder = ClassifyConfiguration.builder();
            getInputValue(SOURCE_DOCUMENT_REF, String.class).ifPresent(builder::sourceDocumentRef);
            getInputValue(CATEGORIES, List.class).ifPresent(builder::categories);
            classifyConfiguration = builder.build();
        }
        if (classifyConfiguration.getSourceDocumentRef() == null
                || classifyConfiguration.getSourceDocumentRef().isEmpty()) {
            throw new ConnectorValidationException("Source document ref is empty");
        }
        if (classifyConfiguration.getCategories() == null
                || classifyConfiguration.getCategories().isEmpty()) {
            throw new ConnectorValidationException("Classification categories are empty");
        }
    }

    @Override
    public void connect() {
        chat = new OpenAiClassifyChat(configuration);
    }

    @Override
    protected Object doExecute() {
        UserDocument userDocument = getUserDocument(classifyConfiguration.getSourceDocumentRef());
        return chat.classify(classifyConfiguration.getCategories(), userDocument);
    }
}
