package com.college.resume.service;

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
public class GeminiService {

    @Value("${openrouter.api.key:}")
    private String apiKey;

    private static final String OPENROUTER_API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyzeResume(String resumeText, String jobDescription) {
        if (!StringUtils.hasText(apiKey)) {
            return "AI Review not available - OpenRouter API key not configured";
        }

        try {
            String prompt = buildComprehensivePrompt(resumeText, jobDescription);
            return callOpenRouterAPI(prompt);
        } catch (Exception e) {
            System.err.println("Error generating AI review with OpenRouter: " + e.getMessage());
            e.printStackTrace();
            return "Error generating AI review with OpenRouter: " + e.getMessage();
        }
    }

    public String getAtsAnalysis(String resumeText, String jobDescription) {
        if (!StringUtils.hasText(apiKey)) {
            return "ATS Analysis not available - OpenRouter API key not configured";
        }

        try {
            String prompt = buildAtsPrompt(resumeText, jobDescription);
            return callOpenRouterAPI(prompt);
        } catch (Exception e) {
            System.err.println("Error generating ATS analysis: " + e.getMessage());
            e.printStackTrace();
            return "Error generating ATS analysis: " + e.getMessage();
        }
    }

    public String getCareerAdvice(String resumeText, String jobDescription) {
        if (!StringUtils.hasText(apiKey)) {
            return "Career Advice not available - OpenRouter API key not configured";
        }

        try {
            String prompt = buildCareerAdvicePrompt(resumeText, jobDescription);
            return callOpenRouterAPI(prompt);
        } catch (Exception e) {
            System.err.println("Error generating career advice: " + e.getMessage());
            e.printStackTrace();
            return "Error generating career advice: " + e.getMessage();
        }
    }

    private String callOpenRouterAPI(String prompt) {
        try {
            System.out.println("Calling OpenRouter API with prompt length: " + prompt.length());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "openai/gpt-3.5-turbo");
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("HTTP-Referer", "http://localhost:8080");
            headers.set("X-Title", "Resume Analyzer");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(OPENROUTER_API_URL, entity, String.class);

            System.out.println("OpenRouter API response received: " + response);

            JsonNode root = objectMapper.readTree(response);

            if (root.has("choices") && root.path("choices").size() > 0) {
                JsonNode choice = root.path("choices").get(0);
                if (choice.has("message") && choice.path("message").has("content")) {
                    String text = choice.path("message").path("content").asText();
                    System.out.println("Successfully extracted text from OpenRouter response");
                    return text;
                }
            }

            if (root.has("error")) {
                String errorMsg = root.path("error").path("message").asText();
                System.err.println("OpenRouter API error: " + errorMsg);
                return "OpenRouter API error: " + errorMsg;
            }

            System.err.println("Unexpected OpenRouter API response structure");
            return "Unexpected OpenRouter API response structure: " + response;

        } catch (Exception e) {
            System.err.println("Exception calling OpenRouter API: " + e.getMessage());
            e.printStackTrace();
            return "Error calling OpenRouter API: " + e.getMessage();
        }
    }

    private String buildComprehensivePrompt(String resumeText, String jobDescription) {
        return "Analyze this resume and provide professional feedback in exactly this format:" +
                "\n\nRESUME:\n" + resumeText +
                (StringUtils.hasText(jobDescription) ? "\n\nJOB DESCRIPTION:\n" + jobDescription : "") +
                "\n\n## Executive Summary" +
                "\n[2-3 sentence overview]" +
                "\n\n## ATS Compatibility Analysis" +
                "\nOverall Score: [0-100]" +
                "\nKeyword Optimization: [Good/Fair/Poor]" +
                "\nFormat Issues: [List specific issues]" +
                "\n\n## Strengths" +
                "\n[3-5 strengths with examples]" +
                "\n\n## Areas for Improvement" +
                "\n[3-5 weaknesses with actionable suggestions]" +
                "\n\n## Actionable Recommendations" +
                "\n[5-7 specific recommendations]" +
                (StringUtils.hasText(jobDescription) ? "\n\n## Job Match Analysis" +
                        "\nMatch Percentage: [0-100%]" +
                        "\nMissing Requirements: [List]" +
                        "\nAlignment Strengths: [List]" +
                        "\nRole-Specific Recommendations: [List]" : "") +
                "\n\nBe professional and concise.";
    }

    private String buildAtsPrompt(String resumeText, String jobDescription) {
        return "Analyze this resume for ATS compatibility." +
                "\n\nRESUME:\n" + resumeText +
                (StringUtils.hasText(jobDescription) ? "\n\nJOB DESCRIPTION:\n" + jobDescription : "") +
                "\n\nProvide ATS analysis in exactly this format:" +
                "\n\n## ATS Score (0-100)" +
                "\n### Keyword Analysis" +
                "\n### Format Issues" +
                "\n### Section Optimization" +
                "\n\n## ATS-Friendly Improvements" +
                "\n### Technical Recommendations" +
                (StringUtils.hasText(jobDescription) ? "\n\n## Job-Specific ATS Alignment" +
                        "\n### Target Keyword Match Rate" +
                        "\n### Required Skills Coverage" +
                        "\n### Experience Alignment Score" +
                        "\n### ATS Optimization for This Role" : "") +
                "\n\nBe concise and professional.";
    }

    private String buildCareerAdvicePrompt(String resumeText, String jobDescription) {
        return "Analyze this resume for career guidance." +
                "\n\nRESUME:\n" + resumeText +
                (StringUtils.hasText(jobDescription) ? "\n\nTARGET ROLE:\n" + jobDescription : "") +
                "\n\nProvide career advice in exactly this format:" +
                "\n\n## Career Assessment" +
                "\n### Current Position" +
                "\n### Skill Analysis" +
                "\n\n## Career Development Plan" +
                "\n### Skills to Develop" +
                "\n### Experience Gaps to Address" +
                "\n\n## Career Recommendations" +
                "\n### Next Steps" +
                (StringUtils.hasText(jobDescription) ? "\n\n## Role-Specific Advice" +
                        "\n### Target Role Alignment" +
                        "\n### Skill Gaps for This Position" +
                        "\n### Role-Specific Development Plan" : "") +
                "\n\nBe professional and concise.";
    }
}
