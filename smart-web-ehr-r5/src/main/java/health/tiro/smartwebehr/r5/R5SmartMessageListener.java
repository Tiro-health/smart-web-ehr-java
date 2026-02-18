package health.tiro.smartwebehr.r5;

import health.tiro.smartwebehr.events.*;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.QuestionnaireResponse;

/**
 * A convenience listener adapter for R5 that provides typed access
 * to FHIR resources in events. Extend this instead of SmartMessageListener
 * to get R5-typed QuestionnaireResponse and OperationOutcome.
 *
 * <pre>{@code
 * handler.addListener(new R5SmartMessageListener() {
 *     @Override
 *     public void onFormSubmitted(QuestionnaireResponse response, OperationOutcome outcome) {
 *         // fully typed, no casts needed
 *     }
 * });
 * }</pre>
 */
public abstract class R5SmartMessageListener implements SmartMessageListener {

    @Override
    public final void onFormSubmitted(FormSubmittedEvent event) {
        onFormSubmitted(
            (QuestionnaireResponse) event.getResponse(),
            event.getOutcome() != null ? (OperationOutcome) event.getOutcome() : null
        );
    }

    /**
     * Called when a form is submitted, with R5-typed resources.
     */
    public void onFormSubmitted(QuestionnaireResponse response, OperationOutcome outcome) {
        // Default no-op; override to handle
    }

    @Override
    public void onCloseApplication(CloseApplicationEvent event) {
        // Default no-op; override to handle
    }

    @Override
    public void onHandshakeReceived(HandshakeReceivedEvent event) {
        // Default no-op; override to handle
    }
}
