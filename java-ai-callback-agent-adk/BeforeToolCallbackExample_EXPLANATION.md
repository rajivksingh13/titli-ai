# BeforeToolCallbackExample - Comprehensive Code Explanation

## Overview
This document provides a detailed explanation of the `BeforeToolCallbackExample` class, which demonstrates the **before_tool_callback** functionality in Google's Agent Development Kit (ADK). This advanced feature allows you to inspect and modify tool invocation parameters before a tool is executed, enabling sophisticated tool behavior control and parameter manipulation.

---

## Key Concepts

### **What is a Before-Tool Callback?**
A **before_tool_callback** is a function that executes **before** a tool is called but **after** the agent has decided to invoke the tool. This allows for:

- **Parameter Inspection**: Examine the arguments passed to the tool
- **Parameter Modification**: Alter tool arguments before execution
- **Tool Blocking**: Prevent tool execution and provide alternative results
- **Security Controls**: Implement access controls and validation
- **Parameter Enhancement**: Add or modify parameters based on context
- **Logging and Monitoring**: Track tool invocation attempts

---

## Architecture Overview

```
User Query → Agent Decision → Before-Tool Callback → Tool Execution → Tool Result → Agent Processing → Final Output
                ↓                    ↓                    ↓               ↓
            Tool Selection    Parameter Inspection/     Actual Tool     Tool Output
                             Modification/Blocking      Execution
```

The callback acts as a **gatekeeper** between the agent's tool selection and actual tool execution, allowing you to:
1. **Inspect** the tool arguments
2. **Modify** the arguments before execution
3. **Block** tool execution and provide alternative results
4. **Log** tool invocation attempts

---

## Key Components Breakdown

### 1. **Constants and Configuration**

#### **Application Constants**
```java
private static final String APP_NAME = "ToolCallbackAgentApp";
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
@Schema(name = "country", description = "The country to find the capital of.")
public static Map<String, Object> getCapitalCity(String country) {
    System.out.printf("--- Tool 'getCapitalCity' executing with country: %s ---%n", country);
    Map<String, String> countryCapitals = new HashMap<>();
    countryCapitals.put("united states", "Washington, D.C.");
    countryCapitals.put("canada", "Ottawa");
    countryCapitals.put("france", "Paris");
    countryCapitals.put("germany", "Berlin");

    String capital = countryCapitals.getOrDefault(country.toLowerCase(), "Capital not found for " + country);
    return ImmutableMap.of("capital", capital);
}
```

**Tool Function Analysis:**

| Component | Purpose | Why This Design |
|-----------|---------|-----------------|
| **@Schema Annotation** | Parameter metadata | Provides parameter name and description for ADK |
| **Parameter Schema** | `name = "country"` | Enables callback to identify parameters by name |
| **Return Type** | `Map<String, Object>` | Standard ADK tool return format |
| **Data Structure** | `{"capital": capital}` | Consistent response format |
| **Error Handling** | Default case for unknown countries | Graceful handling of edge cases |
| **Logging** | Tool execution logging | Visibility into tool execution |

**Why This Tool Design:**
- **Simple Function**: Easy to understand and test
- **Predictable Output**: Consistent return format for callback testing
- **Parameter Naming**: Uses `@Schema(name = "country")` for callback parameter identification
- **Error Handling**: Demonstrates graceful failure cases
- **Logging**: Provides visibility into tool execution

---

### 3. **Agent Configuration**

#### **LlmAgent Setup with Tool and Callback**
```java
LlmAgent myLlmAgent = LlmAgent.builder()
    .name(APP_NAME)
    .model(MODEL_NAME)
    .instruction("You are an agent that can find capital cities. Use the getCapitalCity tool.")
    .description("An LLM agent demonstrating before_tool_callback")
    .tools(capitalTool)
    .beforeToolCallback(this::simpleBeforeToolModifier)
    .build();
```

**Configuration Analysis:**

