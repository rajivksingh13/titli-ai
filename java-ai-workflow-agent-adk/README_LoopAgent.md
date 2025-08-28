# LoopAgentExample - Iterative Refinement Agent

## Overview
This example demonstrates an iterative refinement agent using Google's Agent Development Kit (ADK). The agent consists of three components:

1. **InitialWriterAgent**: Creates the first draft of a story
2. **CriticAgent**: Reviews the draft and provides feedback
3. **RefinerAgent**: Refines the document based on the critique

## Features

### Iteration Tracking
- **Manual iteration control**: You can choose to continue or stop after each iteration
- **Maximum iteration limit**: Set to 3 iterations by default
- **Clear iteration markers**: Shows `=== REFINEMENT ITERATION X ===` for each iteration
- **Progress tracking**: Displays current iteration vs. maximum allowed

### Web UI Support
- **ROOT_AGENT field**: Makes the agent available in the ADK web interface
- **BaseAgent compatibility**: Follows the same pattern as SequentialAgentExample

## How to Use

### Console Mode
1. Run the application: `./gradlew run`
2. Enter a topic for the initial story
3. For each iteration:
   - Provide input for the CriticAgent
   - Provide input for the RefinerAgent
   - Choose whether to continue (y/n)

### Web UI Mode
1. Start the ADK web server: `./gradlew runAdkServer`
2. Open the web interface
3. Select "LoopAgentExample" from the available agents
4. Interact with the agent through the web interface

## Iteration Tracking Output

When running the agent, you'll see output like:

```
=== REFINEMENT ITERATION 1 ===
Max iterations allowed: 3

You > Enter input for CriticAgent: [your input]
Agent > [critic response]

You > Enter input for RefinerAgent: [your input]
Agent > [refined document]

Continue refinement? (y/n): y

=== REFINEMENT ITERATION 2 ===
Max iterations allowed: 3
...
```

## Configuration

### Constants
- `APP_NAME`: "IterativeWritingPipeline"
- `USER_ID`: "test_user_457"
- `MODEL_NAME`: "gemini-2.0-flash"
- `maxIterations`: 3 (in main method)

### State Keys
- `STATE_CURRENT_DOC`: "current_document"
- `STATE_CRITICISM`: "criticism"

## Notes

- This example uses `SequentialAgent` instead of `LoopAgent` due to ADK version compatibility
- Manual iteration tracking is implemented in the main method
- The agent is designed for creative writing tasks but can be adapted for other use cases
- Each iteration runs the critic and refiner agents in sequence

## Troubleshooting

If the agent doesn't appear in the web UI:
1. Ensure the `ROOT_AGENT` field is properly initialized
2. Check that the class compiles without errors
3. Verify the ADK web server is running with the correct source directory
