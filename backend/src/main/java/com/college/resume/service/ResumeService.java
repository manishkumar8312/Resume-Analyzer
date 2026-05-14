package com.college.resume.service;

import com.college.resume.model.Resume;
import com.college.resume.repository.ResumeRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private GroqService groqService;

    private static final List<String> COMMON_SKILLS = Arrays.asList(
        "Java", "Python", "JavaScript", "React", "Spring Boot", "Node.js", "SQL", "MongoDB",
        "Docker", "Kubernetes", "AWS", "Azure", "Git", "Jenkins", "Linux", "REST API",
        "Microservices", "HTML", "CSS", "TypeScript", "Angular", "Vue.js", "Express.js",
        "PostgreSQL", "MySQL", "Redis", "Elasticsearch", "GraphQL", "Kafka", "RabbitMQ",
        "Junit", "Maven", "Gradle", "CI/CD", "Agile", "Scrum", "Jira", "Confluence",
        "Machine Learning", "Data Science", "TensorFlow", "PyTorch", "Pandas", "NumPy",
        "Spring Security", "Hibernate", "JPA", "OAuth", "JWT", "Swagger", "Postman"
    );

    public Resume analyzeResume(MultipartFile file, String jobDescription) throws IOException {
        String text = extractTextFromPDF(file);
        
        List<String> detectedSkills = detectSkills(text);
        String experience = extractExperience(text);
        String education = extractEducation(text);
        
        int score = calculateScore(detectedSkills, experience, education);
        Integer matchPercentage = jobDescription != null && !jobDescription.isEmpty() 
            ? calculateJobMatch(text, jobDescription, detectedSkills) 
            : null;
        
        List<String> suggestions = generateSuggestions(detectedSkills, experience, education);

        Resume resume = new Resume(
            file.getOriginalFilename(),
            text,
            score,
            detectedSkills,
            experience,
            education,
            suggestions,
            matchPercentage
        );

        System.out.println("Starting AI review generation...");
        
        if (groqService != null) {
            try {
                // Run AI analysis concurrently to save time
                CompletableFuture<String> reviewFuture = CompletableFuture.supplyAsync(() -> 
                    groqService.analyzeResume(text, jobDescription));
                
                CompletableFuture<String> atsFuture = CompletableFuture.supplyAsync(() -> 
                    groqService.getAtsAnalysis(text, jobDescription));
                
                CompletableFuture<String> careerFuture = CompletableFuture.supplyAsync(() -> 
                    groqService.getCareerAdvice(text, jobDescription));

                // Wait for all to complete with a timeout (e.g., 30 seconds)
                CompletableFuture.allOf(reviewFuture, atsFuture, careerFuture)
                    .orTimeout(30, TimeUnit.SECONDS)
                    .join();

                String aiReview = reviewFuture.get();
                String atsAnalysis = atsFuture.get();
                String careerAdvice = careerFuture.get();

                if (aiReview != null) {
                    resume.setAiReview(aiReview);
                }
                if (atsAnalysis != null) {
                    resume.setAtsAnalysis(atsAnalysis);
                }
                if (careerAdvice != null) {
                    resume.setCareerAdvice(careerAdvice);
                }

                System.out.println("Concurrent AI analysis completed successfully");

            } catch (Exception e) {
                System.err.println("Error during concurrent AI analysis: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return resumeRepository.save(resume);
    }

    public List<Resume> getAnalysisHistory() {
        return resumeRepository.findAllByOrderByAnalyzedAtDesc();
    }

    public Resume getAnalysisById(Long id) {
        return resumeRepository.findById(id).orElse(null);
    }

    public void deleteResume(Long id) {
        resumeRepository.deleteById(id);
    }

    private String extractTextFromPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private List<String> detectSkills(String text) {
        List<String> detected = new ArrayList<>();
        String lowerText = text.toLowerCase();
        
        for (String skill : COMMON_SKILLS) {
            if (lowerText.contains(skill.toLowerCase())) {
                detected.add(skill);
            }
        }
        
        return detected;
    }

    private String extractExperience(String text) {
        Pattern pattern = Pattern.compile(
            "(?i)(experience|work history|employment|professional experience)" +
            "[:\\s]*(.+?)(?=education|skills|projects|achievements|$)",
            Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.find()) {
            String experience = matcher.group(2).trim();
            return experience.length() > 500 ? experience.substring(0, 500) + "..." : experience;
        }
        
        return "Experience information not clearly identified";
    }

    private String extractEducation(String text) {
        Pattern pattern = Pattern.compile(
            "(?i)(education|academic|qualifications)" +
            "[:\\s]*(.+?)(?=experience|skills|projects|achievements|$)",
            Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.find()) {
            String education = matcher.group(2).trim();
            return education.length() > 300 ? education.substring(0, 300) + "..." : education;
        }
        
        return "Education information not clearly identified";
    }

    private int calculateScore(List<String> skills, String experience, String education) {
        int score = 0;
        
        score += Math.min(skills.size() * 5, 40);
        
        if (!experience.contains("not clearly identified")) {
            score += 30;
        }
        
        if (!education.contains("not clearly identified")) {
            score += 30;
        }
        
        return Math.min(score, 100);
    }

    private Integer calculateJobMatch(String resumeText, String jobDescription, List<String> skills) {
        String lowerJobDesc = jobDescription.toLowerCase();
        String lowerResume = resumeText.toLowerCase();
        
        int matchScore = 0;
        int totalKeywords = 0;
        
        for (String skill : skills) {
            if (lowerJobDesc.contains(skill.toLowerCase())) {
                matchScore += 20;
            }
            totalKeywords++;
        }
        
        Pattern keywordPattern = Pattern.compile("\\b(java|python|javascript|react|spring|docker|aws|sql|agile|microservices)\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = keywordPattern.matcher(jobDescription);
        
        while (matcher.find()) {
            totalKeywords++;
            if (lowerResume.contains(matcher.group().toLowerCase())) {
                matchScore += 10;
            }
        }
        
        if (totalKeywords == 0) return 50;
        
        int percentage = (matchScore * 100) / (totalKeywords * 10);
        return Math.min(Math.max(percentage, 0), 100);
    }

    private List<String> generateSuggestions(List<String> skills, String experience, String education) {
        List<String> suggestions = new ArrayList<>();
        
        if (skills.size() < 5) {
            suggestions.add("Consider adding more technical skills to your resume");
        }
        
        if (experience.contains("not clearly identified")) {
            suggestions.add("Clearly label your work experience section");
        }
        
        if (education.contains("not clearly identified")) {
            suggestions.add("Ensure your education section is properly formatted");
        }
        
        if (skills.size() > 0 && !skills.contains("Git")) {
            suggestions.add("Consider adding version control tools like Git to your skills");
        }
        
        if (suggestions.isEmpty()) {
            suggestions.add("Your resume looks good! Consider quantifying your achievements with specific metrics");
        }
        
        return suggestions;
    }
}