| Component | Value | Purpose | Why Used |
|-----------|-------|---------|----------|
| **Name** | `"ToolCallbackAgentApp"` | Agent identification | Distinguishes this agent in logs and sessions |
| **Description** | `"An LLM agent demonstrating..."` | Agent purpose documentation | Explains the callback functionality |
| **Model** | `"gemini-2.0-flash"` | LLM engine | Fast, reliable model for tool calling |
| **Instruction** | `"You are an agent that can find capital cities..."` | Agent behavior | Clear role and tool usage instructions |
| **Tools** | `capitalTool` | **Tool integration** | **Enables tool calling capabilities** |
| **BeforeToolCallback** | `this::simpleBeforeToolModifier` | **Core feature** | **Enables parameter inspection and modification** |

**Why This Configuration:**
- **Clear Role**: Instruction specifies the agent's purpose and tool usage
- **Tool Integration**: Demonstrates how to add tools to agents
- **Callback Integration**: `beforeToolCallback()` is the key method that enables the feature
- **Simple Model**: Flash model is sufficient for tool-calling tasks
- **Documentation**: Clear descriptions explain the functionality

---

### 4. **Callback Function Implementation**

#### **simpleBeforeToolModifier Method**
```java
public Maybe<Map<String, Object>> simpleBeforeToolModifier(
        InvocationContext invocationContext,
        BaseTool tool,
        Map<String, Object> args,
        ToolContext toolContext) {
    // Parameter inspection and modification logic
}
```

**Function Signature Analysis:**

| Parameter | Type | Purpose | Why Required |
|-----------|------|---------|--------------|
| **invocationContext** | `InvocationContext` | Provides agent and session info | Context for logging and decision-making |
| **tool** | `BaseTool` | The tool about to be executed | Identifies which tool is being called |
| **args** | `Map<String, Object>` | Arguments passed to the tool | The parameters to inspect/modify |
| **toolContext** | `ToolContext` | Tool execution context | Additional tool-specific information |
| **Return Type** | `Maybe<Map<String, Object>>` | Optional result to skip tool | Can return result to skip execution or empty to proceed |

**Why This Signature:**
- **Complete Context**: Access to all tool invocation information
- **Tool Identification**: Knows which tool is about to be executed
- **Parameter Access**: Can see and modify what arguments will be passed
- **Execution Control**: Can block tool execution by returning a result
- **Flexible Control**: Can modify parameters or block execution entirely

---

### 5. **Callback Inspection Phase**

#### **Context Information Extraction**
```java
String agentName = invocationContext.agent().name();
String toolName = tool.name();
System.out.printf("[Callback] Before tool call for tool '%s' in agent '%s'%n", toolName, agentName);
System.out.printf("[Callback] Original args: %s%n", args);
```

**What it does:**
- **Agent Identification**: Gets the name of the agent calling the tool
- **Tool Identification**: Gets the name of the tool about to be executed
- **Parameter Logging**: Logs the arguments that will be passed to the tool
- **Context Logging**: Provides visibility into tool invocation

**Why this is important:**
- **Debugging**: Provides visibility into tool invocation attempts
- **Context Awareness**: Enables tool-specific and parameter-specific behavior
- **Logging**: Helps track tool usage patterns
- **Decision Making**: Information needed for modification logic

---

### 6. **Callback Modification and Control Phase**

#### **Parameter Modification Logic**
```java
if ("getCapitalCity".equals(toolName)) {
    String countryArg = (String) args.get("country");
    if (countryArg != null) {
        if ("canada".equalsIgnoreCase(countryArg)) {
            System.out.println("[Callback] Detected 'Canada'. Modifying args to 'France'.");
            args.put("country", "France");
            System.out.printf("[Callback] Modified args: %s%n", args);
            return Maybe.empty(); // Proceed with modified args
        }
    }
}
```

**What it does:**
- **Tool-Specific Logic**: Only applies to `getCapitalCity` tool
- **Parameter Extraction**: Gets the "country" parameter from args
- **Conditional Modification**: Changes "canada" to "France"
- **Direct Modification**: Modifies the args map directly
- **Proceed**: Returns `Maybe.empty()` to continue with modified args

**Why this approach:**
- **Selective Control**: Only modifies specific tool calls
- **Parameter Awareness**: Can modify based on specific parameter values
- **Direct Modification**: Changes the actual arguments passed to the tool
- **Flexible Logic**: Can implement complex parameter modification rules

