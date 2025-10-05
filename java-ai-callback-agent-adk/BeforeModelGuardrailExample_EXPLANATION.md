# BeforeModelGuardrailExample - Comprehensive Code Explanation

## Overview
This document provides a detailed explanation of the `BeforeModelGuardrailExample` class, which demonstrates the **before_model_callback** functionality in Google's Agent Development Kit (ADK). This advanced feature allows you to inspect and modify LLM requests before they are sent to the model, enabling sophisticated content filtering, request modification, and guardrail implementation.

---

## Key Concepts

### **What is a Before-Model Callback?**
A **before_model_callback** is a function that executes **before** a request is sent to the LLM model but **after** the agent has prepared the request. This allows for:

- **Request Inspection**: Examine the complete LLM request including user messages and system instructions
- **Request Modification**: Alter the request content, configuration, or system instructions
- **Content Filtering**: Block requests containing inappropriate or restricted content
- **Guardrail Implementation**: Enforce safety policies and content restrictions
- **Request Enhancement**: Add or modify system instructions dynamically
- **Logging and Monitoring**: Track and audit LLM requests

---

## Architecture Overview

```
User Input → Agent Processing → Before-Model Callback → LLM Model → Model Response → Agent Processing → Final Output
                ↓                      ↓                    ↓            ↓
            Request Preparation    Request Inspection/    Modified    Model Output
                                  Modification/Blocking   Request
```

The callback acts as a **gatekeeper** between the agent's request preparation and the LLM model, allowing you to:
1. **Inspect** the complete LLM request
2. **Modify** the request content and configuration
3. **Block** the request and provide alternative responses
4. **Log** request attempts for monitoring

---

## Key Components Breakdown

### 1. **Constants and Configuration**

#### **Application Constants**
```java
private static final String MODEL_ID = "gemini-2.0-flash";
private static final String APP_NAME = "guardrail_app";
private static final String USER_ID = "user_1";
```

**What they are:**
- **MODEL_ID**: LLM model identifier for the agent
- **APP_NAME**: Application identifier for the agent
- **USER_ID**: User identifier for session management

**Why they're used:**
- **Model Selection**: Specifies which LLM model to use
- **Session Management**: Enables proper state tracking and isolation
- **User Isolation**: Ensures each user has their own conversation context
- **Identification**: Clear naming for debugging and logging

---

### 2. **Agent Configuration**

#### **LlmAgent Setup with Callback**
```java
LlmAgent myLlmAgent = LlmAgent.builder()
    .name("ModelCallbackAgent")
    .model(MODEL_ID)
    .instruction("You are a helpful assistant.") // Base instruction
    .description("An LLM agent demonstrating before_model_callback")
    .beforeModelCallbackSync(this::simpleBeforeModelModifier) // Assign the callback here
    .build();
```

**Configuration Analysis:**

| Component | Value | Purpose | Why Used |
|-----------|-------|---------|----------|
| **Name** | `"ModelCallbackAgent"` | Agent identification | Distinguishes this agent in logs and sessions |
| **Description** | `"An LLM agent demonstrating..."` | Agent purpose documentation | Explains the callback functionality |
| **Model** | `"gemini-2.0-flash"` | LLM engine | Fast, reliable model for testing |
| **Instruction** | `"You are a helpful assistant."` | Base agent behavior | Simple, predictable behavior |
| **BeforeModelCallbackSync** | `this::simpleBeforeModelModifier` | **Core feature** | **Enables request interception and modification** |

**Why This Configuration:**
- **Clear Role**: Simple instruction ensures predictable behavior
- **Callback Integration**: `beforeModelCallbackSync()` is the key method that enables the feature
- **Sync Callback**: Uses synchronous callback for simpler implementation
- **Simple Model**: Flash model is sufficient for testing callback functionality
- **Documentation**: Clear descriptions explain the functionality

---

### 3. **Callback Function Implementation**

#### **simpleBeforeModelModifier Method**
```java
public Optional<LlmResponse> simpleBeforeModelModifier(
        CallbackContext callbackContext, LlmRequest llmRequest) {
    // Request inspection and modification logic
}
```

**Function Signature Analysis:**

