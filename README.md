# SMART Web EHR - Java

A Java port of the [SMARTWebEHR](https://github.com/Tiro-health/SMARTWebEHR) .NET library for handling SMART Web Messaging protocol in embedded browsers.

## Features

- Parse and handle SMART Web Messaging messages
- SDC (Structured Data Capture) operations
- Form submission handling
- Event-driven architecture
- Multi-version FHIR support (R4 and R5)
- Embedded browser UI modules (JxBrowser and Equo Chromium adapters)

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

### Embedded Browser (FormViewerPanel)

The easiest way to embed a SMART Web Messaging browser is using `FormViewerPanel` with a browser adapter:

```java
import health.tiro.smartwebehr.r5.SmartMessageHandler;
import health.tiro.smartwebehr.ui.*;
import health.tiro.smartwebehr.ui.jxbrowser.*;  // or .equo.*

// 1. Create a browser adapter (pick one)
EmbeddedBrowser browser = new JxBrowserAdapter(
    JxBrowserConfig.builder().licenseKey("YOUR-KEY").build()
);
// OR: new EquoBrowserAdapter()

// 2. Create the FHIR handler (pick your version)
SmartMessageHandler handler = new SmartMessageHandler();

// 3. Create the panel
FormViewerConfig config = FormViewerConfig.builder()
    .targetUrl("https://your-form-app.com/form-viewer.html")
    .build();
FormViewerPanel panel = new FormViewerPanel(config, browser, handler);

// 4. Listen for events
panel.addFormViewerListener(new FormViewerListener() {
    @Override
    public void onFormSubmitted(IBaseResource response, IBaseResource outcome) {
        QuestionnaireResponse qr = (QuestionnaireResponse) response;
        // process the submitted form
    }

    @Override
    public void onCloseRequested() {
        // close the window
    }
});

// 5. Add to your Swing UI
frame.add(panel, BorderLayout.CENTER);

// 6. Display a questionnaire (waits for handshake automatically)
panel.waitForHandshake().thenRun(() -> {
    handler.sendSdcDisplayQuestionnaireAsync(
        "http://example.org/Questionnaire/intake",
        null, patient, encounter, author, null
    );
});
```

Install the UI module matching your browser engine:

**JxBrowser:**

```xml
<dependency>
    <groupId>health.tiro</groupId>
    <artifactId>smart-web-ehr-ui-jxbrowser</artifactId>
    <version>2.0.0</version>
</dependency>
```

**Equo Chromium:**

```xml
<dependency>
    <groupId>health.tiro</groupId>
    <artifactId>smart-web-ehr-ui-equo</artifactId>
    <version>2.0.0</version>
</dependency>
```

Both transitively include `smart-web-ehr-ui` and `smart-web-ehr-core`. You also need the R4 or R5 module for your FHIR handler, plus the browser engine dependency itself (JxBrowser or Equo Chromium).

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
| UI | `smart-web-ehr-ui` | `FormViewerPanel` (JPanel) + `EmbeddedBrowser` interface. Depends on core. |
| UI JxBrowser | `smart-web-ehr-ui-jxbrowser` | JxBrowser adapter. Depends on ui + JxBrowser (provided). |
| UI Equo | `smart-web-ehr-ui-equo` | Equo Chromium adapter. Depends on ui + Equo Chromium (provided). |

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

## JS Bridge Contract

The UI adapters expect the web page to include a SMART Web Messaging bridge script that supports multiple transports. The bridge JS should:

1. **Define `window.SmartWebMessaging = { init: init }`** — the adapters call `SmartWebMessaging.init()` after setting up their transport.

2. **Define `window.swmReceiveMessage(json)`** in `init()` — used by Java to send messages to JS.

3. **Detect transports** in `sendMessage()`:
   - `window.javaBridge` (JxBrowser) — call `window.javaBridge.postMessage(json)`
   - `window.__equoHost` (Equo) — navigate to `swm://postMessage/<encoded-json>`
   - `window.chrome.webview` (WebView2) — call `chrome.webview.postMessage(msg)`
   - `window.parent !== window` (iframe) — call `parent.postMessage(msg, '*')`

For **Equo Chromium**, the HTML page must include `<script>window.__equoHost = true;</script>` before the bridge script so the bridge can detect the Equo transport at init time.

For **JxBrowser**, the adapter injects `window.javaBridge` after page load and calls `SmartWebMessaging.init()` to re-initialize the bridge.

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
