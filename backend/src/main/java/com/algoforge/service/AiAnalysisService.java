package com.algoforge.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiAnalysisService {

    private final String apiKey;
    private final RestTemplate restTemplate;
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent";

    public AiAnalysisService() {
        this.restTemplate = new RestTemplate();
        // Load API key from .env file
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.apiKey = dotenv.get("GEMINI_API_KEY");
    }

    public String analyzeComplexity(String code) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Error: Gemini API key is not configured in the backend .env file.";
        }

        String prompt = "You are a Senior Software Engineer. Analyze the time and space complexity (Big-O) of the following code. " +
                "Provide a brief, clear explanation of why. Do not format the response with markdown blocks, just plain text with line breaks.\n\n" +
                "Code:\n" + code;

        try {
            // Build Gemini Request Payload
            Map<String, Object> requestBody = new HashMap<>();
            
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            
            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));
            
            requestBody.put("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Make the HTTP request to Gemini API
            Map<String, Object> response = restTemplate.postForObject(GEMINI_URL, entity, Map.class);
            
            // Extract the text response from the deeply nested JSON map
            return extractTextFromResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling AI Service: " + e.getMessage();
        }
    }

    private String extractTextFromResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            return "Failed to parse AI response. Raw output: " + response.toString();
        }
    }
}