| Parameter | Type | Purpose | Why Required |
|-----------|------|---------|--------------|
| **callbackContext** | `CallbackContext` | Provides agent and session info | Context for logging and decision-making |
| **llmRequest** | `LlmRequest` | Complete LLM request to inspect/modify | The actual request to examine and potentially modify |
| **Return Type** | `Optional<LlmResponse>` | Optional response to skip LLM call | Can return response to skip LLM or empty to proceed |

**Why This Signature:**
- **Complete Context**: Access to all agent and session information
- **Request Access**: Full access to the LLM request including content and configuration
- **Response Control**: Can provide a response to skip the LLM call entirely
- **Flexible Control**: Can modify the request or block it completely

---

### 4. **Request Inspection Phase**

#### **User Message Extraction**
```java
String lastUserMessageText = "";
List<Content> requestContents = llmRequest.contents();
if (requestContents != null && !requestContents.isEmpty()) {
    Content lastContent = requestContents.get(requestContents.size() - 1);
    if (lastContent.role().isPresent() && "user".equals(lastContent.role().get())) {
        lastUserMessageText = lastContent.parts().orElse(List.of()).stream()
            .flatMap(part -> part.text().stream())
            .collect(Collectors.joining(" ")); // Concatenate text from all parts
    }
}
System.out.println("[Callback] Inspecting last user message: '" + lastUserMessageText + "'");
```

**What it does:**
- **Content Access**: Gets all content from the LLM request
- **Last Message**: Extracts the most recent user message
- **Role Validation**: Ensures the last message is from the user
- **Text Concatenation**: Combines text from all parts of the message
- **Logging**: Provides visibility into the user's input

**Why this approach:**
- **User Input Focus**: Examines what the user actually sent
- **Content Safety**: Can detect inappropriate user content
- **Complete Text**: Handles multi-part messages correctly
- **Debugging**: Provides visibility into request processing

---

### 5. **System Instruction Modification**

#### **Dynamic System Instruction Enhancement**
```java
String prefix = "[Modified by Callback] ";
GenerateContentConfig currentConfig = llmRequest.config().orElse(GenerateContentConfig.builder().build());
Optional<Content> optOriginalSystemInstruction = currentConfig.systemInstruction();

Content conceptualModifiedSystemInstruction;
if (optOriginalSystemInstruction.isPresent()) {
    Content originalSystemInstruction = optOriginalSystemInstruction.get();
    List<Part> originalParts = new ArrayList<>(originalSystemInstruction.parts().orElse(List.of()));
    String originalText = "";

    if (!originalParts.isEmpty()) {
        Part firstPart = originalParts.get(0);
        if (firstPart.text().isPresent()) {
            originalText = firstPart.text().get();
        }
        originalParts.set(0, Part.fromText(prefix + originalText));
    } else {
        originalParts.add(Part.fromText(prefix));
    }
    conceptualModifiedSystemInstruction = originalSystemInstruction.toBuilder().parts(originalParts).build();
} else {
    conceptualModifiedSystemInstruction = Content.builder()
        .role("system")
        .parts(List.of(Part.fromText(prefix)))
        .build();
}
```

**What it does:**
- **Configuration Access**: Gets the current LLM request configuration
- **System Instruction Access**: Retrieves existing system instructions
- **Instruction Enhancement**: Adds a prefix to existing system instructions
- **Fallback Creation**: Creates system instruction if none exists
- **Content Preservation**: Maintains original instruction content while adding enhancement

**Why this approach:**
- **Dynamic Enhancement**: Can modify system instructions based on context
- **Backward Compatibility**: Handles cases where no system instruction exists
- **Content Preservation**: Maintains original instructions while adding modifications
- **Flexible Prefixing**: Can add any kind of enhancement to system instructions

#### **Request Modification**
```java
llmRequest = llmRequest.toBuilder()
    .config(currentConfig.toBuilder()
        .systemInstruction(conceptualModifiedSystemInstruction)
        .build())
    .build();

System.out.println("[Callback] Conceptually modified system instruction is: '" +
    llmRequest.config().get().systemInstruction().get().parts().get().get(0).text().get());
```

**What it does:**
- **Request Reconstruction**: Creates a new LLM request with modified configuration
- **System Instruction Update**: Applies the enhanced system instruction
- **Logging**: Confirms the modification was applied
- **Immutable Pattern**: Creates new objects rather than modifying existing ones

