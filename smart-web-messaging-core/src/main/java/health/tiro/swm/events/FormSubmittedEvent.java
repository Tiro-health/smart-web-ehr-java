package health.tiro.swm.events;

import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.EventObject;

/**
 * Event fired when a form is submitted with validation outcome.
 */
public class FormSubmittedEvent extends EventObject {

    private final IBaseResource response;
    private final IBaseResource outcome;

    public FormSubmittedEvent(Object source, IBaseResource response, IBaseResource outcome) {
        super(source);
        this.response = response;
        this.outcome = outcome;
    }

    public IBaseResource getResponse() {
        return response;
    }

    public IBaseResource getOutcome() {
        return outcome;
    }
}
