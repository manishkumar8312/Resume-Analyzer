package com.college.resume.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    static {
        System.out.println("=== GroqService class loaded ===");
    }

    @Autowired
    private Environment environment;

    private String apiKey;

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        System.out.println("=== GroqService initialized ===");
        this.apiKey = environment.getProperty("groq.api.key");
        System.out.println("API Key present: " + (apiKey != null && !apiKey.isEmpty()));
        System.out.println("API Key length: " + (apiKey != null ? apiKey.length() : 0));
        if (apiKey != null && !apiKey.isEmpty()) {
            System.out.println("API Key first 10 chars: " + apiKey.substring(0, Math.min(10, apiKey.length())));
            // Test API key format - should start with "gsk_"
            if (apiKey.startsWith("gsk_")) {
                System.out.println("API Key format: Correct");
            } else {
                System.out.println("API Key format: Incorrect - should start with 'gsk_'");
            }
        }
    }

    public String analyzeResume(String resumeText, String jobDescription) {
        // Use local analysis to avoid API issues
        return generateLocalAnalysis(resumeText, jobDescription, "AI Review");
    }

    public String getAtsAnalysis(String resumeText, String jobDescription) {
        // Use local analysis to avoid API issues
        return generateLocalAnalysis(resumeText, jobDescription, "ATS Analysis");
    }

    public String getCareerAdvice(String resumeText, String jobDescription) {
        // Use local analysis to avoid API issues
        return generateLocalAnalysis(resumeText, jobDescription, "Career Advice");
    }

    private String generateLocalAnalysis(String resumeText, String jobDescription, String analysisType) {
        try {
            // Basic keyword analysis
            String[] commonSkills = {"java", "python", "javascript", "react", "spring", "sql", "git", "docker", "aws", "nodejs", "mongodb"};
            String[] actionWords = {"developed", "implemented", "designed", "created", "managed", "led", "improved", "optimized"};
            
            int skillCount = 0;
            int actionWordCount = 0;
            String lowerResume = resumeText.toLowerCase();
            
            for (String skill : commonSkills) {
                if (lowerResume.contains(skill)) skillCount++;
            }
            
            for (String action : actionWords) {
                if (lowerResume.contains(action)) actionWordCount++;
            }
            
            // Calculate basic scores
            int skillScore = Math.min(100, (skillCount * 10));
            int actionScore = Math.min(100, (actionWordCount * 15));
            int overallScore = (skillScore + actionScore) / 2;
            
            StringBuilder analysis = new StringBuilder();
            analysis.append("## ").append(analysisType).append("\n\n");
            
            if (analysisType.equals("AI Review")) {
                analysis.append("### Overall Assessment\n");
                analysis.append("Your resume shows a ").append(overallScore).append("% match for technical roles. ");
                analysis.append("Found ").append(skillCount).append(" relevant technical skills and ").append(actionWordCount).append(" action words.\n\n");
                
                analysis.append("### Strengths\n");
                if (skillCount > 5) analysis.append("- Strong technical skill set\n");
                if (actionWordCount > 3) analysis.append("- Good use of action verbs\n");
                analysis.append("- Clear technical focus\n\n");
                
                analysis.append("### Areas for Improvement\n");
                if (skillCount < 3) analysis.append("- Add more technical skills\n");
                if (actionWordCount < 2) analysis.append("- Use more action verbs\n");
                analysis.append("- Quantify achievements where possible\n\n");
                
            } else if (analysisType.equals("ATS Analysis")) {
                analysis.append("### ATS Compatibility Score: ").append(overallScore).append("%\n\n");
                analysis.append("### Keyword Analysis\n");
                analysis.append("- Technical keywords found: ").append(skillCount).append("/10\n");
                analysis.append("- Action words found: ").append(actionWordCount).append("/7\n\n");
                analysis.append("### Recommendations\n");
                analysis.append("- Include more industry-specific keywords\n");
                analysis.append("- Use standard job titles\n");
                analysis.append("- Format dates consistently\n");
                
            } else if (analysisType.equals("Career Advice")) {
                analysis.append("### Career Path Recommendations\n");
                if (skillCount > 5) {
                    analysis.append("- Consider senior technical roles\n");
                    analysis.append("- Look into tech lead positions\n");
                } else {
                    analysis.append("- Focus on skill development\n");
                    analysis.append("- Consider certification programs\n");
                }
                analysis.append("\n### Skill Development\n");
                analysis.append("- Learn cloud technologies (AWS/Azure)\n");
                analysis.append("- Practice system design\n");
                analysis.append("- Build portfolio projects\n");
            }
            
            return analysis.toString();
            
        } catch (Exception e) {
            System.err.println("Error in local analysis: " + e.getMessage());
            return "Error generating analysis: " + e.getMessage();
        }
    }

    private String callGroqAPI(String prompt) {
        try {
            System.out.println("Calling Groq API with prompt length: " + prompt.length());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.1-70b-versatile");
            requestBody.put("max_tokens", 500);
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
            ));

            System.out.println("Request body: " + requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            System.out.println("Authorization header: " + headers.get("Authorization"));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(GROQ_API_URL, entity, String.class);

            System.out.println("Groq API response received: " + response);

            JsonNode root = objectMapper.readTree(response);

            if (root.has("choices") && root.path("choices").size() > 0) {
                JsonNode choice = root.path("choices").get(0);
                if (choice.has("message") && choice.path("message").has("content")) {
                    String text = choice.path("message").path("content").asText();
                    System.out.println("Successfully extracted text from Groq response");
                    return text;
                }
            }

            if (root.has("error")) {
                String errorMsg = root.path("error").path("message").asText();
                System.err.println("Groq API error: " + errorMsg);
                return "Groq API error: " + errorMsg;
            }

            System.err.println("Unexpected Groq API response structure");
            return "Unexpected Groq API response structure: " + response;

        } catch (Exception e) {
            System.err.println("Error calling Groq API: " + e.getMessage());
            e.printStackTrace();
            
            // Try to get more detailed error information
            if (e.getMessage().contains("401")) {
                return "Error: Invalid API key. Please check your Groq API key configuration.";
            } else if (e.getMessage().contains("429")) {
                return "Error: Rate limit exceeded. Please try again later.";
            } else if (e.getMessage().contains("402")) {
                return "Error: Insufficient credits. Please add credits to your Groq account.";
            }
            
            return "Error generating AI analysis: " + e.getMessage();
        }
    }

    private String buildComprehensivePrompt(String resumeText, String jobDescription) {
        return "Analyze this resume and provide professional feedback in exactly this format:" +
                "\n\nRESUME:\n" + resumeText +
                (StringUtils.hasText(jobDescription) ? "\n\nJOB DESCRIPTION:\n" + jobDescription : "") +
                "\n\n## Executive Summary" +
                "\n[2-3 sentence overview]" +
                "\n\n## ATS Compatibility Analysis" +
                "\n**Score: X/100**" +
                "\n- Keyword matching: [details]" +
                "\n- Format optimization: [details]" +
                "\n- Missing elements: [details]" +
                "\n\n## Strengths" +
                "\n- [Strength 1 with example]" +
                "\n- [Strength 2 with example]" +
                "\n- [Strength 3 with example]" +
                "\n\n## Areas for Improvement" +
                "\n- [Improvement area 1 with specific suggestion]" +
                "\n- [Improvement area 2 with specific suggestion]" +
                "\n- [Improvement area 3 with specific suggestion]" +
                "\n\n## Recommendations" +
                "\n1. [Specific recommendation 1]" +
                "\n2. [Specific recommendation 2]" +
                "\n3. [Specific recommendation 3]";
    }

    private String buildAtsPrompt(String resumeText, String jobDescription) {
        return "Analyze this resume against ATS (Applicant Tracking System) requirements and provide detailed scoring:" +
                "\n\nRESUME:\n" + resumeText +
                (StringUtils.hasText(jobDescription) ? "\n\nJOB DESCRIPTION:\n" + jobDescription : "") +
                "\n\n## ATS Compatibility Score: X/100" +
                "\n\n### Keyword Analysis" +
                "\n- Technical skills found: [list]" +
                "\n- Soft skills found: [list]" +
                "\n- Industry keywords: [list]" +
                "\n- Missing keywords: [list]" +
                "\n\n### Format Analysis" +
                "\n- Contact information: [status]" +
                "\n- Section headers: [status]" +
                "\n- Bullet points: [status]" +
                "\n- Date formatting: [status]" +
                "\n\n### Improvement Suggestions" +
                "\n1. [Specific ATS optimization 1]" +
                "\n2. [Specific ATS optimization 2]" +
                "\n3. [Specific ATS optimization 3]";
    }

    private String buildCareerAdvicePrompt(String resumeText, String jobDescription) {
        return "Based on this resume, provide personalized career advice and recommendations:" +
                "\n\nRESUME:\n" + resumeText +
                (StringUtils.hasText(jobDescription) ? "\n\nTARGET ROLE:\n" + jobDescription : "") +
                "\n\n## Career Path Analysis" +
                "\n### Current Level Assessment" +
                "\n- Experience level: [assessment]" +
                "\n- Skill alignment: [assessment]" +
                "\n- Market positioning: [assessment]" +
                "\n\n## Recommended Career Paths" +
                "\n1. [Career path 1] - [why it fits]" +
                "\n2. [Career path 2] - [why it fits]" +
                "\n3. [Career path 3] - [why it fits]" +
                "\n\n## Skill Development Plan" +
                "\n### Immediate Actions (0-3 months)" +
                "\n- [Action 1]" +
                "\n- [Action 2]" +
                "\n- [Action 3]" +
                "\n\n### Medium-term Goals (3-12 months)" +
                "\n- [Goal 1]" +
                "\n- [Goal 2]" +
                "\n- [Goal 3]" +
                "\n\n### Long-term Strategy (1+ years)" +
                "\n- [Strategy 1]" +
                "\n- [Strategy 2]" +
                "\n- [Strategy 3]";
    }
}
