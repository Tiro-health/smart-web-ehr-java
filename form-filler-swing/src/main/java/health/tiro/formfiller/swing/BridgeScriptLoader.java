package health.tiro.formfiller.swing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Loads the SWM bridge JavaScript from the classpath.
 * The script is cached after first load.
 */
public final class BridgeScriptLoader {

    private static final String RESOURCE_PATH =
        "health/tiro/formfiller/swing/tiro-swm-bridge.js";

    private static volatile String cachedScript;

    private BridgeScriptLoader() {}

    /**
     * Returns the bridge JS source code, loading from classpath on first call.
     */
    public static String getScript() {
        if (cachedScript != null) return cachedScript;
        synchronized (BridgeScriptLoader.class) {
            if (cachedScript != null) return cachedScript;
            try (InputStream is = BridgeScriptLoader.class.getClassLoader()
                    .getResourceAsStream(RESOURCE_PATH)) {
                if (is == null) {
                    throw new IllegalStateException(
                        "Bridge script not found on classpath: " + RESOURCE_PATH);
                }
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                cachedScript = sb.toString();
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load bridge script", e);
            }
            return cachedScript;
        }
    }
}