**Why this approach:**
- **Immutability**: Follows ADK's immutable object pattern
- **Configuration Control**: Full control over LLM request configuration
- **Verification**: Logs confirm the modification was applied
- **Type Safety**: Ensures proper request construction

---

### 6. **Guardrail Implementation**

#### **Content Blocking Logic**
```java
// Check if the last user message contains "BLOCK"
if (lastUserMessageText.toUpperCase().contains("BLOCK")) {
    System.out.println("[Callback] 'BLOCK' keyword found. Skipping LLM call.");
    LlmResponse skipResponse = LlmResponse.builder()
        .content(Content.builder()
            .role("model")
            .parts(List.of(Part.builder()
                .text("LLM call was blocked by before_model_callback.")
                .build()))
            .build())
        .build();
    return Optional.of(skipResponse);
}
```

**What it does:**
- **Content Detection**: Searches for the "BLOCK" keyword in user input
- **Case Insensitive**: Uses uppercase comparison for robust detection
- **Response Creation**: Creates a custom response to replace the blocked LLM call
- **Blocking Execution**: Returns the response to skip LLM execution entirely

**Why this approach:**
- **Content Safety**: Demonstrates how to block inappropriate content
- **Custom Responses**: Provides meaningful feedback when blocking occurs
- **Complete Blocking**: Prevents LLM from seeing blocked content
- **User Feedback**: Informs users when their request was blocked

#### **Request Proceeding**
```java
System.out.println("[Callback] Proceeding with LLM call.");
// Return Optional.empty() to allow the (modified) request to go to the LLM
return Optional.empty();
```

**What it does:**
- **Normal Flow**: Allows the LLM request to proceed
- **Modified Request**: The request includes any modifications made by the callback
- **Empty Return**: Indicates no blocking should occur

**Why this approach:**
- **Conditional Logic**: Only blocks when specific conditions are met
- **Modified Request**: LLM receives the enhanced request
- **Normal Processing**: Maintains standard flow for acceptable content

---

## Workflow Examples

### **Example 1: Normal Request Processing**

#### **Input Processing**
```java
String prompt = "Tell me about quantum computing. This is a test.";
```

#### **Callback Processing**
```
[Callback] Before model call for agent: ModelCallbackAgent
[Callback] Inspecting last user message: 'Tell me about quantum computing. This is a test.'
[Callback] Conceptually modified system instruction is: '[Modified by Callback] You are a helpful assistant.'
[Callback] Proceeding with LLM call.
```

#### **LLM Processing**
```
LLM receives request with enhanced system instruction: "[Modified by Callback] You are a helpful assistant."
LLM processes the request about quantum computing
LLM returns response about quantum computing
```

#### **Final Output**
```
Response about quantum computing with enhanced system instruction context
```

**Key Point**: The system instruction was enhanced with the callback prefix, but the request proceeded normally.

---

### **Example 2: Blocked Request Processing**

#### **Input Processing**
```java
String prompt = "Tell me about quantum computing. BLOCK this request.";
```

#### **Callback Processing**
```
[Callback] Before model call for agent: ModelCallbackAgent
[Callback] Inspecting last user message: 'Tell me about quantum computing. BLOCK this request.'
[Callback] Conceptually modified system instruction is: '[Modified by Callback] You are a helpful assistant.'
[Callback] 'BLOCK' keyword found. Skipping LLM call.
```

#### **Blocked Processing**
```
LLM call is completely skipped
Custom response is returned instead
```

#### **Final Output**
```
LLM call was blocked by before_model_callback.
```

**Key Point**: The LLM never received the request due to the "BLOCK" keyword detection.

---

## Callback Decision Matrix

| Scenario | User Input Contains | Callback Action | Result |
|----------|-------------------|-----------------|---------|
| **Normal Content** | No "BLOCK" keyword | Modify system instruction, proceed | LLM processes enhanced request |
| **Blocked Content** | "BLOCK" keyword | Block LLM call | Custom blocking response |
| **Empty Content** | Empty or null | Modify system instruction, proceed | LLM processes enhanced request |
| **Special Characters** | Non-text content | Modify system instruction, proceed | LLM processes enhanced request |