#### **Tool Execution Blocking**
```java
else if ("BLOCK".equalsIgnoreCase(countryArg)) {
    System.out.println("[Callback] Detected 'BLOCK'. Skipping tool execution.");
    return Maybe.just(
        ImmutableMap.of("result", "Tool execution was blocked by before_tool_callback."));
}
```

**What it does:**
- **Blocking Detection**: Identifies when to block tool execution
- **Alternative Result**: Returns a result instead of executing the tool
- **Execution Prevention**: Tool will not be called at all
- **Custom Response**: Provides a custom response to the agent

**Why this is powerful:**
- **Security Control**: Can block unauthorized or dangerous tool calls
- **Access Control**: Can implement permission-based tool access
- **Conditional Execution**: Can skip tool execution based on context
- **Custom Responses**: Can provide alternative results without tool execution

---

## Workflow Examples

### **Example 1: Parameter Modification (Canada → France)**

#### **Input Processing**
```java
String query = "capital of canada";
```

#### **Tool Invocation Flow**
```
Agent decides to call getCapitalCity tool
Agent prepares args: {"country": "canada"}

[Callback] Before tool call for tool 'getCapitalCity' in agent 'ToolCallbackAgentApp'
[Callback] Original args: {country=canada}
[Callback] Detected 'Canada'. Modifying args to 'France'.
[Callback] Modified args: {country=France}
[Callback] Proceeding with original or previously modified args.

--- Tool 'getCapitalCity' executing with country: France ---
Tool returns: {"capital": "Paris"}
```

#### **Final Output**
```
The capital of Canada is Paris.
```

**Key Point**: The tool was called with "France" instead of "Canada", so it returned Paris instead of Ottawa.

---

### **Example 2: Tool Execution Blocking**

#### **Input Processing**
```java
String query = "capital of BLOCK";
```

#### **Tool Invocation Flow**
```
Agent decides to call getCapitalCity tool
Agent prepares args: {"country": "BLOCK"}

[Callback] Before tool call for tool 'getCapitalCity' in agent 'ToolCallbackAgentApp'
[Callback] Original args: {country=BLOCK}
[Callback] Detected 'BLOCK'. Skipping tool execution.

--- Tool execution was skipped ---
```

#### **Final Output**
```
The capital of BLOCK is: Tool execution was blocked by before_tool_callback.
```

**Key Point**: The tool was never executed, and the callback provided the final result.

---

### **Example 3: Normal Execution (No Modification)**

#### **Input Processing**
```java
String query = "capital of germany";
```

#### **Tool Invocation Flow**
```
Agent decides to call getCapitalCity tool
Agent prepares args: {"country": "germany"}

[Callback] Before tool call for tool 'getCapitalCity' in agent 'ToolCallbackAgentApp'
[Callback] Original args: {country=germany}
[Callback] Proceeding with original or previously modified args.

--- Tool 'getCapitalCity' executing with country: germany ---
Tool returns: {"capital": "Berlin"}
```

#### **Final Output**
```
The capital of Germany is Berlin.
```

**Key Point**: No modification occurred, tool executed normally.

---

## Callback Decision Matrix

| Scenario | Tool | Parameter | Callback Action | Result |
|----------|------|-----------|-----------------|---------|
| **getCapitalCity + "canada"** | `getCapitalCity` | `{"country": "canada"}` | Modify to "France" | Tool executes with "France" |
| **getCapitalCity + "BLOCK"** | `getCapitalCity` | `{"country": "BLOCK"}` | Block execution | Custom result returned |
| **getCapitalCity + "germany"** | `getCapitalCity` | `{"country": "germany"}` | Pass through | Tool executes normally |
| **Different Tool** | `otherTool` | Any args | Pass through | Tool executes normally |
| **Missing Parameter** | `getCapitalCity` | `{}` | Pass through | Tool executes normally |

---

## Advanced Use Cases

### **1. Parameter Validation and Sanitization**
```java
if ("getCapitalCity".equals(toolName)) {
    String countryArg = (String) args.get("country");
    if (countryArg != null) {
        // Sanitize input
        String sanitizedCountry = countryArg.trim().toLowerCase();
        if (sanitizedCountry.isEmpty()) {
            // Block execution for invalid input
            return Maybe.just(ImmutableMap.of("result", "Invalid country name provided."));
        }
        // Use sanitized input
        args.put("country", sanitizedCountry);
    }
}
```

