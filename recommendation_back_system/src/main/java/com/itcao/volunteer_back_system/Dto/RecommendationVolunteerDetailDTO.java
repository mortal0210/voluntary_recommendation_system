package com.itcao.volunteer_back_system.Dto;

/**
 * 推荐志愿详情 DTO
 */
public class RecommendationVolunteerDetailDTO {

    private String universityId;
    private String universityName;
    private String majorId;
    private String majorName;
    private Double lastYearScore;
    private Double scoreDifference;
    private Double admissionProbability;
    private Double recommendationRate;
    private String advantageAnalysis;
    private String riskAdvice;
    private String suggestion;

    public String getUniversityId() {
        return universityId;
    }

    public void setUniversityId(String universityId) {
        this.universityId = universityId;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public String getMajorId() {
        return majorId;
    }

    public void setMajorId(String majorId) {
        this.majorId = majorId;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public Double getLastYearScore() {
        return lastYearScore;
    }

    public void setLastYearScore(Double lastYearScore) {
        this.lastYearScore = lastYearScore;
    }

    public Double getScoreDifference() {
        return scoreDifference;
    }

    public void setScoreDifference(Double scoreDifference) {
        this.scoreDifference = scoreDifference;
    }

    public Double getAdmissionProbability() {
        return admissionProbability;
    }

    public void setAdmissionProbability(Double admissionProbability) {
        this.admissionProbability = admissionProbability;
    }

    public Double getRecommendationRate() {
        return recommendationRate;
    }

    public void setRecommendationRate(Double recommendationRate) {
        this.recommendationRate = recommendationRate;
    }

    public String getAdvantageAnalysis() {
        return advantageAnalysis;
    }

    public void setAdvantageAnalysis(String advantageAnalysis) {
        this.advantageAnalysis = advantageAnalysis;
    }

    public String getRiskAdvice() {
        return riskAdvice;
    }

    public void setRiskAdvice(String riskAdvice) {
        this.riskAdvice = riskAdvice;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}


