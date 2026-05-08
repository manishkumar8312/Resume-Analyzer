package com.college.resume.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OpenAIService {

    @Value("${openai.api.key:}")
    private String apiKey;

    public String reviewResume(String resumeText, String jobDescription) {
        if (!StringUtils.hasText(apiKey)) {
            return "OpenAI review not available - API key not configured";
        }

        try {
            // OpenAI integration would go here
            // For now, return a placeholder
            return "OpenAI integration pending implementation";
        } catch (Exception e) {
            return "Error generating OpenAI review: " + e.getMessage();
        }
    }
}
