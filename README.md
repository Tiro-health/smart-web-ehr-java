# SMART Web EHR - Java

A Java port of the [SMARTWebEHR](https://github.com/Tiro-health/SMARTWebEHR) .NET library for handling SMART Web Messaging protocol in embedded browsers.

## Features

- Parse and handle SMART Web Messaging messages
- SDC (Structured Data Capture) operations
- Form submission handling
- Event-driven architecture
- Multi-version FHIR support (R4 and R5)

## Installation

Available on [Maven Central](https://repo1.maven.org/maven2/health/tiro/).

Pick the module matching your FHIR version:

**FHIR R5:**

```xml
<dependency>
    <groupId>health.tiro</groupId>
    <artifactId>smart-web-ehr-r5</artifactId>
    <version>2.0.0</version>
</dependency>
```

**FHIR R4:**

```xml
<dependency>
    <groupId>health.tiro</groupId>
    <artifactId>smart-web-ehr-r4</artifactId>
    <version>2.0.0</version>
</dependency>
```

Both modules transitively include `smart-web-ehr-core`, so you only need one dependency.

## Usage

### Basic Setup

```java
// R5 example — for R4, change the import to health.tiro.smartwebehr.r4.SmartMessageHandler
import health.tiro.smartwebehr.r5.SmartMessageHandler;
import health.tiro.smartwebehr.r5.R5SmartMessageListener;
import org.hl7.fhir.r5.model.*;

// Create handler
SmartMessageHandler handler = new SmartMessageHandler();

// Add event listeners (typed adapter — no casts needed)
handler.addListener(new R5SmartMessageListener() {
    @Override
    public void onFormSubmitted(QuestionnaireResponse response, OperationOutcome outcome) {
        // Process the submitted form — fully typed
    }

    @Override
    public void onCloseApplication(CloseApplicationEvent event) {
        // Close the form viewer
    }
});
```

### Integration with JxBrowser

```java
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.js.JsObject;

public class JxBrowserIntegration {

    private SmartMessageHandler handler;
    private Browser browser;

    public void setup() {
        handler = new SmartMessageHandler();

        // Set up message sender for outbound messages
        handler.setMessageSender(jsonMessage -> {
            CompletableFuture<String> future = new CompletableFuture<>();
            browser.mainFrame().ifPresent(frame -> {
                frame.executeJavaScript("window.postMessage(" + jsonMessage + ", '*')");
                future.complete(null);
            });
            return future;
        });

        // Inject message handler into browser
        browser.mainFrame().ifPresent(frame -> {
            JsObject window = frame.executeJavaScript("window");
            window.putProperty("smartMessageCallback", (String message) -> {
                String response = handler.handleMessage(message);
                if (response != null) {
                    frame.executeJavaScript("window.postMessage(" + response + ", '*')");
                }
            });
        });

        // Add listener for incoming messages
        browser.mainFrame().ifPresent(frame -> {
            frame.executeJavaScript(
                "window.addEventListener('message', function(event) {" +
                "  if (event.data && event.data.messageId) {" +
                "    window.smartMessageCallback(JSON.stringify(event.data));" +
                "  }" +
                "});"
            );
        });
    }
}
```

### Integration with Equo Chromium

```java
import com.nicepay.nicefx.browser.ChromiumBrowser;

public class EquoIntegration {

    private SmartMessageHandler handler;
    private ChromiumBrowser browser;

    public void setup() {
        handler = new SmartMessageHandler();

        // Set up message sender
        handler.setMessageSender(jsonMessage -> {
            CompletableFuture<String> future = new CompletableFuture<>();
            browser.executeJavaScript("window.postMessage(" + jsonMessage + ", '*')");
            future.complete(null);
            return future;
        });

        // Register Java function to be called from JavaScript
        browser.registerBrowserFunction("smartMessageCallback", args -> {
            if (args.length > 0) {
                String message = args[0].toString();
                String response = handler.handleMessage(message);
                if (response != null) {
                    browser.executeJavaScript("window.postMessage(" + response + ", '*')");
                }
            }
            return null;
        });

        // Set up message listener in browser
        browser.executeJavaScript(
            "window.addEventListener('message', function(event) {" +
            "  if (event.data && event.data.messageId) {" +
            "    smartMessageCallback(JSON.stringify(event.data));" +
            "  }" +
            "});"
        );
    }
}
```

### Sending SDC Messages

```java
// Display a questionnaire
Patient patient = new Patient();
patient.setId("patient-123");

handler.sendSdcDisplayQuestionnaireAsync(
    "http://example.org/Questionnaire/intake-form",  // canonical URL
    null,  // existing QuestionnaireResponse (optional)
    patient,
    null,  // Encounter (optional)
    null,  // Practitioner (optional)
    response -> {
        // Handle response
        System.out.println("Questionnaire displayed");
    }
);

// Request form submission
handler.sendFormRequestSubmitAsync(response -> {
    System.out.println("Form submission requested");
});

// Configure SDC context
handler.sendSdcConfigureContextAsync(
    patient,
    null,  // Encounter
    null,  // Practitioner/Author
    response -> {
        System.out.println("Context configured");
    }
);
```

## Module Structure

| Module | Artifact | Description |
|--------|----------|-------------|
| Core | `smart-web-ehr-core` | Shared logic, FHIR-version-independent. Depends on `hapi-fhir-base` only. |
| R4 | `smart-web-ehr-r4` | FHIR R4 typed API. Depends on core + `hapi-fhir-structures-r4`. |
| R5 | `smart-web-ehr-r5` | FHIR R5 typed API. Depends on core + `hapi-fhir-structures-r5`. |

## Message Types Supported

### Inbound (from WebView)
- `status.handshake` - Handshake from embedded app
- `form.submitted` - Form submission with QuestionnaireResponse
- `ui.done` - Application close request

### Outbound (to WebView)
- `ui.form.requestSubmit` - Request form submission
- `ui.form.persist` - Request form persistence
- `sdc.configure` - Configure SDC settings
- `sdc.configureContext` - Configure launch context
- `sdc.displayQuestionnaire` - Display a questionnaire

## Migrating from 1.x

```java
// Before (1.x)
import health.tiro.smartwebehr.SmartMessageHandler;

// After (2.x) — pick your FHIR version
import health.tiro.smartwebehr.r5.SmartMessageHandler;  // R5
import health.tiro.smartwebehr.r4.SmartMessageHandler;  // R4
```

The `SmartMessageHandler` API is unchanged. The only difference is the import and the Maven artifact.

For typed event access, use `R5SmartMessageListener` or `R4SmartMessageListener` instead of `SmartMessageListener`.

## Requirements

- Java 8 or higher

## Dependencies

- HAPI FHIR (5.7.0)
- Jackson Databind (2.15.3)
- SLF4J (2.0.9)
- Javax Validation API (2.0.1.Final)

## License

MIT License - same as the original .NET library.
