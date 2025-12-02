package com.itcao.volunteer_back_system.Dto;

/**
 * 智能推荐学生概要信息
 */
public class RecommendationStudentInfoDTO {
    private String studentId;
    private String studentName;
    private Double examScore;
    private Integer provinceRank;
    private Integer volunteerCount;
    private Integer admissionMatchCount;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Double getExamScore() {
        return examScore;
    }

    public void setExamScore(Double examScore) {
        this.examScore = examScore;
    }

    public Integer getProvinceRank() {
        return provinceRank;
    }

    public void setProvinceRank(Integer provinceRank) {
        this.provinceRank = provinceRank;
    }

    public Integer getVolunteerCount() {
        return volunteerCount;
    }

    public void setVolunteerCount(Integer volunteerCount) {
        this.volunteerCount = volunteerCount;
    }

    public Integer getAdmissionMatchCount() {
        return admissionMatchCount;
    }

    public void setAdmissionMatchCount(Integer admissionMatchCount) {
        this.admissionMatchCount = admissionMatchCount;
    }
}



