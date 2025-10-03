package com.titliai.java.adk.ai.agent;

import com.google.adk.agents.LlmAgent;
import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.CallbackContext;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.adk.sessions.State;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * What it Shows: This example demonstrates the before_agent_callback. This callback runs right before the agent's main processing logic starts for a given request.
 * How it Works: The callback function (check_if_agent_should_run) looks at a flag (skip_llm_agent) in the session's state.
 * If the flag is True, the callback returns a types.Content object. This tells the ADK framework to skip the agent's main execution entirely and use the callback's returned content as the final response.
 * If the flag is False (or not set), the callback returns None or an empty object. This tells the ADK framework to proceed with the agent's normal execution (calling the LLM in this case).
 * Expected Outcome: You'll see two scenarios:
 * In the session with the skip_llm_agent: True state, the agent's LLM call is bypassed, and the output comes directly from the callback ("Agent... skipped...").
 * In the session without that state flag, the callback allows the agent to run, and you see the actual response from the LLM (e.g., "Hello!").
 */
public class BeforeAgentCallbackExample {

    private static final String APP_NAME = "AgentWithBeforeAgentCallback";
    private static final String USER_ID = "test_user_456";
    private static final String SESSION_ID = "session_id_123";
    private static final String MODEL_NAME = "gemini-2.0-flash";

    public static void main(String[] args) {
        BeforeAgentCallbackExample callbackAgent = new BeforeAgentCallbackExample();
        callbackAgent.defineAgent("Write a document about a cat");
    }

    // --- 1. Define the Callback Function ---
    /**
     * Logs entry and checks 'skip_llm_agent' in session state. If True, returns Content to skip the
     * agent's execution. If False or not present, returns None to allow execution.
     */
    public Maybe<Content> checkIfAgentShouldRun(CallbackContext callbackContext) {
        String agentName = callbackContext.agentName();
        String invocationId = callbackContext.invocationId();
        State currentState = callbackContext.state();

        System.out.printf("%n[Callback] Entering agent: %s (Inv: %s)%n", agentName, invocationId);
        System.out.printf("[Callback] Current State: %s%n", currentState.entrySet());

        // Check the condition in session state dictionary
        if (Boolean.TRUE.equals(currentState.get("skip_llm_agent"))) {
            System.out.printf(
                    "[Callback] State condition 'skip_llm_agent=True' met: Skipping agent %s", agentName);
            // Return Content to skip the agent's run
            return Maybe.just(
                    Content.fromParts(
                            Part.fromText(
                                    String.format(
                                            "Agent %s skipped by before_agent_callback due to state.", agentName))));
        }

        System.out.printf(
                "[Callback] State condition 'skip_llm_agent=True' NOT met: Running agent %s \n", agentName);
        // Return empty response to allow the LlmAgent's normal execution
        return Maybe.empty();
    }

    public void defineAgent(String prompt) {
        // --- 2. Setup Agent with Callback ---
        BaseAgent llmAgentWithBeforeCallback =
                LlmAgent.builder()
                        .model(MODEL_NAME)
                        .name(APP_NAME)
                        .instruction("You are a concise assistant.")
                        .description("An LLM agent demonstrating stateful before_agent_callback")
                        // You can also use a sync version of this callback "beforeAgentCallbackSync"
                        .beforeAgentCallback(this::checkIfAgentShouldRun)
                        .build();

        // --- 3. Setup Runner and Sessions using InMemoryRunner ---

        // Use InMemoryRunner - it includes InMemorySessionService
        InMemoryRunner runner = new InMemoryRunner(llmAgentWithBeforeCallback, APP_NAME);
        // Scenario 1: Initial state is null, which means 'skip_llm_agent' will be false in the callback
        // check
        runAgent(runner, null, prompt);
        // Scenario 2: Agent will be skipped (state has skip_llm_agent=true)
        runAgent(runner, new ConcurrentHashMap<>(Map.of("skip_llm_agent", true)), prompt);
    }

    public void runAgent(InMemoryRunner runner, ConcurrentHashMap<String, Object> initialState, String prompt) {
        // InMemoryRunner automatically creates a session service. Create a session using the service.
        Session session =
                runner
                        .sessionService()
                        .createSession(APP_NAME, USER_ID, initialState, SESSION_ID)
                        .blockingGet();
        Content userMessage = Content.fromParts(Part.fromText(prompt));

        // Run the agent
        Flowable<Event> eventStream = runner.runAsync(USER_ID, session.id(), userMessage);

        // Print final output (either from LLM or callback override)
        eventStream.blockingForEach(
                event -> {
                    if (event.finalResponse()) {
                        System.out.println(event.stringifyContent());
                    }
                });
    }
}
