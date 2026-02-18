package health.tiro.smartwebehr.message.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;

/**
 * Launch context for SDC operations.
 */
public class LaunchContext {

    @NotNull
    @JsonProperty("name")
    private String name;

    @JsonProperty("contentReference")
    private IBaseReference contentReference;

    @JsonProperty("contentResource")
    private IBaseResource contentResource;

    public LaunchContext() {
    }

    public LaunchContext(String name) {
        this(name, null, null);
    }

    public LaunchContext(String name, IBaseReference contentReference, IBaseResource contentResource) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("LaunchContext name is required");
        }
        this.name = name;
        this.contentReference = contentReference;
        this.contentResource = contentResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IBaseReference getContentReference() {
        return contentReference;
    }

    public void setContentReference(IBaseReference contentReference) {
        this.contentReference = contentReference;
    }

    public IBaseResource getContentResource() {
        return contentResource;
    }

    public void setContentResource(IBaseResource contentResource) {
        this.contentResource = contentResource;
    }
}
