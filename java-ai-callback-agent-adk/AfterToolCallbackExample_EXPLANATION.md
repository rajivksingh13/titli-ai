# AfterToolCallbackExample - Comprehensive Code Explanation

## Overview
This document provides a detailed explanation of the `AfterToolCallbackExample` class, which demonstrates the **after_tool_callback** functionality in Google's Agent Development Kit (ADK). This advanced feature allows you to inspect and modify tool execution results after a tool has completed its execution but before the result is processed by the agent framework.

---

## Key Concepts

### **What is an After-Tool Callback?**
An **after_tool_callback** is a function that executes **after** a tool has completed its execution but **before** the tool result is processed by the agent framework. This allows for:

- **Result Inspection**: Examine the raw tool execution output
- **Result Modification**: Alter the tool's result before agent processing
- **Error Handling**: Handle tool execution errors gracefully
- **Data Enhancement**: Add additional information to tool results
- **Quality Control**: Validate and improve tool outputs
- **Logging and Monitoring**: Track tool execution and results

---

## Architecture Overview

```
User Query → Agent → Tool Execution → After-Tool Callback → Agent Processing → Final Output
                ↓           ↓               ↓                    ↓
            Tool Call   Tool Result    Modified Result    Processed Response
```

The callback acts as an **interceptor** between tool execution and agent processing, allowing you to:
1. **Inspect** the raw tool result
2. **Modify** the result if needed
3. **Pass through** the result unchanged
4. **Handle errors** from tool execution

---

## Key Components Breakdown

### 1. **Constants and Configuration**

#### **Application Constants**
```java
private static final String APP_NAME = "AfterToolCallbackAgentApp";
private static final String USER_ID = "user_1";
private static final String SESSION_ID = "session_001";
private static final String MODEL_NAME = "gemini-2.0-flash";
```

**What they are:**
- **APP_NAME**: Application identifier for the agent
- **USER_ID**: User identifier for session management
- **SESSION_ID**: Specific session identifier for state management
- **MODEL_NAME**: LLM model used by the agent

**Why they're used:**
- **Session Management**: Enables proper state tracking and isolation
- **User Isolation**: Ensures each user has their own conversation context
- **Consistency**: Standardized model across all operations
- **Identification**: Clear naming for debugging and logging

---

### 2. **Tool Function Definition**

#### **getCapitalCity Tool Function**
```java
@Schema(description = "Retrieves the capital city of a given country.")
public static Map<String, Object> getCapitalCity(
        @Schema(description = "The country to find the capital of.") String country) {
    System.out.printf("--- Tool 'getCapitalCity' executing with country: %s ---%n", country);
    Map<String, String> countryCapitals = new HashMap<>();
    countryCapitals.put("united states", "Washington, D.C.");
    countryCapitals.put("canada", "Ottawa");
    countryCapitals.put("france", "Paris");
    countryCapitals.put("germany", "Berlin");

    String capital = countryCapitals.getOrDefault(country.toLowerCase(), "Capital not found for " + country);
    return ImmutableMap.of("result", capital);
}
```

**Tool Function Analysis:**

| Component | Purpose | Why This Design |
|-----------|---------|-----------------|
| **@Schema Annotation** | Tool metadata | Provides description for ADK tool registration |
| **Parameter Schema** | Input documentation | Describes the expected input parameter |
| **Return Type** | `Map<String, Object>` | Standard ADK tool return format |
| **Data Structure** | `{"result": capital}` | Consistent response format |
| **Error Handling** | Default case for unknown countries | Graceful handling of edge cases |
| **Logging** | Tool execution logging | Visibility into tool execution |

**Why This Tool Design:**
- **Simple Function**: Easy to understand and test
- **Predictable Output**: Consistent return format for callback testing
- **Error Handling**: Demonstrates graceful failure cases
- **Logging**: Provides visibility into tool execution
- **Standard Format**: Follows ADK tool conventions

---

### 3. **Agent Configuration**

#### **LlmAgent Setup with Tool and Callback**
```java
LlmAgent myLlmAgent = LlmAgent.builder()
    .name(APP_NAME)
    .model(MODEL_NAME)
    .instruction("You are an agent that finds capital cities using the getCapitalCity tool. Report the result clearly.")
    .description("An LLM agent demonstrating after_tool_callback")
    .tools(capitalTool) // Add the tool
    .afterToolCallback(this::simpleAfterToolModifier) // Assign the callback
    .build();
```

**Configuration Analysis:**