---

## Advanced Use Cases

### **1. Content Safety and Filtering**
```java
if (containsInappropriateContent(lastUserMessageText)) {
    return Optional.of(createBlockedResponse("Content violates safety guidelines."));
}
```

### **2. Dynamic System Instructions**
```java
// Add context-specific system instructions
if (isTechnicalQuery(lastUserMessageText)) {
    String technicalInstruction = "You are a technical expert. Provide detailed technical explanations.";
    // Modify system instruction accordingly
}
```

### **3. Request Logging and Analytics**
```java
// Log all requests for analysis
logRequestDetails(agentName, lastUserMessageText, requestContents.size());
// Continue with normal processing
return Optional.empty();
```

### **4. Rate Limiting and Usage Control**
```java
if (isRateLimited(callbackContext, lastUserMessageText)) {
    return Optional.of(createRateLimitResponse());
}
```

### **5. Request Enhancement and Context Addition**
```java
// Add user-specific context to system instruction
String userContext = getUserContext(callbackContext);
if (userContext != null) {
    // Enhance system instruction with user context
}
```

### **6. Multi-Language Content Detection**
```java
if (detectLanguage(lastUserMessageText) != "en") {
    // Add translation or language-specific instructions
    String languageInstruction = "Respond in the same language as the user's input.";
    // Modify system instruction
}
```

---

## Configuration Summary Table

| Component | Value | Purpose | Impact |
|-----------|-------|---------|--------|
| **Name** | `"ModelCallbackAgent"` | Agent identification | Used in logs and session management |
| **Description** | `"An LLM agent demonstrating..."` | Documentation | Explains callback functionality |
| **Model** | `"gemini-2.0-flash"` | LLM engine | Fast, reliable model for testing |
| **Instruction** | `"You are a helpful assistant."` | Base agent behavior | Simple, predictable behavior |
| **BeforeModelCallbackSync** | `this::simpleBeforeModelModifier` | **Core feature** | **Enables request interception** |
| **System Instruction Prefix** | `"[Modified by Callback] "` | Enhancement marker | Demonstrates dynamic instruction modification |
| **Blocking Keyword** | `"BLOCK"` | Guardrail trigger | Keyword that triggers request blocking |
| **Blocking Response** | `"LLM call was blocked by before_model_callback."` | Custom response | Message shown when request is blocked |

---

## Key Takeaways

### **1. Callback Timing**
- Executes **after** agent prepares the LLM request
- Executes **before** the request is sent to the LLM model
- Perfect for request inspection and modification

### **2. Request Control**
- **Inspection**: Full access to request content and configuration
- **Modification**: Can modify system instructions, content, and configuration
- **Blocking**: Can prevent LLM calls entirely with custom responses

### **3. System Instruction Enhancement**
- Can dynamically modify system instructions
- Can add context-specific instructions
- Can enhance existing instructions with prefixes or additions

### **4. Guardrail Implementation**
- **Content Filtering**: Block requests containing restricted content
- **Safety Controls**: Implement safety policies and restrictions
- **Custom Responses**: Provide meaningful feedback when blocking occurs

### **5. Use Cases**
- Content safety and filtering
- Dynamic system instruction modification
- Request logging and analytics
- Rate limiting and usage control
- Request enhancement and context addition
- Multi-language content handling

---

## Production Considerations

### **Performance Impact**
- Callbacks add minimal overhead to LLM requests
- Should be lightweight and fast
- Avoid heavy processing in callbacks

### **Error Handling**
- Always handle unexpected request formats
- Provide fallback behaviors for edge cases
- Log errors for debugging and monitoring

### **Security**
- Validate all content before processing
- Implement proper content filtering
- Ensure blocking logic is robust and secure

### **Monitoring**
- Log callback executions for debugging
- Monitor blocking rates and patterns
- Track request modification frequency

### **Testing**
- Test with various request types and content
- Verify blocking logic with different inputs
- Ensure proper handling of edge cases

This callback system provides powerful capabilities for intercepting and controlling LLM requests, enabling sophisticated content management, safety controls, and request enhancement in AI agent applications. The callback system enables pre-processing of LLM requests while maintaining the integrity of the agent-model interaction flow.
