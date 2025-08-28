# AdkLLMAgent - Basic Learning Assistant

## Overview
This is a simple LLM Agent using Google's Agent Development Kit (ADK) that demonstrates how to create a basic AI assistant for educational tasks. The agent is designed to help with learning and educational activities.

## Features

### Basic LLM Agent
- **Simple conversation**: Interactive chat interface with the AI assistant
- **Educational focus**: Specialized for learning and educational tasks
- **No external tools**: Uses only the LLM's knowledge base
- **Web UI support**: Available in the ADK web interface

### Learning Capabilities
- **Concept explanations**: Clear explanations with examples
- **Problem-solving guidance**: Step-by-step problem-solving assistance
- **Personalized learning plans**: Custom learning plans based on user goals
- **Adaptive teaching**: Adjusts to user's understanding level

## How to Use

### Console Mode
1. Run the application: `./gradlew run`
2. Start chatting with the agent:
   ```
   You > What is machine learning?
   Agent > [AI response explaining machine learning]
   
   You > Can you help me understand calculus?
   Agent > [AI response with calculus explanation]
   ```
3. Type `quit` to exit

### Web UI Mode
1. Start the ADK web server: `./gradlew runAdkServer`
2. Open the web interface
3. Select "AdkLLMAgent" from the available agents
4. Interact with the agent through the web interface

## Agent Configuration

### Constants
- `USER_ID`: "test-user"
- `NAME`: "learning-assistant"
- `MODEL`: "gemini-2.0-flash"

### Agent Instructions
The agent is configured with specific instructions to:
- Explain concepts clearly with examples
- Guide users through problem-solving processes
- Create personalized learning plans
- Provide step-by-step explanations
- Adapt teaching style to user's level
- Be encouraging and supportive

## Example Interactions

### Learning Concepts
```
You > Explain photosynthesis in simple terms
Agent > Photosynthesis is like a plant's way of making its own food...
```

### Problem Solving
```
You > I'm stuck on this math problem: 2x + 5 = 13
Agent > Let me help you solve this step by step...
```

### Learning Plans
```
You > I want to learn Python programming
Agent > Great! Let me create a personalized learning plan for you...
```

## Use Cases

### Ideal Scenarios
- **Homework help**: Getting explanations for difficult concepts
- **Study assistance**: Creating study plans and reviewing topics
- **Concept clarification**: Understanding complex topics
- **Learning guidance**: Getting direction for new subjects

### Example Topics
- **Mathematics**: Algebra, calculus, statistics
- **Science**: Physics, chemistry, biology
- **Programming**: Python, Java, web development
- **Languages**: Grammar, vocabulary, writing
- **History**: Events, dates, historical figures

## Agent Architecture

### Simple Structure
```
AdkLLMAgent
└── LlmAgent (learning-assistant)
    ├── Model: gemini-2.0-flash
    ├── Instructions: Educational assistance
    └── No external tools
```

### Session Management
- **InMemoryRunner**: Handles agent execution
- **Session**: Maintains conversation context
- **Event streaming**: Real-time response handling

## Output Format

### Response Structure
- **Natural language**: Conversational responses
- **Educational content**: Explanations, examples, guidance
- **Encouraging tone**: Supportive and helpful language

### Example Response
```
Agent > Machine learning is a subset of artificial intelligence that enables computers to learn and improve from experience without being explicitly programmed. Think of it like teaching a computer to recognize patterns, similar to how you might learn to recognize different types of dogs by seeing many examples.

Key concepts:
1. **Data**: The computer learns from examples (data)
2. **Algorithms**: Mathematical methods that find patterns
3. **Training**: The process of teaching the model
4. **Prediction**: Using the learned patterns to make decisions

Would you like me to explain any specific aspect of machine learning in more detail?
```

## Notes

- **No external tools**: This agent relies solely on the LLM's knowledge
- **Educational focus**: Optimized for learning and teaching scenarios
- **Conversational**: Designed for natural, back-and-forth interactions
- **Supportive**: Encourages learning and provides positive reinforcement

## Troubleshooting

If the agent doesn't appear in the web UI:
1. Ensure the `ROOT_AGENT` field is properly initialized
2. Check that the class compiles without errors
3. Verify the ADK web server is running with the correct source directory

## Performance Benefits

- **Simple and fast**: No external API calls or tool dependencies
- **Reliable**: Works offline with just the LLM model
- **Educational**: Specialized for learning tasks
- **User-friendly**: Natural conversation interface