| Component | Value | Purpose | Why Used |
|-----------|-------|---------|----------|
| **Name** | `"AfterToolCallbackAgentApp"` | Agent identification | Distinguishes this agent in logs and sessions |
| **Description** | `"An LLM agent demonstrating..."` | Agent purpose documentation | Explains the callback functionality |
| **Model** | `"gemini-2.0-flash"` | LLM engine | Fast, reliable model for testing |
| **Instruction** | `"You are an agent that finds capital cities..."` | Agent behavior | Clear role and tool usage instructions |
| **Tools** | `capitalTool` | **Tool integration** | **Enables tool calling capabilities** |
| **AfterToolCallback** | `this::simpleAfterToolModifier` | **Core feature** | **Enables tool result interception and modification** |

**Why This Configuration:**
- **Clear Role**: Instruction specifies the agent's purpose and tool usage
- **Tool Integration**: Demonstrates how to add tools to agents
- **Callback Integration**: `afterToolCallback()` is the key method that enables the feature
- **Simple Model**: Flash model is sufficient for tool-calling tasks
- **Documentation**: Clear descriptions explain the functionality

---

### 4. **Callback Function Implementation**

#### **simpleAfterToolModifier Method**
```java
public Maybe<Map<String, Object>> simpleAfterToolModifier(
        InvocationContext invocationContext,
        BaseTool tool,
        Map<String, Object> args,
        ToolContext toolContext,
        Object toolResponse) {
    // Inspection and modification logic
}
```

**Function Signature Analysis:**

| Parameter | Type | Purpose | Why Required |
|-----------|------|---------|--------------|
| **invocationContext** | `InvocationContext` | Provides agent and session info | Context for logging and decision-making |
| **tool** | `BaseTool` | The tool that was executed | Identifies which tool was called |
| **args** | `Map<String, Object>` | Arguments passed to the tool | Shows what parameters were used |
| **toolContext** | `ToolContext` | Tool execution context | Additional tool-specific information |
| **toolResponse** | `Object` | Raw tool execution result | The actual result to inspect/modify |
| **Return Type** | `Maybe<Map<String, Object>>` | Optional modified result | Can return new result or empty (pass-through) |

**Why This Signature:**
- **Complete Context**: Access to all tool execution information
- **Tool Identification**: Knows which tool was executed
- **Parameter Access**: Can see what arguments were passed
- **Result Access**: Provides the actual tool output to modify
- **Flexible Return**: Can modify or pass through the result

---

### 5. **Callback Inspection Phase**

#### **Context Information Extraction**
```java
String agentName = invocationContext.agent().name();
String toolName = tool.name();
System.out.printf("[Callback] After tool call for tool '%s' in agent '%s'%n", toolName, agentName);
System.out.printf("[Callback] Args used: %s%n", args);
System.out.printf("[Callback] Original tool_response: %s%n", toolResponse);
```

**What it does:**
- **Agent Identification**: Gets the name of the agent that called the tool
- **Tool Identification**: Gets the name of the executed tool
- **Parameter Logging**: Logs the arguments passed to the tool
- **Result Logging**: Logs the original tool response

**Why this is important:**
- **Debugging**: Provides visibility into tool execution
- **Context Awareness**: Enables tool-specific behavior
- **Logging**: Helps track tool usage patterns
- **Decision Making**: Information needed for modification logic

#### **Type Safety Checks**
```java
if (!(toolResponse instanceof Map)) {
    System.out.println("[Callback] toolResponse is not a Map, cannot process further.");
    return Maybe.empty(); // Pass through if not a map
}
```

**What it does:**
- **Type Validation**: Ensures the tool response is a Map
- **Safety Check**: Prevents errors from unexpected response types
- **Pass-Through**: Returns empty if response type is not supported

**Why this is needed:**
- **Type Safety**: Prevents runtime errors from unexpected types
- **Robustness**: Handles edge cases gracefully
- **Flexibility**: Allows tools to return different types

#### **Response Structure Access**
```java
@SuppressWarnings("unchecked")
Map<String, Object> responseMap = (Map<String, Object>) toolResponse;
Object originalResultValue = responseMap.get("result");
```

**What it does:**
- **Type Casting**: Safely casts the response to Map
- **Structure Access**: Extracts the "result" field from the response
- **Value Extraction**: Gets the actual tool result value

**Why this approach:**
- **Standard Format**: Assumes tool returns `{"result": value}` format
- **Safe Casting**: Uses unchecked cast with type validation
- **Value Access**: Gets the core result for modification

---

### 6. **Callback Modification Phase**

