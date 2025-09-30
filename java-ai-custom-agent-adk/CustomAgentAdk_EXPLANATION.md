# CustomAgentAdk - Comprehensive Code Explanation

## Overview
This document provides a detailed explanation of the `CustomAgentAdk` class, which demonstrates a sophisticated multi-agent workflow for story generation, critique, revision, and quality checks. This is an advanced example of orchestrating multiple AI agents to work together in a complex pipeline.

---

## Architecture Overview

The `CustomAgentAdk` implements a **multi-stage story generation pipeline** with the following workflow:

```
1. Story Generation → 2. Critique & Revision Loop → 3. Post-Processing → 4. Conditional Regeneration
```

---

## Key Components Breakdown

### 1. **Constants and Configuration**

#### **Application Constants**
```java
private static final String APP_NAME = "story_app";
private static final String USER_ID = "user_12345";
private static final String SESSION_ID = "session_123344";
private static final String MODEL_NAME = "gemini-2.0-flash";
```

**What they are:**
- **APP_NAME**: Application identifier for session management
- **USER_ID**: Unique user identifier for session isolation
- **SESSION_ID**: Specific session identifier for state management
- **MODEL_NAME**: LLM model used across all agents

**Why they're used:**
- **Session Management**: Enables proper state tracking across the multi-agent workflow
- **User Isolation**: Ensures each user has their own conversation context
- **State Persistence**: Allows agents to share data through session state
- **Consistency**: All agents use the same model for consistent behavior

---

### 2. **Individual LLM Agents Configuration**

#### **StoryGenerator Agent**
```java
LlmAgent storyGenerator = LlmAgent.builder()
    .name("StoryGenerator")
    .model(MODEL_NAME)
    .description("Generates the initial story.")
    .instruction("""
        You are a story writer. Write a short story (around 100 words) about a cat,
        based on the topic: {topic}
        """)
    .outputKey("current_story")
    .build();
```

**Configuration Analysis:**

| Component | Value | Purpose | Why Used |
|-----------|-------|---------|----------|
| **Name** | `"StoryGenerator"` | Agent identification | Distinguishes this agent in logs and debugging |
| **Description** | `"Generates the initial story."` | Agent purpose summary | Documents the agent's role in the workflow |
| **Model** | `"gemini-2.0-flash"` | LLM engine | Fast, cost-effective for creative writing tasks |
| **Instruction** | Story writing prompt | Behavior definition | Sets creative writing persona and word limit |
| **OutputKey** | `"current_story"` | State storage key | Stores generated story in session state for other agents |

**Why This Configuration:**
- **Creative Task**: Story writing requires creativity, not complex reasoning
- **Flash Model**: Sufficient for creative tasks, faster and cheaper
- **Template Variables**: `{topic}` allows dynamic story topics
- **State Management**: `outputKey` enables data flow between agents

---

#### **Critic Agent**
```java
LlmAgent critic = LlmAgent.builder()
    .name("Critic")
    .model(MODEL_NAME)
    .description("Critiques the story.")
    .instruction("""
        You are a story critic. Review the story: {current_story}. Provide 1-2 sentences of constructive criticism
        on how to improve it. Focus on plot or character.
        """)
    .outputKey("criticism")
    .build();
```

**Configuration Analysis:**

| Component | Purpose | Why This Design |
|-----------|---------|-----------------|
| **Name** | `"Critic"` | Clear role identification in multi-agent system |
| **Description** | `"Critiques the story."` | Documents analytical role |
| **Instruction** | Critical analysis prompt | Sets analytical persona, focuses on constructive feedback |
| **Template Variables** | `{current_story}` | Accesses story from previous agent's output |
| **OutputKey** | `"criticism"` | Stores feedback for revision agent |

**Why This Configuration:**
- **Analytical Role**: Different persona from creative writer
- **Constructive Focus**: Encourages improvement rather than just criticism
- **Specific Scope**: Focuses on plot/character to avoid overwhelming feedback
- **State Integration**: Uses `{current_story}` to access previous agent's work

---

#### **Reviser Agent**
```java
LlmAgent reviser = LlmAgent.builder()
    .name("Reviser")
    .model(MODEL_NAME)
    .description("Revises the story based on criticism.")
    .instruction("""
        You are a story reviser. Revise the story: {current_story}, based on the criticism: {criticism}. 
        Output only the revised story.
        """)
    .outputKey("current_story") // Overwrites the original story
    .build();
```

