package com.itcao.volunteer_back_system.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 智能推荐模块集成测试
 * 
 * 测试推荐系统的完整流程，包括：
 * - 志愿推荐
 * - 冲突检测
 * - 综合分析
 * - 录取概率预测
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("智能推荐模块集成测试")
class RecommendationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // ==================== 志愿推荐集成测试 ====================
    @Nested
    @DisplayName("志愿推荐集成测试")
    class VolunteerRecommendationTests {

        @Test
        @DisplayName("获取志愿推荐 - 学生ID为空")
        void getVolunteerRecommendation_shouldReturnError_whenStudentIdBlank() throws Exception {
            mockMvc.perform(get("/api/recommendation/volunteer")
                    .param("studentId", "  "))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500));
        }

        @Test
        @DisplayName("获取志愿推荐 - 学生不存在")
        void getVolunteerRecommendation_shouldReturn404_whenStudentNotFound() throws Exception {
            mockMvc.perform(get("/api/recommendation/volunteer")
                    .param("studentId", "NOTEXIST999"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    // ==================== 学生信息集成测试 ====================
    @Nested
    @DisplayName("学生推荐信息集成测试")
    class StudentInfoTests {

        @Test
        @DisplayName("获取学生推荐信息 - 学生ID为空")
        void getRecommendationStudentInfo_shouldReturnError_whenStudentIdBlank() throws Exception {
            mockMvc.perform(get("/api/recommendation/student/info")
                    .param("studentId", ""))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    // ==================== 录取概率集成测试 ====================
    @Nested
    @DisplayName("录取概率预测集成测试")
    class AdmissionProbabilityTests {

        @Test
        @DisplayName("获取录取概率 - 参数为空")
        void getAdmissionProbability_shouldReturnError_whenParamsBlank() throws Exception {
            mockMvc.perform(get("/api/recommendation/probability")
                    .param("studentId", "")
                    .param("universityId", "U001")
                    .param("majorId", "M001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    // ==================== 就业前景集成测试 ====================
    @Nested
    @DisplayName("就业前景分析集成测试")
    class EmploymentProspectsTests {

        @Test
        @DisplayName("获取就业前景 - 专业ID为空")
        void getEmploymentProspects_shouldReturnError_whenMajorIdBlank() throws Exception {
            mockMvc.perform(get("/api/recommendation/employment")
                    .param("majorId", "  "))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    // ==================== 学科门类集成测试 ====================
    @Nested
    @DisplayName("学科门类列表集成测试")
    class DisciplineCategoriesTests {

        @Test
        @DisplayName("获取学科门类列表")
        void getDisciplineCategories_shouldReturnList() throws Exception {
            mockMvc.perform(get("/api/recommendation/disciplines"))
                    .andExpect(status().isOk());
        }
    }

    // ==================== 推荐详情集成测试 ====================
    @Nested
    @DisplayName("推荐志愿详情集成测试")
    class VolunteerDetailTests {

        @Test
        @DisplayName("获取推荐详情 - 参数为空")
        void getRecommendationVolunteerDetail_shouldReturnError_whenParamsBlank() throws Exception {
            mockMvc.perform(get("/api/recommendation/volunteer/detail")
                    .param("studentId", "S001")
                    .param("universityId", "")
                    .param("majorId", "M001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }
}