### **2. Access Control and Security**
```java
if ("getCapitalCity".equals(toolName)) {
    String countryArg = (String) args.get("country");
    if (countryArg != null && isRestrictedCountry(countryArg)) {
        // Check user permissions
        if (!hasPermission(invocationContext, "access_restricted_countries")) {
            return Maybe.just(ImmutableMap.of("result", "Access denied for this country."));
        }
    }
}
```

### **3. Parameter Enhancement**
```java
if ("getCapitalCity".equals(toolName)) {
    // Add additional context
    args.put("include_region", true);
    args.put("include_population", false);
    // Modify existing parameter
    String countryArg = (String) args.get("country");
    if (countryArg != null) {
        args.put("country", countryArg.toUpperCase());
    }
}
```

### **4. Rate Limiting and Usage Control**
```java
if (isRateLimited(invocationContext, toolName)) {
    return Maybe.just(ImmutableMap.of("result", "Rate limit exceeded. Please try again later."));
}
```

### **5. Parameter Transformation**
```java
if ("getCapitalCity".equals(toolName)) {
    String countryArg = (String) args.get("country");
    if (countryArg != null) {
        // Transform country names
        String transformedCountry = transformCountryName(countryArg);
        args.put("country", transformedCountry);
    }
}
```

---

## Configuration Summary Table

| Component | Value | Purpose | Impact |
|-----------|-------|---------|--------|
| **Name** | `"ToolCallbackAgentApp"` | Agent identification | Used in logs and session management |
| **Description** | `"An LLM agent demonstrating..."` | Documentation | Explains callback functionality |
| **Model** | `"gemini-2.0-flash"` | LLM engine | Fast, reliable model for tool calling |
| **Instruction** | `"You are an agent that can find capital cities..."` | Agent behavior | Clear role and tool usage |
| **Tools** | `capitalTool` | **Tool integration** | **Enables tool calling capabilities** |
| **BeforeToolCallback** | `this::simpleBeforeToolModifier` | **Core feature** | **Enables parameter interception** |
| **Tool Function** | `getCapitalCity` | Tool implementation | Provides capital city lookup |
| **Parameter Schema** | `@Schema(name = "country")` | Parameter identification | Enables callback parameter access |
| **Modification Logic** | Canada → France, BLOCK → Skip | Callback behavior | Demonstrates parameter modification and blocking |

---

## Key Takeaways

### **1. Callback Timing**
- Executes **after** agent decides to call a tool
- Executes **before** the tool is actually executed
- Perfect for parameter inspection and modification

### **2. Parameter Control**
- **Modification**: Can change tool arguments before execution
- **Blocking**: Can prevent tool execution entirely
- **Validation**: Can validate and sanitize parameters
- **Enhancement**: Can add additional parameters

### **3. Execution Control**
- `Maybe.just(result)` blocks tool execution and returns custom result
- `Maybe.empty()` allows tool execution to proceed
- Full control over whether tools execute or not

### **4. Context Access**
- Access to agent name, tool name, and all parameters
- Access to invocation context and session information
- Enables sophisticated decision-making logic

### **5. Use Cases**
- Parameter validation and sanitization
- Access control and security
- Parameter enhancement and transformation
- Rate limiting and usage control
- Tool execution blocking and redirection

---

## Production Considerations

### **Performance Impact**
- Callbacks add minimal overhead to tool invocation
- Should be lightweight and fast
- Avoid heavy processing in callbacks

### **Error Handling**
- Always handle unexpected parameter types
- Provide fallback behaviors for edge cases
- Log errors for debugging and monitoring

### **Security**
- Validate all parameters before modification
- Implement proper access controls
- Sanitize inputs to prevent injection attacks

### **Monitoring**
- Log callback executions for debugging
- Monitor parameter modification rates
- Track tool blocking incidents

### **Testing**
- Test with various parameter combinations
- Verify modification logic with different inputs
- Ensure proper handling of edge cases

This callback system provides powerful capabilities for intercepting and controlling tool invocations, enabling sophisticated parameter management, security controls, and execution control in AI agent applications. The callback system enables pre-processing of tool calls while maintaining the integrity of the agent-tool interaction flow.
