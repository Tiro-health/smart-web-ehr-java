package health.tiro.smartwebehr.ui;

/**
 * Configuration for {@link FormViewer}.
 * Use {@link #builder()} to create instances.
 *
 * <pre>{@code
 * FormViewerConfig config = FormViewerConfig.builder()
 *     .targetUrl("https://your-form-app.com/form-viewer.html")
 *     .handshakeTimeoutSeconds(30)
 *     .build();
 * }</pre>
 */
public class FormViewerConfig {

    private final String targetUrl;
    private final long handshakeTimeoutSeconds;

    private FormViewerConfig(Builder builder) {
        this.targetUrl = builder.targetUrl;
        this.handshakeTimeoutSeconds = builder.handshakeTimeoutSeconds;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public long getHandshakeTimeoutSeconds() {
        return handshakeTimeoutSeconds;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String targetUrl;
        private long handshakeTimeoutSeconds = 30;

        private Builder() {}

        /**
         * Set the URL to load in the embedded browser (required).
         */
        public Builder targetUrl(String targetUrl) {
            this.targetUrl = targetUrl;
            return this;
        }

        /**
         * Set the maximum time to wait for the JS handshake (default: 30 seconds).
         */
        public Builder handshakeTimeoutSeconds(long handshakeTimeoutSeconds) {
            this.handshakeTimeoutSeconds = handshakeTimeoutSeconds;
            return this;
        }

        public FormViewerConfig build() {
            if (targetUrl == null || targetUrl.trim().isEmpty()) {
                throw new IllegalArgumentException("targetUrl is required");
            }
            return new FormViewerConfig(this);
        }
    }
}