**Configuration Analysis:**

| Component | Purpose | Why This Design |
|-----------|---------|-----------------|
| **Name** | `"Reviser"` | Clear revision role identification |
| **Instruction** | Revision prompt | Combines original story and criticism for improvement |
| **Template Variables** | `{current_story}`, `{criticism}` | Accesses both story and feedback |
| **OutputKey** | `"current_story"` | **Overwrites** original story with improved version |

**Why This Configuration:**
- **Iterative Improvement**: Same output key allows story evolution
- **Dual Input**: Uses both story and criticism for context-aware revision
- **Clean Output**: "Output only the revised story" ensures clean formatting
- **State Management**: Overwrites original to maintain single source of truth

---

#### **GrammarCheck Agent**
```java
LlmAgent grammarCheck = LlmAgent.builder()
    .name("GrammarCheck")
    .model(MODEL_NAME)
    .description("Checks grammar and suggests corrections.")
    .instruction("""
        You are a grammar checker. Check the grammar of the story: {current_story}. 
        Output only the suggested corrections as a list, or output 'Grammar is good!' if there are no errors.
        """)
    .outputKey("grammar_suggestions")
    .build();
```

**Configuration Analysis:**

| Component | Purpose | Why This Design |
|-----------|---------|-----------------|
| **Name** | `"GrammarCheck"` | Clear quality assurance role |
| **Instruction** | Grammar analysis prompt | Sets technical editing persona |
| **Template Variables** | `{current_story}` | Accesses current story version |
| **OutputKey** | `"grammar_suggestions"` | Stores grammar feedback separately |

**Why This Configuration:**
- **Quality Assurance**: Focuses on technical correctness
- **Separate Storage**: Different output key preserves grammar feedback
- **Conditional Output**: Handles both error and no-error cases
- **Technical Focus**: Different from creative/analytical agents

---

#### **ToneCheck Agent**
```java
LlmAgent toneCheck = LlmAgent.builder()
    .name("ToneCheck")
    .model(MODEL_NAME)
    .description("Analyzes the tone of the story.")
    .instruction("""
        You are a tone analyzer. Analyze the tone of the story: {current_story}. 
        Output only one word: 'positive' if the tone is generally positive, 'negative' if 
        the tone is generally negative, or 'neutral' otherwise.
        """)
    .outputKey("tone_check_result")
    .build();
```

**Configuration Analysis:**

| Component | Purpose | Why This Design |
|-----------|---------|-----------------|
| **Name** | `"ToneCheck"` | Clear emotional analysis role |
| **Instruction** | Tone analysis prompt | Sets analytical persona for emotional assessment |
| **Template Variables** | `{current_story}` | Accesses current story version |
| **OutputKey** | `"tone_check_result"` | **Critical for workflow control** |

**Why This Configuration:**
- **Workflow Control**: Output determines conditional regeneration
- **Standardized Output**: Single word output enables programmatic decisions
- **Emotional Analysis**: Different from technical/creative analysis
- **Decision Making**: Enables automated quality gates

---

### 3. **Composite Agent Configuration**

#### **LoopAgent (CriticReviserLoop)**
```java
LoopAgent loopAgent = LoopAgent.builder()
    .name("CriticReviserLoop")
    .description("Iteratively critiques and revises the story.")
    .subAgents(critic, reviser)
    .maxIterations(2)
    .build();
```

**Configuration Analysis:**

| Component | Purpose | Why This Design |
|-----------|---------|-----------------|
| **Name** | `"CriticReviserLoop"` | Describes the iterative process |
| **Description** | `"Iteratively critiques and revises the story."` | Documents the improvement cycle |
| **SubAgents** | `critic, reviser` | Defines the agents in the loop |
| **MaxIterations** | `2` | **Prevents infinite loops** |

**Why This Configuration:**
- **Iterative Improvement**: Allows multiple rounds of critique and revision
- **Controlled Iteration**: Max iterations prevent runaway loops
- **Quality Enhancement**: Multiple improvement cycles increase story quality
- **Workflow Efficiency**: Encapsulates the improvement process

---

#### **SequentialAgent (PostProcessing)**
```java
SequentialAgent sequentialAgent = SequentialAgent.builder()
    .name("PostProcessing")
    .description("Performs grammar and tone checks sequentially.")
    .subAgents(grammarCheck, toneCheck)
    .build();
```

**Configuration Analysis:**

