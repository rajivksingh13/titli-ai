# ParallelResearchPipeline - Parallel Research Agent

## Overview
This example demonstrates a parallel research pipeline using Google's Agent Development Kit (ADK). The agent consists of four components:

1. **RenewableEnergyResearcher**: Researches renewable energy sources using Google Search
2. **EVResearcher**: Researches electric vehicle technology using Google Search
3. **CarbonCaptureResearcher**: Researches carbon capture methods using Google Search
4. **SynthesisAgent**: Combines all research findings into a structured report

## Features

### Parallel Execution
- **Concurrent research**: Three research agents run in parallel using `ParallelAgent`
- **Efficient data gathering**: Multiple research tasks execute simultaneously
- **State management**: Results are stored in session state for synthesis

### Research Capabilities
- **Google Search integration**: Uses `GoogleSearchTool` for real-time information
- **Specialized research**: Each agent focuses on a specific domain
- **Structured output**: Results are organized by topic with clear attributions

### Web UI Support
- **ROOT_AGENT field**: Makes the agent available in the ADK web interface
- **BaseAgent compatibility**: Follows the same pattern as other working examples

## How to Use

### Console Mode
1. Run the application: `./gradlew run`
2. The agent will automatically research sustainable tech advancements
3. View the parallel research results and final synthesis

### Web UI Mode
1. Start the ADK web server: `./gradlew runAdkServer`
2. Open the web interface
3. Select "ParallelResearchPipeline" from the available agents
4. Interact with the agent through the web interface

## Agent Architecture

### Parallel Research Phase
```
ParallelWebResearchAgent
├── RenewableEnergyResearcher (parallel)
├── EVResearcher (parallel)
└── CarbonCaptureResearcher (parallel)
```

### Synthesis Phase
```
SynthesisAgent
└── Combines all research findings into structured report
```

## Output Format

The agent produces a structured report with the following sections:

```
## Summary of Recent Sustainable Technology Advancements

### Renewable Energy Findings
(Based on RenewableEnergyResearcher's findings)
[Detailed renewable energy research summary]

### Electric Vehicle Findings  
(Based on EVResearcher's findings)
[Detailed EV technology research summary]

### Carbon Capture Findings
(Based on CarbonCaptureResearcher's findings)
[Detailed carbon capture research summary]

### Overall Conclusion
[Brief concluding statement connecting all findings]
```

## Configuration

### Constants
- `APP_NAME`: "parallel_research_app"
- `USER_ID`: "research_user_01"
- `GEMINI_MODEL`: "gemini-2.0-flash"

### Tools
- `GoogleSearchTool`: Provides real-time web search capabilities

### State Keys
- `renewable_energy_result`: Stores renewable energy research findings
- `ev_technology_result`: Stores EV technology research findings
- `carbon_capture_result`: Stores carbon capture research findings

## Research Topics

The agent currently researches three main areas:

1. **Renewable Energy Sources**: Latest advancements in solar, wind, hydro, etc.
2. **Electric Vehicle Technology**: Recent developments in EV batteries, charging, etc.
3. **Carbon Capture Methods**: Current state of carbon capture and storage technologies

## Notes

- The parallel execution significantly reduces research time
- Each research agent uses Google Search for current information
- The synthesis agent strictly uses only the provided research findings
- The agent is designed for sustainable technology research but can be adapted for other topics

## Troubleshooting

If the agent doesn't appear in the web UI:
1. Ensure the `ROOT_AGENT` field is properly initialized
2. Check that the class compiles without errors
3. Verify the ADK web server is running with the correct source directory
4. Ensure Google Search API credentials are properly configured

## Performance Benefits

- **Parallel execution**: 3x faster than sequential research
- **Real-time data**: Uses Google Search for current information
- **Structured output**: Well-organized research report with clear sections
