package com.titliai.java.adk.ai.agent;

import static com.google.adk.agents.LlmAgent.IncludeContents.NONE;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
// import com.google.adk.agents.LoopAgent; // Commented out as LoopAgent might not be available in this version
import com.google.adk.agents.SequentialAgent;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
// import com.google.adk.tools.Annotations.Schema; // Not used
// import com.google.adk.tools.FunctionTool; // Not used
// import com.google.adk.tools.ToolContext; // Not used
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Flowable;

import java.nio.charset.StandardCharsets;
// import java.util.Map; // Not used
import java.util.Scanner;

/**
 * Example of an Iterative Refinement Agent using Google's Agent Development Kit (ADK)
 * Writer Agent: An LlmAgent that generates or refines a draft on a topic.
 * Critic Agent: An LlmAgent that critiques the draft, identifying areas for improvement.
 * Refiner Agent: An LlmAgent that refines the document based on critique.
 * 
 * Note: This example uses manual iteration tracking since LoopAgent might not be available
 * in the current ADK version (0.1.0).
 */
public class LoopAgentExample {
    public static BaseAgent ROOT_AGENT = initAgent();
    
    // --- Constants ---
    private static final String APP_NAME = "IterativeWritingPipeline";
    private static final String USER_ID = "test_user_457";
    private static final String MODEL_NAME = "gemini-2.0-flash";

    // --- State Keys ---
    private static final String STATE_CURRENT_DOC = "current_document";
    private static final String STATE_CRITICISM = "criticism";


    public static void main(String[] args) {
        // Test ROOT_AGENT initialization
        testRootAgent();
        
        // Use the ROOT_AGENT for consistency with web UI
        BaseAgent agent = ROOT_AGENT;
        InMemoryRunner runner = new InMemoryRunner(agent, APP_NAME);
        Session session = runner.sessionService().createSession(APP_NAME, USER_ID).blockingGet();

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            Content userMessage = null;

            // Cast to SequentialAgent to access subAgents
            SequentialAgent sequentialAgent = (SequentialAgent) agent;
            
            // Run the initial writer agent
            System.out.print("\nYou > Enter input for " + sequentialAgent.subAgents().get(0).name() + ": ");
            String userInput = scanner.nextLine();
            userMessage = Content.fromParts(Part.fromText(userInput));

            Flowable<Event> eventStream = runner.runAsync(USER_ID, session.id(), userMessage);

            System.out.print("\nAgent > ");
            eventStream.blockingForEach(event -> {
                if (event.finalResponse()) {
                    System.out.println(event.stringifyContent());
                }
            });

            // Manual iteration tracking for refinement loop
            int maxIterations = 3;
            int currentIteration = 0;
            boolean shouldContinue = true;

            while (shouldContinue && currentIteration < maxIterations) {
                currentIteration++;
                System.out.println("\n=== REFINEMENT ITERATION " + currentIteration + " ===");
                System.out.println("Max iterations allowed: " + maxIterations);

                // Run the refinement loop (critic + refiner agents)
                for (int i = 1; i < sequentialAgent.subAgents().size(); i++) {
                    System.out.print("\nYou > Enter input for " + sequentialAgent.subAgents().get(i).name() + ": ");
                    String refinementInput = scanner.nextLine();
                    userMessage = Content.fromParts(Part.fromText(refinementInput));

                    Flowable<Event> refinementEventStream = runner.runAsync(USER_ID, session.id(), userMessage);

                    System.out.print("\nAgent > ");
                    refinementEventStream.blockingForEach(event -> {
                        if (event.finalResponse()) {
                            System.out.println(event.stringifyContent());
                        }
                    });
                }

                // Check if we should continue (you can modify this logic)
                System.out.print("\nContinue refinement? (y/n): ");
                String continueInput = scanner.nextLine().toLowerCase();
                shouldContinue = continueInput.equals("y") || continueInput.equals("yes");

                if (currentIteration >= maxIterations) {
                    System.out.println("⚠️  Reached maximum iterations limit!");
                }
            }
                
            // Print summary after the agent completes
            System.out.println("\n" + "=".repeat(50));
            System.out.println("AGENT EXECUTION SUMMARY");
            System.out.println("=".repeat(50));
            System.out.println("Agent completed successfully!");
            System.out.println("Total iterations completed: " + currentIteration);
            System.out.println("Max iterations allowed: " + maxIterations);
        }
    }

    // --- Tool Definition ---
    // Note: Removed exitLoop tool as we're using manual iteration control

    // Test method to verify ROOT_AGENT initialization
    public static void testRootAgent() {
        System.out.println("Testing ROOT_AGENT initialization...");
        System.out.println("ROOT_AGENT name: " + ROOT_AGENT.name());
        System.out.println("ROOT_AGENT description: " + ROOT_AGENT.description());
        
        if (ROOT_AGENT instanceof SequentialAgent) {
            SequentialAgent seqAgent = (SequentialAgent) ROOT_AGENT;
            System.out.println("Number of sub-agents: " + seqAgent.subAgents().size());
            for (int i = 0; i < seqAgent.subAgents().size(); i++) {
                System.out.println("Sub-agent " + i + ": " + seqAgent.subAgents().get(i).name());
            }
        }
        System.out.println("ROOT_AGENT initialization test completed successfully!");
    }


    public static BaseAgent initAgent() {
        // Initialize the sub-agents
        LlmAgent initialWriterAgent = LlmAgent.builder()
                .model(MODEL_NAME)
                .name("InitialWriterAgent")
                .description("Writes the initial document draft based on the topic.")
                .instruction("""
                        You are a Creative Writing Assistant tasked with starting a story.
                        Write the *first draft* of a short story (aim for 2-4 sentences).
                        Base the content *only* on the topic provided below.
                        """)
                .outputKey(STATE_CURRENT_DOC)
                .build();

        LlmAgent criticAgent = LlmAgent.builder()
                .model(MODEL_NAME)
                .name("CriticAgent")
                .description("Reviews the current draft, providing critique.")
                .instruction("""
                        You are a Constructive Critic AI reviewing a short document draft.
                        Provide actionable feedback or state 'No major issues found.'
                        """)
                .outputKey(STATE_CRITICISM)
                .build();

        LlmAgent refinerAgent = LlmAgent.builder()
                .model(MODEL_NAME)
                .name("RefinerAgent")
                .description("Refines the document based on critique.")
                .instruction("""
                You are a Creative Writing Assistant refining a document based on feedback.
                **Current Document:**
                ```
                {{current_document}}
                ```
                **Critique/Suggestions:**
                {{criticism}}

                **Task:**
                Carefully apply the suggestions from the critique to improve the 'Current Document'.
                Output *only* the refined document text.
                """)
                .outputKey("current_document")
                .includeContents(NONE)
                .build();

        // Create a SequentialAgent for the refinement loop
        SequentialAgent refinementLoop = SequentialAgent.builder()
                .name("RefinementLoop")
                .description("Refines the document with critique (single iteration).")
                .subAgents(criticAgent, refinerAgent)
                .build();

        return SequentialAgent.builder()
                .name(APP_NAME)
                .description("Writes an initial document and iteratively refines it.")
                .subAgents(initialWriterAgent, refinementLoop)
                .build();
    }

}
