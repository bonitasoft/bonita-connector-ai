package org.bonitasoft.connectors.ai.classify;

import static org.bonitasoft.connectors.ai.classify.ClassifyConfiguration.CATEGORY_LIST;
import static org.bonitasoft.connectors.ai.classify.ClassifyConfiguration.SOURCE_DOCUMENT_REF;

import java.util.List;
import org.bonitasoft.connectors.ai.AiConnector;
import org.bonitasoft.connectors.ai.doc.UserDocument;
import org.bonitasoft.engine.connector.ConnectorValidationException;

public class AiClassifyConnector<T extends ClassifyChat> extends AiConnector {

    protected ClassifyConfiguration classifyConfiguration;
    protected T chat;

    @Override
    protected void validateConfiguration() throws ConnectorValidationException {
        if (classifyConfiguration == null) {
            var builder = ClassifyConfiguration.builder();
            getInputValue(SOURCE_DOCUMENT_REF, String.class).ifPresent(builder::sourceDocumentRef);
            getInputValue(CATEGORY_LIST, List.class).ifPresent(builder::categories);
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
    protected Object doExecute() {
        UserDocument userDocument = getUserDocument(classifyConfiguration.getSourceDocumentRef());
        return chat.classify(classifyConfiguration.getCategories(), userDocument);
    }
}
