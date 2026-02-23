package health.tiro.formfiller.swing.equo;

/**
 * Configuration for {@link EquoBrowserAdapter}.
 * Currently minimal — may be extended with additional settings.
 */
public class EquoBrowserConfig {

    private EquoBrowserConfig(Builder builder) {
    }

    public EquoBrowserConfig() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Builder() {}

        public EquoBrowserConfig build() {
            return new EquoBrowserConfig(this);
        }
    }
}