| Component | Purpose | Why This Design |
|-----------|---------|-----------------|
| **Name** | `"PostProcessing"` | Describes the quality assurance phase |
| **Description** | `"Performs grammar and tone checks sequentially."` | Documents the QA process |
| **SubAgents** | `grammarCheck, toneCheck` | Defines the QA agents |
| **Execution Order** | Sequential | Grammar check before tone check |

**Why This Configuration:**
- **Quality Assurance**: Final checks before completion
- **Sequential Execution**: Grammar check before tone analysis
- **Independent Checks**: Each agent performs different analysis
- **Workflow Completion**: Final stage of the pipeline

---

### 4. **Main CustomAgentAdk Configuration**

```java
public CustomAgentAdk(
        String name, LlmAgent storyGenerator, LoopAgent loopAgent, SequentialAgent sequentialAgent) {
    super(
            name,
            "Orchestrates story generation, critique, revision, and checks.",
            List.of(storyGenerator, loopAgent, sequentialAgent),
            null,
            null);
    // ...
}
```

**Configuration Analysis:**

| Component | Purpose | Why This Design |
|-----------|---------|-----------------|
| **Name** | `APP_NAME` ("story_app") | Application identifier |
| **Description** | `"Orchestrates story generation, critique, revision, and checks."` | Documents the orchestration role |
| **SubAgents** | `List.of(storyGenerator, loopAgent, sequentialAgent)` | Defines the workflow stages |
| **Custom Logic** | `runAsyncImpl()` | Implements custom orchestration |

**Why This Configuration:**
- **Orchestration Role**: Coordinates multiple agents in a complex workflow
- **Custom Logic**: Implements conditional flows and stage management
- **Workflow Control**: Manages the entire story generation pipeline
- **State Management**: Handles data flow between agents

---

## Workflow Implementation

### **Stage 1: Story Generation**
```java
Flowable<Event> storyGenFlow = runStage(storyGenerator, invocationContext, "StoryGenerator");
```

**What it does:**
- Generates initial story based on topic
- Stores result in `current_story` state key
- Provides foundation for all subsequent stages

**Why this stage:**
- **Foundation**: All other agents depend on having a story to work with
- **Creative Input**: Human-like creative writing
- **Topic Flexibility**: Adapts to any given topic

---

### **Stage 2: Critique-Revision Loop**
```java
Flowable<Event> criticReviserFlow = Flowable.defer(() -> {
    if (!isStoryGenerated(invocationContext)) {
        // Error handling
        return Flowable.empty();
    }
    return runStage(loopAgent, invocationContext, "CriticReviserLoop");
});
```

**What it does:**
- Iteratively critiques and revises the story
- Maximum 2 iterations to prevent infinite loops
- Improves story quality through multiple feedback cycles

**Why this stage:**
- **Quality Enhancement**: Multiple improvement cycles
- **Automated Feedback**: AI provides its own critique
- **Controlled Iteration**: Prevents runaway improvement loops

---

### **Stage 3: Post-Processing**
```java
Flowable<Event> postProcessingFlow = Flowable.defer(() -> {
    return runStage(sequentialAgent, invocationContext, "PostProcessing");
});
```

**What it does:**
- Performs grammar check
- Analyzes story tone
- Provides quality assurance

**Why this stage:**
- **Technical Quality**: Grammar and language correctness
- **Emotional Analysis**: Tone assessment for content appropriateness
- **Final Validation**: Ensures story meets quality standards

---

### **Stage 4: Conditional Regeneration**
```java
Flowable<Event> conditionalRegenFlow = Flowable.defer(() -> {
    String toneCheckResult = (String) invocationContext.session().state().get("tone_check_result");
    
    if ("negative".equalsIgnoreCase(toneCheckResult)) {
        return runStage(storyGenerator, invocationContext, "StoryGenerator (Regen)");
    } else {
        return Flowable.empty(); // No regeneration needed
    }
});
```

**What it does:**
- Checks tone analysis result
- Regenerates story if tone is negative
- Maintains positive content standards

**Why this stage:**
- **Content Safety**: Ensures appropriate tone
- **Quality Gate**: Prevents negative content
- **Automated Correction**: Self-corrects inappropriate content

---

## State Management

### **Session State Keys**

