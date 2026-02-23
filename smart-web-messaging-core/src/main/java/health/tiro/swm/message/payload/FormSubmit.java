package health.tiro.swm.message.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import org.hl7.fhir.instance.model.api.IBaseResource;

/**
 * Payload for form.submitted requests.
 */
public class FormSubmit extends RequestPayload {

    @NotNull(message = "Outcome is mandatory.")
    @JsonProperty("outcome")
    private IBaseResource outcome;

    @NotNull(message = "Response is mandatory.")
    @JsonProperty("response")
    private IBaseResource response;

    public FormSubmit() {
        super();
    }

    public FormSubmit(IBaseResource outcome, IBaseResource response) {
        this.outcome = outcome;
        this.response = response;
    }

    public IBaseResource getOutcome() {
        return outcome;
    }

    public void setOutcome(IBaseResource outcome) {
        this.outcome = outcome;
    }

    public IBaseResource getResponse() {
        return response;
    }

    public void setResponse(IBaseResource response) {
        this.response = response;
    }
}
