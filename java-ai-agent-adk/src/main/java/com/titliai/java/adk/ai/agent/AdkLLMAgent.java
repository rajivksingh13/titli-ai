package com.titliai.java.adk.ai.agent;

/**
 * Simple LLM Agent using Google's Agent Development Kit (ADK)
 * This agent demonstrates how to create an AI agent with tool calling capabilities
 * using the official Google ADK framework.
 * 
 * Based on: https://google.github.io/adk-docs/
 */
import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Flowable;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class AdkLLMAgent {

    public static BaseAgent ROOT_AGENT = initAgent();
    private static String USER_ID = "test-user";
    private static String NAME = "learning-assistant";

    public static BaseAgent initAgent() {
        return LlmAgent.builder()
                .name("learning-assistant")
                .description("An AI assistant designed to help with learning and educational tasks.")
                .model("gemini-2.0-flash")
                .instruction("""
                         You are a helpful learning assistant.
                         Your role is to assist users with educational tasks, answer questions,
                         and provide explanations on various topics.

                         You should:
                         - Explain concepts clearly with examples
                         - Guide users through problem-solving processes
                         - Create personalized learning plans based on user goals
                         - Provide step-by-step explanations for complex topics
                         - Use available tools to get the most current information
                         - Adapt your teaching style to the user's level of understanding

                         Always be encouraging and supportive in your responses.
                         Use the tools available to provide accurate and up-to-date information.
                        """)
                .build();

    }

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

}