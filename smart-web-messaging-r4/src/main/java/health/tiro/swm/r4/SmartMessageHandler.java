package health.tiro.swm.r4;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import health.tiro.swm.AbstractSmartMessageHandler;
import health.tiro.swm.message.SmartMessageResponse;
import health.tiro.swm.message.payload.LaunchContext;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * FHIR R4 handler for SMART Web Messaging protocol.
 * Provides typed convenience methods accepting R4 model classes.
 */
public class SmartMessageHandler extends AbstractSmartMessageHandler {

    public SmartMessageHandler() {
        this(null, null);
    }

    public SmartMessageHandler(ObjectMapper customObjectMapper) {
        this(customObjectMapper, null);
    }

    /**
     * Creates a new SmartMessageHandler with optional custom ObjectMapper and FhirContext.
     *
     * @param customObjectMapper Custom ObjectMapper to use, or null for the default
     * @param customFhirContext  Custom FhirContext to use, or null to use the cached R4 context
     */
    public SmartMessageHandler(ObjectMapper customObjectMapper, FhirContext customFhirContext) {
        super(customFhirContext != null ? customFhirContext : FhirContext.forR4Cached(), customObjectMapper);
    }

    @Override
    protected ObjectNode serializeReference(IBaseReference ref) {
        Reference r4Ref = (Reference) ref;
        ObjectNode node = getObjectMapper().createObjectNode();
        if (r4Ref.getReference() != null) {
            node.put("reference", r4Ref.getReference());
        }
        if (r4Ref.getType() != null) {
            node.put("type", r4Ref.getType());
        }
        if (r4Ref.getDisplay() != null) {
            node.put("display", r4Ref.getDisplay());
        }
        return node;
    }

    // ========== Typed convenience methods ==========

    public CompletableFuture<String> sendSdcConfigureContextAsync(
            Patient patient,
            Encounter encounter,
            Practitioner author,
            Consumer<SmartMessageResponse> responseHandler) {

        logger.debug("Sending sdc.configureContext message with FHIR resources.");

        List<LaunchContext> launchContext = new ArrayList<>();
        if (patient != null) {
            launchContext.add(new LaunchContext("patient", null, patient));
        }
        if (encounter != null) {
            launchContext.add(new LaunchContext("encounter", null, encounter));
        }
        if (author != null) {
            launchContext.add(new LaunchContext("user", null, author));
        }

        return sendSdcConfigureContextAsync(null, null, null, launchContext, responseHandler);
    }

    public CompletableFuture<String> sendSdcDisplayQuestionnaireAsync(
            Questionnaire questionnaire,
            QuestionnaireResponse questionnaireResponse,
            Patient patient,
            Encounter encounter,
            Practitioner author,
            Consumer<SmartMessageResponse> responseHandler) {

        logger.debug("Sending sdc.displayQuestionnaire message with FHIR resources.");

        List<LaunchContext> launchContext = new ArrayList<>();
        if (patient != null) {
            launchContext.add(new LaunchContext("patient", null, patient));
        }
        if (encounter != null) {
            launchContext.add(new LaunchContext("encounter", null, encounter));
        }
        if (author != null) {
            launchContext.add(new LaunchContext("user", null, author));
        }

        return sendSdcDisplayQuestionnaireAsync(questionnaire, questionnaireResponse, null, null, null, launchContext, responseHandler);
    }

    public CompletableFuture<String> sendSdcDisplayQuestionnaireAsync(
            String questionnaireCanonicalUrl,
            QuestionnaireResponse questionnaireResponse,
            Patient patient,
            Encounter encounter,
            Practitioner author,
            Consumer<SmartMessageResponse> responseHandler) {

        logger.debug("Sending sdc.displayQuestionnaire message with canonical URL.");

        List<LaunchContext> launchContext = new ArrayList<>();
        if (patient != null) {
            launchContext.add(new LaunchContext("patient", null, patient));
        }
        if (encounter != null) {
            launchContext.add(new LaunchContext("encounter", null, encounter));
        }
        if (author != null) {
            launchContext.add(new LaunchContext("user", null, author));
        }

        return sendSdcDisplayQuestionnaireAsync(questionnaireCanonicalUrl, questionnaireResponse, null, null, null, launchContext, responseHandler);
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SmartMessageHandler.class);
}
