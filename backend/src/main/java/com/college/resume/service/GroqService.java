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
        System.out.println("DEBUG: API Key present: " + (apiKey != null && !apiKey.isEmpty()));
        if (apiKey != null) {
            System.out.println("DEBUG: API Key value: " + (apiKey.length() > 5 ? apiKey.substring(0, 5) + "..." : "TOO SHORT"));
            System.out.println("DEBUG: Is placeholder: " + apiKey.equals("${GROQ_API_KEY:}"));
        }
    }

    public String analyzeResume(String resumeText, String jobDescription) {
        System.out.println("DEBUG: Entering analyzeResume. API Key status: " + (StringUtils.hasText(apiKey) && !apiKey.equals("${GROQ_API_KEY:}")));
        if (StringUtils.hasText(apiKey) && !apiKey.equals("${GROQ_API_KEY:}")) {
            System.out.println("DEBUG: Calling Groq API...");
            return callGroqAPI(buildComprehensivePrompt(resumeText, jobDescription));
        }
        System.out.println("DEBUG: Falling back to local analysis.");
        return generateLocalAnalysis(resumeText, jobDescription, "AI Review");
    }

    public String getAtsAnalysis(String resumeText, String jobDescription) {
        if (StringUtils.hasText(apiKey) && !apiKey.equals("${GROQ_API_KEY:}")) {
            return callGroqAPI(buildAtsPrompt(resumeText, jobDescription));
        }
        return generateLocalAnalysis(resumeText, jobDescription, "ATS Analysis");
    }

    public String getCareerAdvice(String resumeText, String jobDescription) {
        if (StringUtils.hasText(apiKey) && !apiKey.equals("${GROQ_API_KEY:}")) {
            return callGroqAPI(buildCareerAdvicePrompt(resumeText, jobDescription));
        }
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
            requestBody.put("model", "llama-3.3-70b-versatile");
            requestBody.put("max_tokens", 2000);
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
        boolean hasJD = StringUtils.hasText(jobDescription);
        
        return "As an expert Senior Technical Recruiter and Career Coach, perform a deep, multi-dimensional analysis of the following resume. " +
                "Provide highly detailed, professional, and actionable feedback.\n\n" +
                "RESUME CONTENT:\n" + resumeText + "\n\n" +
                (hasJD ? "TARGET JOB DESCRIPTION:\n" + jobDescription + "\n\n" : "") +
                "Please structure your response with the following sections:\n\n" +
                "## Executive Summary\n" +
                "Provide a sophisticated 4-5 sentence professional overview of the candidate's profile, unique value proposition, and overall marketability.\n\n" +
                (hasJD ? "## Job Description Match Analysis\n" +
                "- **Match Score:** [Provide a percentage score X/100]\n" +
                "- **Alignment Summary:** How well does this resume align with the specific requirements of the target role?\n" +
                "- **Critical Gaps:** Identify the top 3-5 specific skills, technologies, or experiences missing that are explicitly mentioned in the JD.\n" +
                "- **Tailoring Advice:** Give specific advice on which sections to emphasize to better fit this role.\n\n" : "") +
                "## Deep Technical Assessment\n" +
                "- Evaluate the depth and breadth of the technical stack mentioned.\n" +
                "- Analyze the complexity of the projects described.\n" +
                "- Identify specific industry-standard patterns or technologies that are present or conspicuously missing.\n\n" +
                "## Strengths (Highly Detailed)\n" +
                "- Provide at least 3-4 significant strengths, each supported by specific evidence found in the resume.\n" +
                "- Explain *why* these strengths are valuable in the current market.\n\n" +
                "## Areas for Improvement & Gap Analysis\n" +
                "- Identify specific weaknesses in content, phrasing, or technical focus.\n" +
                "- Point out missing keywords or certifications that would elevate the profile.\n" +
                "- Critically evaluate the 'Impact' - does the resume focus on responsibilities or results?\n\n" +
                "## Strategic Recommendations\n" +
                "1. Provide a specific recommendation for rewriting bullet points using the Google XYZ formula.\n" +
                "2. Suggest 2-3 specific certifications or courses based on the current profile.\n" +
                "3. Offer advice on how to better highlight leadership or soft skills.\n" +
                (hasJD ? "4. Provide 3 specific bullet point rewrites tailored directly to the target JD." : "4. Suggest how to better structure the project section for high impact.");
    }

    private String buildAtsPrompt(String resumeText, String jobDescription) {
        return "Act as an ATS (Applicant Tracking System) optimization expert. Analyze the following resume for maximum searchability and ranking potential.\n\n" +
                "RESUME:\n" + resumeText + "\n\n" +
                (StringUtils.hasText(jobDescription) ? "TARGET JOB DESCRIPTION:\n" + jobDescription + "\n\n" : "") +
                "## ATS Compatibility Score: [Provide a realistic score out of 100]\n\n" +
                "### Detailed Keyword Optimization\n" +
                "- **Hard Skills Found:** [List specifically]\n" +
                "- **Missing High-Priority Keywords:** [List keywords from the JD or industry that are missing]\n" +
                "- **Action Verb Evaluation:** Analyze the strength and variety of verbs used.\n\n" +
                "### Parseability & Formatting Audit\n" +
                "- Evaluate section headings for standard naming conventions.\n" +
                "- Identify any non-standard characters, tables, or columns that might break a parser.\n" +
                "- Check date formats and contact info structure.\n\n" +
                "### Ranking Strategy & Quick Wins\n" +
                "1. Identify the 'Top 5' keywords to add immediately.\n" +
                "2. Suggest a specific re-formatting of the skills section for better indexing.\n" +
                "3. Provide a 'Before/After' example of a poorly phrased bullet point.";
    }

    private String buildCareerAdvicePrompt(String resumeText, String jobDescription) {
        return "Act as an elite Career Strategy Consultant. Based on the following resume content, develop a comprehensive career growth roadmap.\n\n" +
                "RESUME CONTENT:\n" + resumeText + "\n\n" +
                (StringUtils.hasText(jobDescription) ? "DESIRED NEXT ROLE:\n" + jobDescription + "\n\n" : "") +
                "## Career Path & Market Positioning\n" +
                "- Analyze the candidate's current career trajectory.\n" +
                "- Determine if they are positioned for 'Lateral', 'Vertical', or 'Pivotal' growth.\n" +
                "- Assess their 'Career Narrative'—how well does the resume tell a story of growth?\n\n" +
                "## Strategic Skill Development Roadmap\n" +
                "### Immediate (0-6 months)\n" +
                "- Identify 2-3 specific technical skills to acquire or deepen.\n" +
                "- Suggest a high-impact project or certification.\n\n" +
                "### Mid-term (6-18 months)\n" +
                "- Recommend leadership or architectural focus areas.\n" +
                "- Suggest networking or industry engagement strategies.\n\n" +
                "## Interview & Narrative Strategy\n" +
                "- Identify the most impressive 'Achievement Story' in their resume.\n" +
                "- Provide a specific tip on how to handle potential weaknesses (e.g., gaps, short tenures) in an interview.\n" +
                "- Suggest a 'Personal Branding' statement they can use on LinkedIn.";
    }
}
