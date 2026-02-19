package health.tiro.swm.message.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hl7.fhir.instance.model.api.IBaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Payload for sdc.configureContext requests.
 */
public class SdcConfigureContext extends RequestPayload {

    @JsonProperty("subject")
    private IBaseReference subject;

    @JsonProperty("author")
    private IBaseReference author;

    @JsonProperty("encounter")
    private IBaseReference encounter;

    @JsonProperty("launchContext")
    private List<LaunchContext> launchContext;

    public SdcConfigureContext() {
        super();
        this.launchContext = new ArrayList<>();
    }

    public SdcConfigureContext(IBaseReference subject, IBaseReference author, IBaseReference encounter, List<LaunchContext> launchContext) {
        this.subject = subject;
        this.author = author;
        this.encounter = encounter;
        this.launchContext = launchContext != null ? launchContext : new ArrayList<>();
    }

    public IBaseReference getSubject() {
        return subject;
    }

    public void setSubject(IBaseReference subject) {
        this.subject = subject;
    }

    public IBaseReference getAuthor() {
        return author;
    }

    public void setAuthor(IBaseReference author) {
        this.author = author;
    }

    public IBaseReference getEncounter() {
        return encounter;
    }

    public void setEncounter(IBaseReference encounter) {
        this.encounter = encounter;
    }

    public List<LaunchContext> getLaunchContext() {
        return launchContext;
    }

    public void setLaunchContext(List<LaunchContext> launchContext) {
        this.launchContext = launchContext;
    }
}
