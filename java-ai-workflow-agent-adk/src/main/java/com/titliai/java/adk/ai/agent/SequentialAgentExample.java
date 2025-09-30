package com.titliai.java.adk.ai.agent;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.agents.SequentialAgent;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Flowable;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Example of a Sequential Agent using Google's Agent Development Kit (ADK)
 * Code Writer Agent: An LLM Agent that generates initial code based on a specification.
 * Code Reviewer Agent: An LLM Agent that reviews the generated code for errors, style issues, and adherence to best practices. It receives the output of the Code Writer Agent.
 * Code Refactorer Agent: An LLM Agent that takes the reviewed code (and the reviewer's comments) and refactors it to improve quality and address issues.
 *
 * The output from each sub-agent is passed to the next by storing them in state via Output Key.
 */

public class SequentialAgentExample {
    public static BaseAgent ROOT_AGENT = initAgent();
    private static final String APP_NAME = "CodePipelineAgent";
    private static final String USER_ID = "test_user_456";
    private static final String MODEL_NAME = "gemini-2.0-flash";

    public static void main(String[] args) {
    // Initialize the SequentialAgent using initAgent
    BaseAgent sequentialAgent = initAgent();

    // Create an InMemoryRunner
    InMemoryRunner runner = new InMemoryRunner(sequentialAgent, APP_NAME);
    Session session = runner.sessionService().createSession(APP_NAME, USER_ID).blockingGet();

    try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
        Content userMessage = null;

        for (int i = 0; i < ((SequentialAgent) sequentialAgent).subAgents().size(); i++) {
            System.out.print("\nYou > Enter input for " + ((SequentialAgent) sequentialAgent).subAgents().get(i).name() + ": ");
            String userInput = scanner.nextLine();
            userMessage = Content.fromParts(Part.fromText(userInput));

            // Run the current agent
            Flowable<Event> eventStream = runner.runAsync(USER_ID, session.id(), userMessage);

            System.out.print("\nAgent > ");
            eventStream.blockingForEach(event -> {
                if (event.finalResponse()) {
                    System.out.println(event.stringifyContent());
                }
            });
        }
    }
}


    public static BaseAgent initAgent() {
    // Initialize the sub-agents
    LlmAgent codeWriterAgent = LlmAgent.builder()
            .model(MODEL_NAME)
            .name("CodeWriterAgent")
            .description("Writes initial Java code based on a specification.")
            .instruction("""
                    You are a Java Code Generator.
                    Based *only* on the user's request, write Java code that fulfills the requirement.
                    Output *only* the complete Java code block, enclosed in triple backticks (```java ... ```).
                    Do not add any other text before or after the code block.
                    """)
            .outputKey("generated_code")
            .build();

    LlmAgent codeReviewerAgent = LlmAgent.builder()
            .model(MODEL_NAME)
            .name("CodeReviewerAgent")
            .description("Reviews code and provides feedback.")
            .instruction("""
                    You are an expert Java Code Reviewer.
                    Your task is to provide constructive feedback on the provided code.
                                                
                    **Code to Review:**
                    ```java
                    {generated_code}
                    ```
                                                
                    **Review Criteria:**
                    1. **Correctness:** Does the code work as intended? Are there logic errors?
                    2. **Readability:** Is the code clear and easy to understand? Follows Java style guidelines?
                    3. **Efficiency:** Is the code reasonably efficient? Any obvious performance bottlenecks?
                    4. **Edge Cases:** Does the code handle potential edge cases or invalid inputs gracefully?
                    5. **Best Practices:** Does the code follow common Java best practices?
                                                
                    **Output:**
                    Provide your feedback as a concise, bulleted list. Focus on the most important points for improvement.
                    If the code is excellent and requires no changes, simply state: "No major issues found."
                    """)
            .outputKey("review_comments")
            .build();

    LlmAgent codeRefactorerAgent = LlmAgent.builder()
            .model(MODEL_NAME)
            .name("CodeRefactorerAgent")
            .description("Refactors code based on review comments.")
            .instruction("""
                    You are a Java Code Refactoring AI.
                    Your goal is to improve the given Java code based on the provided review comments.
                                                
                    **Original Code:**
                    ```java
                    {generated_code}
                    ```
                                                
                    **Review Comments:**
                    {review_comments}
                                                
                    **Task:**
                    Carefully apply the suggestions from the review comments to refactor the original code.
                    If the review comments state "No major issues found," return the original code unchanged.
                    Ensure the final code is complete, functional, and includes necessary imports and docstrings.
                                                
                    **Output:**
                    Output *only* the final, refactored Java code block, enclosed in triple backticks (```java ... ```).
                    """)
            .outputKey("refactored_code")
            .build();

    // Build the SequentialAgent
    return SequentialAgent.builder()
            .name("CodePipelineAgent")
            .description("Executes a sequence of code writing, reviewing, and refactoring.")
            .subAgents(codeWriterAgent, codeReviewerAgent, codeRefactorerAgent)
            .build();
}
}