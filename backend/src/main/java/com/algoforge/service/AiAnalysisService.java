package com.algoforge.service;

import com.algoforge.exception.AiAnalysisException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiAnalysisService {

    private final String apiKey;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent";

    public AiAnalysisService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        
        // Load API key from .env file
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.apiKey = dotenv.get("GEMINI_API_KEY");
    }

    @Cacheable(value = "aiComplexityCache", key = "#code.hashCode()")
    public String analyzeComplexity(String code) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Error: Gemini API key is not configured in the backend .env file.";
        }

        String prompt = "You are a Senior Software Engineer. Analyze the time and space complexity (Big-O) of the following code.\n" +
                "You MUST output your response strictly as a JSON object with exactly these three keys: \"time_complexity\", \"space_complexity\", and \"explanation\".\n" +
                "Do not include any markdown formatting like ```json or any other text.\n\n" +
                "Code:\n" + code;

        // Implementation of manual retry for resilience (Max 3 attempts)
        int maxAttempts = 3;
        int delayMs = 1000;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return executeApiCall(prompt);
            } catch (Exception e) {
                if (attempt == maxAttempts) {
                    // Fallback / Graceful Degradation after max retries
                    System.err.println("AI Service failed after " + maxAttempts + " attempts: " + e.getMessage());
                    return "**Service Temporarily Unavailable**\n\n" +
                           "The AI complexity analyzer is currently overloaded or experiencing issues. Please try again later.\n\n" +
                           "*Error details: " + e.getMessage() + "*";
                }
                
                // Exponential Backoff
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
                delayMs *= 2;
            }
        }
        
        return "Unknown Error"; // Should never be reached
    }

    private String executeApiCall(String prompt) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        
        // Force JSON output
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("response_mime_type", "application/json");
        requestBody.put("generationConfig", generationConfig);
        
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        
        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));
        
        requestBody.put("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        Map<String, Object> response = restTemplate.postForObject(GEMINI_URL, entity, Map.class);
        
        String rawJsonStr = extractTextFromResponse(response);
        
        // Validate and Parse the LLM's JSON output
        JsonNode jsonNode = objectMapper.readTree(rawJsonStr);
        
        if (!jsonNode.has("time_complexity") || !jsonNode.has("space_complexity") || !jsonNode.has("explanation")) {
            throw new AiAnalysisException("LLM returned JSON missing required schema keys.");
        }
        
        // Format into Markdown string
        return "**Time Complexity:** `" + jsonNode.get("time_complexity").asText() + "`\n" +
               "**Space Complexity:** `" + jsonNode.get("space_complexity").asText() + "`\n\n" +
               "**Explanation:**\n" + jsonNode.get("explanation").asText();
    }

    private String extractTextFromResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            throw new AiAnalysisException("Failed to extract content from Gemini API response payload.", e);
        }
    }
}
