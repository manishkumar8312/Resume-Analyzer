package com.resumeanalyzer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${openai.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String reviewResume(String resumeText, String jobDescription) {
        if (!StringUtils.hasText(apiKey)) {
            return "AI Review not available - OpenAI API key not configured";
        }

        try {
            String systemPrompt = "You are an expert resume reviewer and career coach. " +
                    "Provide a comprehensive review of the resume focusing on: " +
                    "1. Overall strengths and weaknesses " +
                    "2. Specific improvements for each section with concrete examples " +
                    "3. Actionable recommendations with clear steps to implement " +
                    "4. ATS compatibility and keyword optimization " +
                    "5. Professional formatting suggestions " +
                    "6. Missing skills or experiences to add " +
                    "7. Quantifiable achievements to include " +
                    "For each recommendation, provide: " +
                    "- What to change " +
                    "- Why it's important " +
                    "- How to implement it " +
                    "- Example of the change " +
                    "Be specific, constructive, and professional. " +
                    "Focus on recommendations that will actually improve job prospects.";

            String userPrompt = "Please review the following resume:\n\n" + resumeText;

            if (StringUtils.hasText(jobDescription)) {
                userPrompt += "\n\nJob Description (for context):\n" + jobDescription;
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1000);
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            String response = restTemplate.postForObject(OPENAI_API_URL, entity, String.class);

            if (!StringUtils.hasText(response)) {
                return "Error generating AI review: empty response from OpenAI";
            }

            JsonNode root = objectMapper.readTree(response);

            if (root.has("error")) {
                String errorMessage = root.path("error").path("message").asText("Unknown OpenAI error");
                return "OpenAI API error: " + errorMessage;
            }

            JsonNode choices = root.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode content = choices.get(0).path("message").path("content");
                if (content.isTextual()) {
                    return content.asText();
                }
            }

            return "Unexpected OpenAI response structure: " + response;
        } catch (Exception e) {
            return "Error generating AI review: " + e.getMessage();
        }
    }
}