#### **Conditional Modification Logic**
```java
if ("getCapitalCity".equals(toolName) && "Washington, D.C.".equals(originalResultValue)) {
    System.out.println("[Callback] Detected 'Washington, D.C.'. Modifying tool response.");
    
    // Create a new mutable map or modify a copy
    Map<String, Object> modifiedResponse = new HashMap<>(responseMap);
    modifiedResponse.put("result", originalResultValue + " (Note: This is the capital of the USA).");
    modifiedResponse.put("note_added_by_callback", true); // Add extra info if needed
    
    System.out.printf("[Callback] Modified tool_response: %s%n", modifiedResponse);
    return Maybe.just(modifiedResponse);
}
```

**What it does:**
- **Conditional Check**: Only modifies specific tool results
- **Tool-Specific Logic**: Only applies to `getCapitalCity` tool
- **Value-Specific Logic**: Only modifies "Washington, D.C." results
- **Response Enhancement**: Adds additional information to the result
- **Metadata Addition**: Adds callback tracking information

**Why this approach:**
- **Selective Modification**: Only modifies when conditions are met
- **Tool Awareness**: Different tools can have different modification logic
- **Value Awareness**: Can modify based on specific result values
- **Enhancement**: Adds useful information to the result
- **Tracking**: Provides metadata about callback modifications

#### **Response Reconstruction**
```java
Map<String, Object> modifiedResponse = new HashMap<>(responseMap);
modifiedResponse.put("result", originalResultValue + " (Note: This is the capital of the USA).");
modifiedResponse.put("note_added_by_callback", true);
```

**What it does:**
- **Response Cloning**: Creates a mutable copy of the original response
- **Result Enhancement**: Modifies the "result" field with additional information
- **Metadata Addition**: Adds callback tracking information
- **Structure Preservation**: Maintains the original response structure

**Why this approach:**
- **Immutability**: Creates new objects rather than modifying existing ones
- **Structure Preservation**: Maintains the expected response format
- **Enhancement**: Adds value without breaking existing functionality
- **Tracking**: Provides visibility into callback modifications

---

## Workflow Example

### **Input Processing**
```java
String query = "What is the capital of the United States?";
```

### **Tool Execution Flow**

#### **1. Agent Processing**
```
Agent receives query: "What is the capital of the United States?"
Agent decides to use getCapitalCity tool
Agent calls tool with argument: "United States"
```

#### **2. Tool Execution**
```
--- Tool 'getCapitalCity' executing with country: United States ---
Tool returns: {"result": "Washington, D.C."}
```

#### **3. Callback Processing**
```
[Callback] After tool call for tool 'getCapitalCity' in agent 'AfterToolCallbackAgentApp'
[Callback] Args used: {country=United States}
[Callback] Original tool_response: {result=Washington, D.C.}
[Callback] Detected 'Washington, D.C.'. Modifying tool response.
[Callback] Modified tool_response: {result=Washington, D.C. (Note: This is the capital of the USA)., note_added_by_callback=true}
```

#### **4. Final Agent Response**
```
Agent processes the modified tool result
Agent responds: "The capital of the United States is Washington, D.C. (Note: This is the capital of the USA)."
```

### **Expected Output Sequence**
```
--- Calling agent with query: "What is the capital of the United States?" ---
--- Tool 'getCapitalCity' executing with country: United States ---
[Callback] After tool call for tool 'getCapitalCity' in agent 'AfterToolCallbackAgentApp'
[Callback] Args used: {country=United States}
[Callback] Original tool_response: {result=Washington, D.C.}
[Callback] Detected 'Washington, D.C.'. Modifying tool response.
[Callback] Modified tool_response: {result=Washington, D.C. (Note: This is the capital of the USA)., note_added_by_callback=true}
The capital of the United States is Washington, D.C. (Note: This is the capital of the USA).
```

---

## Callback Decision Matrix

| Scenario | Tool Response | Callback Action | Result |
|----------|---------------|-----------------|---------|
| **getCapitalCity + Washington, D.C.** | `{"result": "Washington, D.C."}` | Modify with note | `{"result": "Washington, D.C. (Note: This is the capital of the USA).", "note_added_by_callback": true}` |
| **getCapitalCity + Other Capital** | `{"result": "Paris"}` | Pass through | `{"result": "Paris"}` |
| **Different Tool** | `{"result": "any_value"}` | Pass through | `{"result": "any_value"}` |
| **Non-Map Response** | `"string_result"` | Pass through | `"string_result"` |
| **Tool Error** | Error object | Pass through | Error object |

---

## Advanced Use Cases

