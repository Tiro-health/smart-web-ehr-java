package health.tiro.smartwebehr.message.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Payload for sdc.displayQuestionnaire requests.
 */
public class SdcDisplayQuestionnaire extends RequestPayload {

    @NotNull(message = "Questionnaire is mandatory.")
    @JsonProperty("questionnaire")
    private Object questionnaire; // Can be String (canonical URL), IBaseReference, or IBaseResource

    @JsonProperty("questionnaireResponse")
    private IBaseResource questionnaireResponse;

    @JsonProperty("context")
    private SdcDisplayQuestionnaireContext context;

    public SdcDisplayQuestionnaire() {
        super();
    }

    public SdcDisplayQuestionnaire(Object questionnaire, IBaseResource questionnaireResponse, SdcDisplayQuestionnaireContext context) {
        this.questionnaire = questionnaire;
        this.questionnaireResponse = questionnaireResponse;
        this.context = context;
    }

    public Object getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Object questionnaire) {
        this.questionnaire = questionnaire;
    }

    public IBaseResource getQuestionnaireResponse() {
        return questionnaireResponse;
    }

    public void setQuestionnaireResponse(IBaseResource questionnaireResponse) {
        this.questionnaireResponse = questionnaireResponse;
    }

    public SdcDisplayQuestionnaireContext getContext() {
        return context;
    }

    public void setContext(SdcDisplayQuestionnaireContext context) {
        this.context = context;
    }

    /**
     * Context for sdc.displayQuestionnaire.
     */
    public static class SdcDisplayQuestionnaireContext {

        @JsonProperty("subject")
        private IBaseReference subject;

        @JsonProperty("author")
        private IBaseReference author;

        @JsonProperty("encounter")
        private IBaseReference encounter;

        @JsonProperty("launchContext")
        private List<LaunchContext> launchContext;

        public SdcDisplayQuestionnaireContext() {
            this.launchContext = new ArrayList<>();
        }

        public SdcDisplayQuestionnaireContext(IBaseReference subject, IBaseReference author, IBaseReference encounter, List<LaunchContext> launchContext) {
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
}
