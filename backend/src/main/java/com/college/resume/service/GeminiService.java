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
        return "You are a world-class resume analyzer and career coach with deep expertise in ATS systems and recruitment."
                +
                "\n\nTASK: Analyze the following resume comprehensively." +
                "\n\nRESUME TEXT:\n" + resumeText +
                (StringUtils.hasText(jobDescription) ? "\n\nJOB DESCRIPTION:\n" + jobDescription : "") +
                "\n\nProvide a detailed professional review in Markdown format with these EXACT sections:" +
                "\n\n## Executive Summary" +
                "\nBrief 2-3 sentence overview of the resume's overall quality and marketability." +
                "\n\n## ATS Compatibility Analysis" +
                "\nEvaluate how well this resume will perform against Applicant Tracking Systems. Include:" +
                "\n- Keyword optimization" +
                "\n- Format compatibility" +
                "\n- Section clarity" +
                "\n- Potential ATS blockers" +
                "\n\n## Key Strengths" +
                "\nList 3-5 specific strengths with examples from the resume." +
                "\n\n## Areas for Improvement" +
                "\nIdentify 3-5 specific weaknesses with actionable suggestions." +
                "\n\n## Actionable Recommendations" +
                "\nProvide 5-7 concrete, prioritized recommendations to improve the resume." +
                "\n\n" + (StringUtils.hasText(jobDescription) ? "## Job Match Analysis" +
                        "\nCompare the resume against the job description and provide:" +
                        "\n- Match percentage (0-100%)" +
                        "\n- Missing key requirements" +
                        "\n- Alignment strengths" +
                        "\n- Specific recommendations for this role" : "")
                +
                "\n\nUse professional, constructive tone. Be specific and actionable.";
    }

    private String buildAtsPrompt(String resumeText, String jobDescription) {
        return "You are an ATS (Applicant Tracking System) expert. Analyze this resume for ATS compatibility." +
                "\n\nRESUME:\n" + resumeText +
                (StringUtils.hasText(jobDescription) ? "\n\nJOB DESCRIPTION:\n" + jobDescription : "") +
                "\n\nProvide ATS-specific analysis in Markdown format:" +
                "\n\n## ATS Score (0-100)" +
                "\n## Keyword Analysis" +
                "\n## Format Issues" +
                "\n## Section Optimization" +
                "\n## ATS-Friendly Improvements" +
                "\n## Technical Recommendations";
    }

    private String buildCareerAdvicePrompt(String resumeText, String jobDescription) {
        return "You are an experienced career coach and recruiter. Provide career advice based on this resume." +
                "\n\nRESUME:\n" + resumeText +
                (StringUtils.hasText(jobDescription) ? "\n\nTARGET JOB:\n" + jobDescription : "") +
                "\n\nProvide career development advice in Markdown format:" +
                "\n\n## Career Positioning" +
                "\n## Skill Development Priorities" +
                "\n## Experience Gaps to Address" +
                "\n## Career Growth Recommendations" +
                "\n## Next Steps (6-12 months)" +
                "\n## Market Positioning Advice";
    }
}
