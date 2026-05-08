package com.college.resume.dto;

public class AnalysisRequest {
    private String jobDescription;

    public AnalysisRequest() {}

    public AnalysisRequest(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
}
