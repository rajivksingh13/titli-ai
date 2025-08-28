# AdkLLMFunctionToolAgent - Capital City Assistant

## Overview
This agent demonstrates how to create an LLM Agent with function tool capabilities using Google's Agent Development Kit (ADK). The agent includes a custom tool that can retrieve capital cities for different countries, showing how to extend an AI assistant with specific functionality.

## Features

### Function Tool Integration
- **Custom tool**: `getCapitalCity` function for retrieving capital cities
- **Tool calling**: Agent can automatically call tools when needed
- **Structured responses**: Tools return structured data in Map format
- **Error handling**: Graceful handling when country data is not found

### Learning Assistant with Tools
- **Educational focus**: Maintains learning assistant capabilities
- **Tool-enhanced responses**: Can provide factual information using tools
- **Interactive chat**: Natural conversation interface
- **Web UI support**: Available in the ADK web interface

## How to Use

### Console Mode
1. Run the application: `./gradlew run`
2. Ask about capital cities or general learning topics:
   ```
   You > What is the capital of France?
   Agent > The capital of France is Paris.
   
   You > Can you tell me about Canada's capital?
   Agent > The capital of Canada is Ottawa.
   
   You > What is machine learning?
   Agent > [Educational explanation about machine learning]
   ```
3. Type `quit` to exit

### Web UI Mode
1. Start the ADK web server: `./gradlew runAdkServer`
2. Open the web interface
3. Select "AdkLLMFunctionToolAgent" from the available agents
4. Interact with the agent through the web interface

## Agent Configuration

### Constants
- `USER_ID`: "test-user"
- `NAME`: "capital_agent"
- `MODEL`: "gemini-2.0-flash"

### Available Countries
The agent can provide capital cities for:
- Canada → Ottawa
- France → Paris
- India → New Delhi
- Sri Lanka → Colombo
- China → Beijing
- USA → Washington, D.C.

## Tool Function Details

### getCapitalCity Function
```java
public static Map<String, Object> getCapitalCity(
    @Annotations.Schema(name = "country", description = "The country to get capital for")
    String country)
```

**Parameters:**
- `country`: The name of the country (case-insensitive)

**Returns:**
- `Map<String, Object>` with key "result" containing the capital city
- Error message if country is not found

**Example Usage:**
```
Input: "france"
Output: {"result": "Paris"}

Input: "unknown_country"
Output: {"result": "Sorry, I couldn't find the capital for unknown_country."}
```

## Example Interactions

### Capital City Queries
```
You > What is the capital of India?
Agent > The capital of India is New Delhi.

You > Tell me about the capital of Canada
Agent > The capital of Canada is Ottawa.

You > What's the capital of Sri Lanka?
Agent > The capital of Sri Lanka is Colombo.
```

### Combined Learning and Tool Usage
```
You > What is the capital of France and can you tell me about French history?
Agent > The capital of France is Paris. As for French history, it's a fascinating subject...
```

### Unknown Countries
```
You > What is the capital of Mars?
Agent > Sorry, I couldn't find the capital for Mars. Mars is a planet, not a country.
```

## Agent Architecture

### Structure with Tool
```
AdkLLMFunctionToolAgent
└── LlmAgent (capital_agent)
    ├── Model: gemini-2.0-flash
    ├── Instructions: Educational assistance
    └── Tools: getCapitalCity function
```

### Tool Integration
- **FunctionTool**: Wraps the `getCapitalCity` method
- **Schema annotations**: Define tool parameters and descriptions
- **Automatic calling**: Agent decides when to use the tool
- **Response integration**: Tool results are incorporated into responses

## Use Cases

### Ideal Scenarios
- **Geography learning**: Learning about countries and capitals
- **Educational assistance**: Combining factual data with explanations
- **Tool demonstration**: Understanding how to extend agents with custom functions
- **Interactive learning**: Engaging way to learn about different countries

### Example Queries
- "What is the capital of [country]?"
- "Tell me about [country] and its capital"
- "Can you compare the capitals of different countries?"
- "What is machine learning?" (general learning)

## Tool Development

### Creating Custom Tools
1. **Define the function**: Create a static method with appropriate parameters
2. **Add annotations**: Use `@Annotations.Schema` to describe parameters
3. **Return Map**: Tools must return `Map<String, Object>`
4. **Register with agent**: Use `FunctionTool.create()` to register the tool

### Example Tool Structure
```java
public static Map<String, Object> myCustomTool(
    @Annotations.Schema(name = "param1", description = "Description")
    String param1) {
    // Tool logic here
    return Map.of("result", "tool result");
}
```

## Output Format

### Tool Responses
- **Structured data**: Returns capital city information
- **Error handling**: Graceful responses for unknown countries
- **Integration**: Seamlessly incorporated into conversational responses

### Example Tool Call
```
-- Tool Call: getCapitalCity(country='france') --
-- Tool Result: 'Paris' --
```

## Notes

- **Hybrid approach**: Combines LLM knowledge with specific tool functionality
- **Extensible**: Easy to add more tools for different purposes
- **Educational**: Maintains learning assistant capabilities
- **Robust**: Handles edge cases and unknown inputs gracefully

## Troubleshooting

If the agent doesn't appear in the web UI:
1. Ensure the `ROOT_AGENT` field is properly initialized
2. Check that the class compiles without errors
3. Verify the ADK web server is running with the correct source directory

## Performance Benefits

- **Tool-enhanced**: Combines LLM capabilities with specific functions
- **Fast responses**: Direct tool calls for factual information
- **Educational**: Maintains learning assistant functionality
- **Extensible**: Easy to add more tools and capabilities