| Key | Purpose | Set By | Used By |
|-----|---------|--------|---------|
| `topic` | Story topic | User input | StoryGenerator |
| `current_story` | Current story version | StoryGenerator, Reviser | All agents |
| `criticism` | Critique feedback | Critic | Reviser |
| `grammar_suggestions` | Grammar feedback | GrammarCheck | (Display only) |
| `tone_check_result` | Tone analysis | ToneCheck | Conditional flow |

### **State Flow Diagram**

```
User Input (topic)
    ↓
StoryGenerator → current_story
    ↓
Critic → criticism
    ↓
Reviser → current_story (updated)
    ↓
GrammarCheck → grammar_suggestions
    ↓
ToneCheck → tone_check_result
    ↓
Conditional: If negative → StoryGenerator (regen)
```

---

## Configuration Summary Table

| Agent | Name | Model | Description | Instruction Focus | Output Key | Purpose |
|-------|------|-------|-------------|-------------------|------------|---------|
| **StoryGenerator** | `"StoryGenerator"` | `gemini-2.0-flash` | `"Generates the initial story."` | Creative writing | `current_story` | Foundation |
| **Critic** | `"Critic"` | `gemini-2.0-flash` | `"Critiques the story."` | Analytical feedback | `criticism` | Quality improvement |
| **Reviser** | `"Reviser"` | `gemini-2.0-flash` | `"Revises the story based on criticism."` | Revision | `current_story` | Iterative improvement |
| **GrammarCheck** | `"GrammarCheck"` | `gemini-2.0-flash` | `"Checks grammar and suggests corrections."` | Technical analysis | `grammar_suggestions` | Language quality |
| **ToneCheck** | `"ToneCheck"` | `gemini-2.0-flash` | `"Analyzes the tone of the story."` | Emotional analysis | `tone_check_result` | Content appropriateness |
| **LoopAgent** | `"CriticReviserLoop"` | N/A | `"Iteratively critiques and revises the story."` | Orchestration | N/A | Iterative improvement |
| **SequentialAgent** | `"PostProcessing"` | N/A | `"Performs grammar and tone checks sequentially."` | Orchestration | N/A | Quality assurance |
| **CustomAgentAdk** | `"story_app"` | N/A | `"Orchestrates story generation, critique, revision, and checks."` | Orchestration | N/A | Workflow management |

---

## Key Design Patterns

### 1. **Multi-Agent Orchestration**
- **What**: Multiple specialized agents working together
- **Why**: Each agent has specific expertise (creative, analytical, technical)
- **How**: Custom orchestration logic in `runAsyncImpl()`

### 2. **State-Based Communication**
- **What**: Agents communicate through session state
- **Why**: Enables loose coupling and data persistence
- **How**: `outputKey` and template variables like `{current_story}`

### 3. **Conditional Workflow**
- **What**: Workflow branches based on analysis results
- **Why**: Enables quality gates and content safety
- **How**: Tone check result determines regeneration

### 4. **Iterative Improvement**
- **What**: Multiple rounds of critique and revision
- **Why**: Continuous quality enhancement
- **How**: LoopAgent with controlled iterations

---

## Production Considerations

### **Strengths**
- **Modular Design**: Each agent has a single responsibility
- **Quality Assurance**: Multiple validation stages
- **Content Safety**: Automated tone checking
- **Flexible Topics**: Adapts to any story topic

### **Improvements Needed**
- **Error Handling**: More robust error recovery
- **Configuration**: Externalize hardcoded values
- **Monitoring**: Add performance metrics
- **Testing**: Unit tests for each agent
- **Scalability**: Consider distributed execution
- **Cost Management**: Monitor LLM usage costs

---

## Use Cases

This multi-agent system is ideal for:

1. **Content Generation**: Automated story creation with quality assurance
2. **Educational Tools**: Teaching creative writing with AI feedback
3. **Content Moderation**: Ensuring appropriate tone and quality
4. **Creative Assistance**: AI-powered writing improvement
5. **Quality Assurance**: Automated content review pipelines

---

## Key Takeaways

1. **Name**: Provides clear identification in complex multi-agent systems
2. **Description**: Documents each agent's specific role in the workflow
3. **Model**: Consistent model choice enables predictable behavior across agents
4. **Instruction**: Most critical - defines each agent's personality and expertise
5. **OutputKey**: Enables state-based communication between agents
6. **Orchestration**: Custom logic manages complex multi-stage workflows
7. **Quality Gates**: Conditional flows ensure content meets standards

This architecture demonstrates how to build sophisticated AI workflows by combining multiple specialized agents, each with their own expertise, working together through well-defined interfaces and state management.
