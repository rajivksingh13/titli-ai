# SequentialAgentExample - Code Pipeline Agent

## Overview
This example demonstrates a sequential code development pipeline using Google's Agent Development Kit (ADK). The agent consists of three specialized components that work in sequence to create, review, and improve Java code:

1. **CodeWriterAgent**: Generates initial Java code based on user specifications
2. **CodeReviewerAgent**: Reviews the generated code for quality and best practices
3. **CodeRefactorerAgent**: Refactors the code based on review feedback

## Features

### Sequential Execution
- **Ordered workflow**: Agents execute in a specific sequence (Write → Review → Refactor)
- **Data flow**: Each agent receives input from the previous agent's output
- **State management**: Results are stored in session state and passed between agents

### Code Development Pipeline
- **Code generation**: Creates Java code from user requirements
- **Quality review**: Comprehensive code review with multiple criteria
- **Automatic refactoring**: Improves code based on review feedback

### Web UI Support
- **ROOT_AGENT field**: Makes the agent available in the ADK web interface
- **BaseAgent compatibility**: Follows the same pattern as other working examples

## How to Use

### Console Mode
1. Run the application: `./gradlew run`
2. You'll be prompted for input for each agent in sequence:
   - **CodeWriterAgent**: Enter your code requirement (e.g., "Create a calculator class")
   - **CodeReviewerAgent**: Enter any specific review criteria (optional)
   - **CodeRefactorerAgent**: Enter any refactoring preferences (optional)

### Web UI Mode
1. Start the ADK web server: `./gradlew runAdkServer`
2. Open the web interface
3. Select "SequentialAgentExample" from the available agents
4. Interact with the agent through the web interface

## Agent Architecture

### Sequential Pipeline Flow
```
CodeWriterAgent → CodeReviewerAgent → CodeRefactorerAgent
     ↓                    ↓                    ↓
generated_code    review_comments    refactored_code
```

### Agent Details

#### CodeWriterAgent
- **Purpose**: Generates initial Java code
- **Input**: User's code requirement/specification
- **Output**: Complete Java code block in markdown format
- **Output Key**: `generated_code`

#### CodeReviewerAgent
- **Purpose**: Reviews generated code for quality
- **Input**: Generated code from CodeWriterAgent
- **Output**: Constructive feedback and suggestions
- **Output Key**: `review_comments`
- **Review Criteria**:
  - Correctness (logic errors)
  - Readability (clarity and style)
  - Efficiency (performance)
  - Edge Cases (error handling)
  - Best Practices (Java conventions)

#### CodeRefactorerAgent
- **Purpose**: Refactors code based on review feedback
- **Input**: Original code + review comments
- **Output**: Improved Java code
- **Output Key**: `refactored_code`

## Example Usage

### Input Flow
```
You > Enter input for CodeWriterAgent: Create a simple calculator class with add and subtract methods

Agent > ```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    
    public int subtract(int a, int b) {
        return a - b;
    }
}
```

You > Enter input for CodeReviewerAgent: [Press Enter for default review]

Agent > • **Correctness:** Code logic is correct for basic arithmetic operations
• **Readability:** Code is clear and follows Java naming conventions
• **Efficiency:** Simple and efficient implementation
• **Edge Cases:** Consider adding input validation for large numbers
• **Best Practices:** Good use of public methods and clear method names

You > Enter input for CodeRefactorerAgent: [Press Enter for default refactoring]

Agent > ```java
public class Calculator {
    public int add(int a, int b) {
        // Add input validation for edge cases
        if (a > Integer.MAX_VALUE - b || a < Integer.MIN_VALUE + b) {
            throw new ArithmeticException("Result would overflow");
        }
        return a + b;
    }
    
    public int subtract(int a, int b) {
        // Add input validation for edge cases
        if (b > Integer.MAX_VALUE - a || b < Integer.MIN_VALUE + a) {
            throw new ArithmeticException("Result would overflow");
        }
        return a - b;
    }
}
```

## Configuration

### Constants
- `APP_NAME`: "CodePipelineAgent"
- `USER_ID`: "test_user_456"
- `MODEL_NAME`: "gemini-2.0-flash"

### State Keys
- `generated_code`: Stores the initial code from CodeWriterAgent
- `review_comments`: Stores feedback from CodeReviewerAgent
- `refactored_code`: Stores the final improved code

## Code Review Criteria

The CodeReviewerAgent evaluates code based on five key criteria:

1. **Correctness**: Does the code work as intended? Are there logic errors?
2. **Readability**: Is the code clear and easy to understand? Follows Java style guidelines?
3. **Efficiency**: Is the code reasonably efficient? Any obvious performance bottlenecks?
4. **Edge Cases**: Does the code handle potential edge cases or invalid inputs gracefully?
5. **Best Practices**: Does the code follow common Java best practices?

## Output Formats

### CodeWriterAgent Output
- Complete Java code block enclosed in triple backticks
- Includes necessary imports and class structure
- Ready-to-use code

### CodeReviewerAgent Output
- Bulleted list of feedback points
- Focuses on most important improvements
- "No major issues found" if code is excellent

### CodeRefactorerAgent Output
- Refactored Java code block
- Addresses review comments
- Maintains functionality while improving quality

## Use Cases

### Ideal Scenarios
- **Rapid prototyping**: Quickly generate and improve code
- **Learning**: Understand code review and refactoring processes
- **Code quality**: Ensure generated code meets standards
- **Best practices**: Learn Java coding conventions

### Example Requirements
- "Create a simple REST API controller"
- "Build a utility class for string manipulation"
- "Implement a sorting algorithm"
- "Create a data model class with getters and setters"

## Notes

- Each agent specializes in one aspect of code development
- The pipeline ensures code quality through systematic review
- Agents use the Gemini 2.0 Flash model for optimal performance
- The sequential flow allows for iterative improvement

## Troubleshooting

If the agent doesn't appear in the web UI:
1. Ensure the `ROOT_AGENT` field is properly initialized
2. Check that the class compiles without errors
3. Verify the ADK web server is running with the correct source directory

## Performance Benefits

- **Specialized agents**: Each agent focuses on one task for better results
- **Systematic review**: Comprehensive code quality assessment
- **Automatic improvement**: Code is automatically enhanced based on feedback
- **Structured workflow**: Clear progression from generation to refinement
