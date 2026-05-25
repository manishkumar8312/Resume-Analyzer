package com.resumeanalyzer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "resumes")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private Integer score;

    @Column
    private Integer matchPercentage;

    @ElementCollection
    @CollectionTable(name = "resume_skills", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "skill")
    private List<String> skills;

    @Column(columnDefinition = "TEXT")
    private String experience;

    @Column(columnDefinition = "TEXT")
    private String education;

    @ElementCollection
    @CollectionTable(name = "resume_suggestions", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "suggestion")
    private List<String> suggestions;

    @Column(columnDefinition = "TEXT")
    private String aiReview;

    @Column(columnDefinition = "TEXT")
    private String atsAnalysis;

    @Column(columnDefinition = "TEXT")
    private String careerAdvice;

    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt;

    public Resume() {
    }

    public Resume(String fileName, String content, Integer score, List<String> skills, 
                  String experience, String education, List<String> suggestions, 
                  Integer matchPercentage) {
        this.fileName = fileName;
        this.content = content;
        this.score = score;
        this.skills = skills;
        this.experience = experience;
        this.education = education;
        this.suggestions = suggestions;
        this.matchPercentage = matchPercentage;
        this.analyzedAt = LocalDateTime.now();
    }

    public String getAiReview() {
        return aiReview;
    }

    public void setAiReview(String aiReview) {
        this.aiReview = aiReview;
    }

    public String getAtsAnalysis() {
        return atsAnalysis;
    }

    public void setAtsAnalysis(String atsAnalysis) {
        this.atsAnalysis = atsAnalysis;
    }

    public String getCareerAdvice() {
        return careerAdvice;
    }

    public void setCareerAdvice(String careerAdvice) {
        this.careerAdvice = careerAdvice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(Integer matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
}
