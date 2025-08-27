package com.titliai.java.adk.ai.agent;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.adk.tools.Annotations;
import com.google.adk.tools.FunctionTool;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Flowable;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AdkLLMFunctionToolAgent {
    AdkLLMFunctionToolAgent agent = new AdkLLMFunctionToolAgent();
    public static BaseAgent ROOT_AGENT = initAgent();
    private static String USER_ID = "test-user";
    private static String NAME = "capital_agent";

    public static void main(String[] args) {
        InMemoryRunner runner = new InMemoryRunner(ROOT_AGENT);
        Session session =
                runner
                        .sessionService()
                        .createSession(NAME, USER_ID)
                        .blockingGet();
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            while (true) {
                System.out.print("\nYou > ");
                String userInput = scanner.nextLine();
                if ("quit".equalsIgnoreCase(userInput)) {
                    break;
                }
                Content userMsg = Content.fromParts(Part.fromText(userInput));
                Flowable<Event> events = runner.runAsync(USER_ID, session.id(), userMsg);
                System.out.print("\nAgent > ");
                events.blockingForEach(event -> System.out.println(event.stringifyContent()));
            }
        }
    }

    // Define a tool function
// Retrieves the capital city of a given country.
    public static Map<String, Object> getCapitalCity(
            @Annotations.Schema(name = "country", description = "The country to get capital for")
            String country) {
        // Replace with actual logic (e.g., API call, database lookup)
        Map<String, String> countryCapitals = new HashMap<>();
        countryCapitals.put("canada", "Ottawa");
        countryCapitals.put("france", "Paris");
        countryCapitals.put("india", "New Delhi");
        countryCapitals.put("sri lanka", "Colombo");
        countryCapitals.put("china", "Beijing");
        countryCapitals.put("usa", "Washington, D.C.");

        String result =
                countryCapitals.getOrDefault(
                        country.toLowerCase(), "Sorry, I couldn't find the capital for " + country + ".");
        return Map.of("result", result); // Tools must return a Map
    }

    public static BaseAgent initAgent() {
        FunctionTool capitalTool = FunctionTool.create(AdkLLMFunctionToolAgent.class, "getCapitalCity");
        return LlmAgent.builder()
                .name("capital_agent") // Agent name
                .description("An AI assistant designed to help with learning and educational tasks.")// Agent description
                .model("gemini-2.0-flash")// LLM model to use
                .instruction("""
                        You are an agent that provides the capital city of a country.
                        When a user asks for the capital of a country:
                        1. Identify the country name from the user's query.
                        2. Use the `get_capital_city` tool to find the capital.
                        3. Respond clearly to the user, stating the capital city.
                        Example Query: "What's the capital of {country}?"
                        Example Response: "The capital of France is Paris."
                        """)// Agent instructions
                .tools(capitalTool)
                .generateContentConfig(
                        GenerateContentConfig.builder()
                                .maxOutputTokens(250)
                                .temperature(0.2F)
                                .topP(0.8F)
                                .build())// Generation parameters You can adjust how the underlying LLM generates responses using generate_content_config
                .build();

    }


}
