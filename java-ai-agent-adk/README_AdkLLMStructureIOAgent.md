# AdkLLMStructureIOAgent - Structured Data Agent

## Overview
This agent demonstrates advanced LLM Agent capabilities using Google's Agent Development Kit (ADK) with both function tools and structured input/output schemas. It shows how to create agents that can handle structured data and provide formatted responses.

## Features

### Dual Agent Architecture
- **Capital Agent with Tool**: Uses function tools to retrieve capital city information
- **Structured Info Agent**: Uses input/output schemas for structured data handling
- **Root Agent**: Delegates tasks to appropriate sub-agents

### Structured Data Handling
- **Input schemas**: Define expected input format (JSON)
- **Output schemas**: Define response format with specific fields
- **Tool integration**: Function tools for data retrieval
- **Schema validation**: Ensures data format compliance

### Interactive Selection
- **Agent selection**: Choose between different agent types
- **Session management**: Separate sessions for different agents
- **Structured queries**: JSON-formatted input for precise data requests

## How to Use

### Console Mode
1. Run the application: `./gradlew run`
2. Select an agent type:
   ```
   Select Agent:
   1. Capital Agent with Tool
   2. Structured Info Agent with Schema
   Enter choice (1 or 2, or 'quit' to exit): 1
   ```
3. Enter your query in JSON format:
   ```
   You > {"country": "France"}
   ```
4. View the structured response

### Web UI Mode
1. Start the ADK web server: `./gradlew runAdkServer`
2. Open the web interface
3. Select "AdkLLMStructureIOAgent" from the available agents
4. Interact with the agent through the web interface

## Agent Types

### 1. Capital Agent with Tool
- **Purpose**: Retrieves capital cities using function tools
- **Input**: JSON with country name
- **Output**: Capital city information
- **Tool**: `getCapitalCityFormated` function

### 2. Structured Info Agent with Schema
- **Purpose**: Provides structured country information
- **Input**: JSON with country name
- **Output**: Structured JSON with capital and population
- **Schema**: Enforced input/output format

## Agent Configuration

### Constants
- `MODEL_NAME`: "gemini-2.0-flash"
- `APP_NAME`: "capital_tool_result_delegator"
- `USER_ID`: "test_user_456"
- `SESSION_ID_TOOL_AGENT`: "session_tool_agent_xyz"
- `SESSION_ID_SCHEMA_AGENT`: "session_schema_agent_xyz"

### Available Countries
- Canada, France, India, Sri Lanka, China, USA

## Schema Definitions

### Input Schema (COUNTRY_INPUT_SCHEMA)
```json
{
  "type": "OBJECT",
  "description": "Input for specifying a country.",
  "properties": {
    "country": {
      "type": "STRING",
      "description": "The country to get information about."
    }
  },
  "required": ["country"]
}
```

### Output Schema (CAPITAL_INFO_OUTPUT_SCHEMA)
```json
{
  "type": "OBJECT",
  "description": "Schema for capital city information.",
  "properties": {
    "capital": {
      "type": "STRING",
      "description": "The capital city of the country."
    },
    "population_estimate": {
      "type": "STRING",
      "description": "An estimated population of the capital city."
    }
  },
  "required": ["capital", "population_estimate"]
}
```

## Tool Functions

### getCapitalCityFormated
```java
public static Map<String, Object> getCapitalCityFormated(
    @Annotations.Schema(name = "country", description = "The country to get capital for")
    String country)
```
- **Purpose**: Retrieves capital city information
- **Returns**: Map with "result" key containing capital city
- **Logging**: Shows tool calls and results

### getStructuredInfo
```java
public static Map<String, Object> getStructuredInfo(
    @Annotations.Schema(name = "country", description = "The country to get structured info for")
    String country)
```
- **Purpose**: Provides structured country information
- **Returns**: Map with capital and population data

## Example Interactions

### Capital Agent (Tool-based)
```
Select Agent: 1
You > {"country": "France"}

>>> Calling Agent: 'capital_agent_tool' | Session: 'session_tool_agent_xyz' | Query: {"country": "France"}
-- Tool Call: getCapitalCityFormated(country='France') --
-- Tool Result: 'Paris' --
<<< Agent 'capital_agent_tool' Response: The capital of France is Paris.
```

### Structured Info Agent (Schema-based)
```
Select Agent: 2
You > {"country": "India"}

>>> Calling Agent: 'structured_info_agent_schema' | Session: 'session_schema_agent_xyz' | Query: {"country": "India"}
<<< Agent 'structured_info_agent_schema' Response: {"capital": "New Delhi", "population_estimate": "20,000,000"}
```

## Agent Architecture

### Root Agent Structure
```
AdkLLMStructureIOAgent (Root)
├── capital_agent_tool
│   ├── Tool: getCapitalCityFormated
│   ├── Input Schema: COUNTRY_INPUT_SCHEMA
│   └── Output Key: capital_tool_result
└── structured_info_agent_schema
    ├── Input Schema: COUNTRY_INPUT_SCHEMA
    ├── Output Schema: CAPITAL_INFO_OUTPUT_SCHEMA
    └── Output Key: structured_info_result
```

### Session Management
- **Separate sessions**: Each agent type has its own session
- **Session isolation**: No interference between different agent types
- **State management**: Maintains context within each session

## Use Cases

### Ideal Scenarios
- **Structured data retrieval**: Getting formatted country information
- **Tool demonstration**: Understanding function tool integration
- **Schema validation**: Ensuring data format compliance
- **Multi-agent systems**: Delegating tasks to specialized agents

### Example Queries
- `{"country": "Canada"}` - Get capital city
- `{"country": "USA"}` - Get structured country info
- `{"country": "India"}` - Compare tool vs schema approaches

## Input/Output Formats

### Input Format
All queries must be in JSON format:
```json
{"country": "country_name"}
```

### Output Formats

#### Tool Agent Output
- **Natural language**: Conversational responses
- **Tool integration**: Shows tool calls and results
- **Capital information**: Direct capital city answers

#### Schema Agent Output
- **Structured JSON**: Enforced output format
- **Multiple fields**: Capital and population information
- **Schema compliance**: Validates against defined schema

## Development Patterns

### Creating Schema-based Agents
1. **Define input schema**: Specify expected input format
2. **Define output schema**: Specify response format
3. **Configure agent**: Use `.inputSchema()` and `.outputSchema()`
4. **Handle validation**: Agent validates against schemas

### Creating Tool-based Agents
1. **Define tool function**: Create static method with annotations
2. **Register tool**: Use `FunctionTool.create()`
3. **Configure agent**: Add tool to agent configuration
4. **Handle tool calls**: Agent automatically calls tools when needed

## Notes

- **Dual approach**: Demonstrates both tool and schema-based methods
- **Session isolation**: Each agent type operates independently
- **Structured data**: Enforces data format requirements
- **Extensible**: Easy to add more agents and tools

## Troubleshooting

If the agent doesn't appear in the web UI:
1. Ensure the `ROOT_AGENT` field is properly initialized
2. Check that the class compiles without errors
3. Verify the ADK web server is running with the correct source directory

## Performance Benefits

- **Structured responses**: Predictable data formats
- **Tool integration**: Direct access to specific functions
- **Schema validation**: Ensures data quality
- **Multi-agent**: Specialized agents for different tasks
