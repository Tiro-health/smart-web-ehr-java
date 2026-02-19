package health.tiro.formviewer.swing;

import org.hl7.fhir.instance.model.api.IBaseResource;

/**
 * Listener for {@link FormViewer} events.
 * All methods have default no-op implementations — override only what you need.
 *
 * <p>FHIR resources are returned as {@link IBaseResource} (version-agnostic).
 * Cast to your version's concrete types:
 * <pre>{@code
 * viewer.addFormViewerListener(new FormViewerListener() {
 *     @Override
 *     public void onFormSubmitted(IBaseResource response, IBaseResource outcome) {
 *         QuestionnaireResponse qr = (QuestionnaireResponse) response;
 *         // process
 *     }
 * });
 * }</pre>
 */
public interface FormViewerListener {

    /**
     * Called when the JS page completes the SMART Web Messaging handshake.
     */
    default void onHandshakeReceived() {}

    /**
     * Called when the user submits a form in the browser.
     *
     * @param response the submitted QuestionnaireResponse (as IBaseResource)
     * @param outcome  the OperationOutcome (as IBaseResource), may be null
     */
    default void onFormSubmitted(IBaseResource response, IBaseResource outcome) {}

    /**
     * Called when the browser app requests to close (ui.done message).
     */
    default void onCloseRequested() {}
}
