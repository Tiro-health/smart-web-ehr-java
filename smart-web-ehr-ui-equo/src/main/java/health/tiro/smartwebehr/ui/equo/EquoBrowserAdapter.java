package health.tiro.smartwebehr.ui.equo;

import com.equo.chromium.ChromiumBrowser;
import health.tiro.smartwebehr.ui.EmbeddedBrowser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * {@link EmbeddedBrowser} implementation backed by Equo Chromium.
 *
 * <p>Uses URL scheme interception ({@code swm://postMessage/}) for JS→Java messaging
 * and {@code window.swmReceiveMessage(...)} for Java→JS messaging.
 *
 * <pre>{@code
 * EmbeddedBrowser browser = new EquoBrowserAdapter();
 * }</pre>
 */
public class EquoBrowserAdapter implements EmbeddedBrowser {

    private static final Logger logger = LoggerFactory.getLogger(EquoBrowserAdapter.class);
    private static final String SWM_SCHEME = "swm://postMessage/";

    private final List<Runnable> pageLoadListeners = new CopyOnWriteArrayList<>();
    private ChromiumBrowser browser;
    private JPanel container;
    private Function<String, String> incomingMessageHandler;
    private Path tempFile;

    public EquoBrowserAdapter() {
    }

    public EquoBrowserAdapter(EquoBrowserConfig config) {
    }

    @Override
    public Component createComponent() {
        container = new JPanel(new BorderLayout());
        return container;
    }

    @Override
    public void loadUrl(String url) {
        String resolvedUrl = resolveUrl(url);

        if (browser == null) {
            browser = ChromiumBrowser.swing(container, BorderLayout.CENTER, resolvedUrl);
            setupUrlInterception();
            setupPageLoadListener();
            setupConsoleListener();
            logger.info("Equo Chromium browser created, loading: {}", resolvedUrl);
        } else {
            browser.setUrl(resolvedUrl);
        }
    }

    @Override
    public void executeJavaScript(String script) {
        if (browser != null) {
            browser.executeJavaScript(script);
        }
    }

    @Override
    public void sendMessage(String json) {
        sendToJsInternal(json);
    }

    @Override
    public void setIncomingMessageHandler(Function<String, String> handler) {
        this.incomingMessageHandler = handler;
    }

    @Override
    public void addPageLoadListener(Runnable callback) {
        pageLoadListeners.add(callback);
    }

    @Override
    public void dispose() {
        if (tempFile != null) {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException e) {
                logger.warn("Failed to delete temp file: {}", tempFile, e);
            }
        }
    }

    /**
     * Get the underlying {@link ChromiumBrowser} for advanced use cases.
     */
    public ChromiumBrowser getBrowser() {
        return browser;
    }

    // ========== Internal ==========

    private void setupUrlInterception() {
        browser.subscribe().onBeforeBrowse(event -> {
            String url = event.getUrl();
            if (url != null && url.startsWith(SWM_SCHEME)) {
                event.prevent();
                handleIncomingMessage(url);
            }
        });
    }

    private void setupPageLoadListener() {
        browser.subscribe().onLoadEnd(event -> {
            logger.info("Page load complete (status={})", event.getHttpStatusCode());
            browser.executeJavaScript(
                "if (!window.__equoHost) { " +
                "  window.__equoHost = true; " +
                "  console.log('[Equo] __equoHost flag injected'); " +
                "  if (window.SmartWebMessaging && typeof window.SmartWebMessaging.init === 'function') { " +
                "    window.SmartWebMessaging.init(); " +
                "  } " +
                "}"
            );
            for (Runnable listener : pageLoadListeners) {
                listener.run();
            }
        });
    }

    private void setupConsoleListener() {
        browser.addConsoleListener((level, message, source, line) -> {
            // CEF levels: 0=DEFAULT, 1=VERBOSE, 2=INFO(console.log), 3+=WARNING/ERROR
            if (level >= 3) {
                logger.warn("[JS] {}", message);
            } else {
                logger.debug("[JS] {}", message);
            }
            return false;
        });
    }

    private void handleIncomingMessage(String url) {
        try {
            String encoded = url.substring(SWM_SCHEME.length());
            String json = URLDecoder.decode(encoded, StandardCharsets.UTF_8.name());
            logger.debug("Received from JS: {}", json);

            if (incomingMessageHandler != null) {
                String responseJson = incomingMessageHandler.apply(json);
                if (responseJson != null) {
                    sendToJsInternal(responseJson);
                }
            }
        } catch (Exception e) {
            logger.error("Error handling message from: {}", url, e);
        }
    }

    private void sendToJsInternal(String json) {
        String escaped = json
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\n", "\\n")
            .replace("\r", "\\r");
        browser.executeJavaScript("window.swmReceiveMessage('" + escaped + "');");
    }

    /**
     * Resolve a URL for loading in CEF.
     * CEF cannot load jar: URLs, so classpath resources are extracted to temp files.
     */
    private String resolveUrl(String url) {
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file://")) {
            return url;
        }

        URL resource = getClass().getResource(url);
        if (resource == null) {
            return url;
        }

        if ("jar".equals(resource.getProtocol())) {
            try {
                tempFile = Files.createTempFile("smart-web-ehr-", ".html");
                tempFile.toFile().deleteOnExit();
                try (InputStream in = getClass().getResourceAsStream(url)) {
                    Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
                }
                return tempFile.toUri().toString();
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to extract resource: " + url, e);
            }
        }

        return resource.toExternalForm();
    }
}
