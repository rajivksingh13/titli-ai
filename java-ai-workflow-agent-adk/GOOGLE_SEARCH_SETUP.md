# Google Search API Setup Guide

## Overview
The `ParallelResearchPipeline` uses Google Search API to gather real-time information. If the API credentials are not configured, the agent will fall back to using its knowledge base.

## Setup Instructions

### 1. Get Google Search API Credentials
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Custom Search API
4. Create credentials (API Key)

### 2. Configure Environment Variables
Set the following environment variables:

```bash
# Windows
set GOOGLE_API_KEY=your_api_key_here
set GOOGLE_CSE_ID=your_custom_search_engine_id_here

# Linux/Mac
export GOOGLE_API_KEY=your_api_key_here
export GOOGLE_CSE_ID=your_custom_search_engine_id_here
```

### 3. Create Custom Search Engine
1. Go to [Google Programmable Search Engine](https://programmablesearchengine.google.com/)
2. Create a new search engine
3. Note the Search Engine ID (CSE ID)

### 4. Test Configuration
Run the agent and check the output:
- If you see "✅ GoogleSearchTool is available" - configuration is working
- If you see "⚠️ GoogleSearchTool is not available" - check your credentials

## Fallback Behavior
If Google Search is not available, the agent will:
- Use the LLM's knowledge base for research
- Still provide structured research reports
- Work without external web search capabilities

## Troubleshooting

### Common Issues
1. **API Key Invalid**: Check that your API key is correct and has the Custom Search API enabled
2. **CSE ID Missing**: Ensure you have created a Custom Search Engine and have the correct ID
3. **Quota Exceeded**: Google Search API has usage limits - check your quota in Google Cloud Console

### Error Messages
- `GoogleSearchTool initialization failed` - Check API credentials
- `NullPointerException` - Usually indicates missing or invalid credentials

## Alternative Setup
If you don't want to use Google Search, the agent will work with just the LLM's knowledge base. Simply run without setting the environment variables.
