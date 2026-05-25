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
public class GeminiService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyzeResume(String resumeText, String jobDescription) {
        if (!StringUtils.hasText(apiKey)) {
            return "AI Review not available - Gemini API key not configured";
        }

        try {
            String prompt = buildComprehensivePrompt(resumeText, jobDescription);
            return callGeminiAPI(prompt);
        } catch (Exception e) {
            return "Error generating AI review with Gemini: " + e.getMessage();
        }
    }

    public String getAtsAnalysis(String resumeText, String jobDescription) {
        if (!StringUtils.hasText(apiKey)) {
            return "ATS Analysis not available - Gemini API key not configured";
        }

        try {
            String prompt = buildAtsPrompt(resumeText, jobDescription);
            return callGeminiAPI(prompt);
        } catch (Exception e) {
            return "Error generating ATS analysis: " + e.getMessage();
        }
    }

    public String getCareerAdvice(String resumeText, String jobDescription) {
        if (!StringUtils.hasText(apiKey)) {
            return "Career Advice not available - Gemini API key not configured";
        }

        try {
            String prompt = buildCareerAdvicePrompt(resumeText, jobDescription);
            return callGeminiAPI(prompt);
        } catch (Exception e) {
            return "Error generating career advice: " + e.getMessage();
        }
    }

    private String callGeminiAPI(String prompt) {
        try {
            System.out.println("Calling Gemini API with prompt length: " + prompt.length());
            
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            content.put("parts", List.of(part));
            requestBody.put("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(GEMINI_API_URL + apiKey, entity, String.class);
            
            System.out.println("Gemini API response received: " + response);
            
            JsonNode root = objectMapper.readTree(response);
            
            // Check if response has candidates
            if (root.has("candidates") && root.path("candidates").size() > 0) {
                JsonNode candidate = root.path("candidates").get(0);
                if (candidate.has("content") && candidate.path("content").has("parts") && candidate.path("content").path("parts").size() > 0) {
                    String text = candidate.path("content").path("parts").get(0).path("text").asText();
                    System.out.println("Successfully extracted text from Gemini response");
                    return text;
                }
            }
            
            // Check for error response
            if (root.has("error")) {
                String errorMsg = root.path("error").path("message").asText();
                System.err.println("Gemini API error: " + errorMsg);
                return "Gemini API error: " + errorMsg;
            }
            
            System.err.println("Unexpected Gemini API response structure");
            return "Unexpected Gemini API response structure: " + response;

        } catch (Exception e) {
            System.err.println("Exception calling Gemini API: " + e.getMessage());
            e.printStackTrace();
            return "Error calling Gemini API: " + e.getMessage();
        }
    }

    private String buildComprehensivePrompt(String resumeText, String jobDescription) {
        return "You are a world-class resume analyzer and career coach with deep expertise in ATS systems and recruitment." +
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
                "\n- Specific recommendations for this role" : "") +
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