### **1. Result Validation and Enhancement**
```java
if ("getCapitalCity".equals(toolName)) {
    String capital = (String) responseMap.get("result");
    if (capital != null && !capital.contains("not found")) {
        // Add additional context
        Map<String, Object> enhancedResponse = new HashMap<>(responseMap);
        enhancedResponse.put("result", capital + " - Verified from database");
        enhancedResponse.put("confidence", "high");
        return Maybe.just(enhancedResponse);
    }
}
```

### **2. Error Handling and Recovery**
```java
if (toolResponse instanceof Exception) {
    // Handle tool execution errors
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("result", "Tool execution failed, using fallback data");
    errorResponse.put("error_handled", true);
    return Maybe.just(errorResponse);
}
```

### **3. Data Formatting and Standardization**
```java
if ("getCapitalCity".equals(toolName)) {
    String capital = (String) responseMap.get("result");
    if (capital != null) {
        // Standardize formatting
        String formattedCapital = capital.trim().toLowerCase();
        formattedCapital = formattedCapital.substring(0, 1).toUpperCase() + formattedCapital.substring(1);
        
        Map<String, Object> formattedResponse = new HashMap<>(responseMap);
        formattedResponse.put("result", formattedCapital);
        return Maybe.just(formattedResponse);
    }
}
```

### **4. Analytics and Logging**
```java
// Log tool usage for analytics
logToolUsage(agentName, toolName, args, responseMap);
// Add usage tracking
Map<String, Object> trackedResponse = new HashMap<>(responseMap);
trackedResponse.put("usage_tracked", true);
trackedResponse.put("timestamp", System.currentTimeMillis());
return Maybe.just(trackedResponse);
```

### **5. Security and Compliance**
```java
if (containsSensitiveData(responseMap)) {
    // Sanitize sensitive information
    Map<String, Object> sanitizedResponse = new HashMap<>(responseMap);
    sanitizedResponse.put("result", "[Sensitive data redacted]");
    sanitizedResponse.put("sanitized", true);
    return Maybe.just(sanitizedResponse);
}
```

---

## Configuration Summary Table

| Component | Value | Purpose | Impact |
|-----------|-------|---------|--------|
| **Name** | `"AfterToolCallbackAgentApp"` | Agent identification | Used in logs and session management |
| **Description** | `"An LLM agent demonstrating..."` | Documentation | Explains callback functionality |
| **Model** | `"gemini-2.0-flash"` | LLM engine | Fast, reliable model for tool calling |
| **Instruction** | `"You are an agent that finds capital cities..."` | Agent behavior | Clear role and tool usage |
| **Tools** | `capitalTool` | **Tool integration** | **Enables tool calling capabilities** |
| **AfterToolCallback** | `this::simpleAfterToolModifier` | **Core feature** | **Enables tool result interception** |
| **Tool Function** | `getCapitalCity` | Tool implementation | Provides capital city lookup |
| **Modification Logic** | Washington, D.C. specific | Callback behavior | Adds contextual information |

---

## Key Takeaways

### **1. Callback Timing**
- Executes **after** tool execution completes
- Executes **before** agent processes the tool result
- Perfect for result inspection and modification

### **2. Tool Integration**
- Tools must be properly registered with the agent
- Tool functions should return consistent formats
- Callbacks can access all tool execution context

### **3. Result Modification**
- `Maybe.just(modifiedResponse)` returns modified result
- `Maybe.empty()` preserves original result
- Full control over tool result processing

### **4. Context Access**
- Access to agent name, tool name, and arguments
- Access to tool execution context and results
- Enables sophisticated decision-making logic

### **5. Use Cases**
- Result validation and enhancement
- Error handling and recovery
- Data formatting and standardization
- Analytics and usage tracking
- Security and compliance measures

---

## Production Considerations

### **Performance Impact**
- Callbacks add minimal overhead to tool execution
- Should be lightweight and fast
- Avoid heavy processing in callbacks

### **Error Handling**
- Always handle unexpected response types
- Provide fallback behaviors for edge cases
- Log errors for debugging and monitoring

### **Data Safety**
- Validate modified responses before returning
- Ensure modifications don't break expected formats
- Test edge cases thoroughly

### **Monitoring**
- Log callback executions for debugging
- Monitor callback performance
- Track modification rates and patterns

### **Testing**
- Test with various tool response types
- Verify modification logic with different inputs
- Ensure proper handling of edge cases

This callback system provides powerful capabilities for intercepting and modifying tool execution results, enabling sophisticated result processing, quality enhancement, and monitoring in AI agent applications. The callback system enables post-processing of tool outputs while maintaining the integrity of the agent-tool interaction flow.
