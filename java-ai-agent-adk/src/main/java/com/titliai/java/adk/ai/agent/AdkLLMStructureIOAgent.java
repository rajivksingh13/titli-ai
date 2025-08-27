package com.titliai.java.adk.ai.agent;

// --- Full example code demonstrating LlmAgent with Tools vs. Output Schema ---

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.events.Event;
import com.google.adk.runner.Runner;
import com.google.adk.sessions.InMemorySessionService;
import com.google.adk.tools.Annotations;
import com.google.adk.tools.FunctionTool;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.google.genai.types.Schema;
import io.reactivex.rxjava3.core.Flowable;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class AdkLLMStructureIOAgent {

    AdkLLMStructureIOAgent agent = new AdkLLMStructureIOAgent();
    // --- 1. Define Constants ---

    private static final String MODEL_NAME = "gemini-2.0-flash";
    private static final String APP_NAME = "capital_tool_result_delegator";
    private static final String USER_ID = "test_user_456";
    private static final String SESSION_ID_TOOL_AGENT = "session_tool_agent_xyz";
    private static final String SESSION_ID_SCHEMA_AGENT = "session_schema_agent_xyz";

    // --- 2. Define Schemas ---

    // Input schema used by both agents
    private static final Schema COUNTRY_INPUT_SCHEMA =
            Schema.builder()
                    .type("OBJECT")
                    .description("Input for specifying a country.")
                    .properties(
                            Map.of(
                                    "country",
                                    Schema.builder()
                                            .type("STRING")
                                            .description("The country to get information about.")
                                            .build()))
                    .required(List.of("country"))
                    .build();

    // Output schema ONLY for the second agent
    private static final Schema CAPITAL_INFO_OUTPUT_SCHEMA =
            Schema.builder()
                    .type("OBJECT")
                    .description("Schema for capital city information.")
                    .properties(
                            Map.of(
                                    "capital",
                                    Schema.builder()
                                            .type("STRING")
                                            .description("The capital city of the country.")
                                            .build(),
                                    "population_estimate",
                                    Schema.builder()
                                            .type("STRING")
                                            .description("An estimated population of the capital city.")
                                            .build()))
                    .required(List.of("capital", "population_estimate"))
                    .build();
    public static BaseAgent ROOT_AGENT = initAgent();
    // --- 3. Define the Tool (Only for the first agent) ---
    // Retrieves the capital city of a given country.
    public static Map<String, Object> getCapitalCityFormated(
            @Annotations.Schema(name = "country", description = "The country to get capital for")
            String country) {
        System.out.printf("%n-- Tool Call: getCapitalCityFormated(country='%s') --%n", country);
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
        System.out.printf("-- Tool Result: '%s' --%n", result);
        return Map.of("result", result); // Tools must return a Map
    }
    public static Map<String, Object> getStructuredInfo(
            @Annotations.Schema(name = "country", description = "The country to get structured info for")
            String country) {
        // Replace with actual logic to provide structured info
        Map<String, Object> structuredInfo = new HashMap<>();
        structuredInfo.put("capital", "Example Capital");
        structuredInfo.put("population_estimate", "1,000,000");
        return structuredInfo;
    }


    public static void main(String[] args) {
        // --- 5. Set up Session Management and Runners ---
        InMemorySessionService sessionService = new InMemorySessionService();
        sessionService.createSession(APP_NAME, USER_ID, null, SESSION_ID_TOOL_AGENT).blockingGet();
        sessionService.createSession(APP_NAME, USER_ID, null, SESSION_ID_SCHEMA_AGENT).blockingGet();

        Runner capitalRunner = new Runner(ROOT_AGENT, APP_NAME, null, sessionService);
        Runner structuredRunner = new Runner(ROOT_AGENT, APP_NAME, null, sessionService);

        // --- 6. Run Interactions ---
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            while (true) {
                System.out.println("\nSelect Agent:");
                System.out.println("1. Capital Agent with Tool");
                System.out.println("2. Structured Info Agent with Schema");
                System.out.print("Enter choice (1 or 2, or 'quit' to exit): ");
                String choice = scanner.nextLine();

                if ("quit".equalsIgnoreCase(choice)) {
                    break;
                }

                System.out.print("\nYou > ");
                String userInput = scanner.nextLine();

                String queryJson = String.format("{\"country\": \"%s\"}", userInput);

                if ("1".equals(choice)) {
                    callAgentAndPrint(capitalRunner, ROOT_AGENT, SESSION_ID_TOOL_AGENT, queryJson);
                } else if ("2".equals(choice)) {
                    callAgentAndPrint(structuredRunner, ROOT_AGENT, SESSION_ID_SCHEMA_AGENT, queryJson);
                } else {
                    System.out.println("Invalid choice. Please select 1 or 2.");
                }
            }
        }
    }

    public static void callAgentAndPrint(Runner runner, BaseAgent agent, String sessionId, String queryJson) {
        System.out.printf(
                "%n>>> Calling Agent: '%s' | Session: '%s' | Query: %s%n",
                agent.name(), sessionId, queryJson);

        Content userContent = Content.fromParts(Part.fromText(queryJson));
        final String[] finalResponseContent = {"No final response received."};
        Flowable<Event> eventStream = runner.runAsync(USER_ID, sessionId, userContent);

        // Stream event response
        eventStream.blockingForEach(event -> {
            if (event.finalResponse() && event.content().isPresent()) {
                event
                        .content()
                        .get()
                        .parts()
                        .flatMap(parts -> parts.isEmpty() ? Optional.empty() : Optional.of(parts.get(0)))
                        .flatMap(Part::text)
                        .ifPresent(text -> finalResponseContent[0] = text);
            }
        });

        System.out.printf("<<< Agent '%s' Response: %s%n", agent.name(), finalResponseContent[0]);
    }
    public static BaseAgent initAgent() {
        // Initialize the two agents
        FunctionTool capitalAgentTool = FunctionTool.create(AdkLLMStructureIOAgent.class, "getCapitalCityFormated");
        // Correct the tool creation
        FunctionTool structuredInfoTool = FunctionTool.create(AdkLLMStructureIOAgent.class, "getStructuredInfo");
        LlmAgent capitalAgentWithTool =
                LlmAgent.builder()
                        .model(MODEL_NAME)
                        .name("capital_agent_tool")
                        .description("Retrieves the capital city using a specific tool.")
                        .instruction(
                                """
                                You are a helpful agent that provides the capital city of a country using a tool.
                                1. Extract the country name.
                                2. Use the `get_capital_city` tool to find the capital.
                                3. Respond clearly to the user, stating the capital city found by the tool.
                                """)
                        .tools(capitalAgentTool)
                        .inputSchema(COUNTRY_INPUT_SCHEMA)
                        .outputKey("capital_tool_result")
                        .build();

        LlmAgent structuredInfoAgentSchema =
                LlmAgent.builder()
                        .model(MODEL_NAME)
                        .name("structured_info_agent_schema")
                        .description("Provides capital and estimated population in a specific JSON format.")
                        .instruction(
                                String.format("""
                            You are an agent that provides country information.
                            Respond ONLY with a JSON object matching this exact schema: %s
                            Use your knowledge to determine the capital and estimate the population. Do not use any tools.
                            """, CAPITAL_INFO_OUTPUT_SCHEMA.toJson()))
                        .inputSchema(COUNTRY_INPUT_SCHEMA)
                        .outputSchema(CAPITAL_INFO_OUTPUT_SCHEMA)
                        .outputKey("structured_info_result")
                        .build();

        // Add the two agents as tools to the ROOT_AGENT
        return LlmAgent.builder()
                .model(MODEL_NAME)
                .name("capital_tool_result_delegator")
                .description("Root agent that delegates tasks to sub-agents.")
                .instruction("""
                You are a root agent responsible for delegating tasks to sub-agents.
                Use the appropriate sub-agent to handle user queries:
                - Use `capital_agent_tool` for retrieving capital cities.
                - Use `structured_info_agent_schema` for structured country information.
                """)
                .tools(capitalAgentTool) // Add the tool for the first agent
                .tools(structuredInfoTool) // Add the second agent as a tool
                .build();
    }
}