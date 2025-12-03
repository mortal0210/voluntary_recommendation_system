package com.itcao.volunteer_back_system.controller;

import com.itcao.volunteer_back_system.Dto.*;
import com.itcao.volunteer_back_system.common.Result;
import com.itcao.volunteer_back_system.service.RecommendationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RecommendationController 控制器层测试
 */
@WebMvcTest(RecommendationController.class)
@DisplayName("RecommendationController 控制器测试")
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    // ==================== 学生推荐信息测试 ====================
    @Nested
    @DisplayName("学生推荐信息接口测试")
    class StudentInfoTests {

        @Test
        @DisplayName("获取学生推荐信息成功")
        void getRecommendationStudentInfo_shouldReturnInfo() throws Exception {
            RecommendationStudentInfoDTO dto = new RecommendationStudentInfoDTO();
            dto.setStudentId("S001");
            dto.setStudentName("张三");
            dto.setExamScore(650.0);
            dto.setProvinceRank(1000);

            when(recommendationService.getRecommendationStudentInfo("S001"))
                    .thenReturn(Result.success(dto));

            mockMvc.perform(get("/api/recommendation/student/info")
                    .param("studentId", "S001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.studentId").value("S001"))
                    .andExpect(jsonPath("$.data.studentName").value("张三"));
        }

        @Test
        @DisplayName("获取学生推荐信息失败 - 学生不存在")
        void getRecommendationStudentInfo_shouldReturnError_whenNotFound() throws Exception {
            when(recommendationService.getRecommendationStudentInfo("S999"))
                    .thenReturn(Result.error(404, "未找到对应学生信息"));

            mockMvc.perform(get("/api/recommendation/student/info")
                    .param("studentId", "S999"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(404));
        }
    }

    // ==================== 志愿推荐测试 ====================
    @Nested
    @DisplayName("志愿推荐接口测试")
    class VolunteerRecommendationTests {

        @Test
        @DisplayName("获取志愿推荐成功")
        void getVolunteerRecommendation_shouldReturnRecommendations() throws Exception {
            RecommendationResult result = new RecommendationResult(650.0, new ArrayList<>());

            when(recommendationService.getVolunteerRecommendation("S001"))
                    .thenReturn(Result.success(result));

            mockMvc.perform(get("/api/recommendation/volunteer")
                    .param("studentId", "S001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.studentScore").value(650.0));
        }
    }

    // ==================== 志愿冲突检测测试 ====================
    @Nested
    @DisplayName("志愿冲突检测接口测试")
    class ConflictDetectionTests {

        @Test
        @DisplayName("获取志愿冲突检测结果成功")
        void getVolunteerConflict_shouldReturnConflicts() throws Exception {
            ConflictResult conflictResult = new ConflictResult(
                    1, 3, 2, new ArrayList<>(), new ArrayList<>());

            when(recommendationService.getVolunteerConflict("S001"))
                    .thenReturn(Result.success(conflictResult));

            mockMvc.perform(get("/api/recommendation/conflict")
                    .param("studentId", "S001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.studentExists").value(1));
        }
    }

    // ==================== 综合分析测试 ====================
    @Nested
    @DisplayName("综合分析接口测试")
    class ComprehensiveAnalysisTests {

        @Test
        @DisplayName("获取综合分析成功")
        void getComprehensiveAnalysis_shouldReturnAnalysis() throws Exception {
            ComprehensiveAnalysisResult analysisResult = new ComprehensiveAnalysisResult();
            analysisResult.setStudentName("张三");
            analysisResult.setScore(650);

            when(recommendationService.getComprehensiveAnalysis("S001"))
                    .thenReturn(Result.success(analysisResult));

            mockMvc.perform(get("/api/recommendation/analysis")
                    .param("studentId", "S001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.studentName").value("张三"));
        }
    }

    // ==================== 院校专业匹配测试 ====================
    @Nested
    @DisplayName("院校专业匹配接口测试")
    class UniversityMajorMatchTests {

        @Test
        @DisplayName("获取院校专业匹配成功")
        void getUniversityMajorMatch_shouldReturnMatch() throws Exception {
            UniversityMajorMatchResult matchResult = new UniversityMajorMatchResult();
            matchResult.setUniversityName("北京大学");
            matchResult.setMajorName("计算机科学");

            when(recommendationService.getUniversityMajorMatch("U001", "M001"))
                    .thenReturn(Result.success(matchResult));

            mockMvc.perform(get("/api/recommendation/match")
                    .param("universityId", "U001")
                    .param("majorId", "M001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.universityName").value("北京大学"));
        }
    }

    // ==================== 录取概率预测测试 ====================
    @Nested
    @DisplayName("录取概率预测接口测试")
    class AdmissionProbabilityTests {

        @Test
        @DisplayName("获取录取概率成功")
        void getAdmissionProbability_shouldReturnProbability() throws Exception {
            Map<String, Object> probabilityData = new HashMap<>();
            probabilityData.put("your_score", 650.0);
            probabilityData.put("admission_probability", 85.5);
            probabilityData.put("probability_level", "高概率");

            when(recommendationService.getAdmissionProbability("S001", "U001", "M001"))
                    .thenReturn(Result.success(probabilityData));

            mockMvc.perform(get("/api/recommendation/probability")
                    .param("studentId", "S001")
                    .param("universityId", "U001")
                    .param("majorId", "M001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.admission_probability").value(85.5));
        }
    }

    // ==================== 就业前景分析测试 ====================
    @Nested
    @DisplayName("就业前景分析接口测试")
    class EmploymentProspectsTests {

        @Test
        @DisplayName("获取就业前景成功")
        void getEmploymentProspects_shouldReturnProspects() throws Exception {
            Map<String, Object> employmentData = new HashMap<>();
            employmentData.put("average_employment_rate", 95.5);
            employmentData.put("average_salary", 15000);
            employmentData.put("employment_status", "就业率高");

            when(recommendationService.getEmploymentProspects("M001"))
                    .thenReturn(Result.success(employmentData));

            mockMvc.perform(get("/api/recommendation/employment")
                    .param("majorId", "M001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.employment_status").value("就业率高"));
        }
    }

    // ==================== 省份分数线分析测试 ====================
    @Nested
    @DisplayName("省份分数线分析接口测试")
    class ProvinceScoreTrendsTests {

        @Test
        @DisplayName("获取省份分数线趋势成功")
        void getProvinceScoreTrends_shouldReturnTrends() throws Exception {
            Map<String, Object> trendsData = new HashMap<>();
            trendsData.put("province_name", "北京");
            trendsData.put("year", 2024);
            trendsData.put("score", 600);

            when(recommendationService.getProvinceScoreTrends(11, 1))
                    .thenReturn(Result.success(trendsData));

            mockMvc.perform(get("/api/recommendation/score-trends")
                    .param("provinceId", "11")
                    .param("disciplineCategoryId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.province_name").value("北京"));
        }
    }

    // ==================== 学科门类列表测试 ====================
    @Nested
    @DisplayName("学科门类列表接口测试")
    class DisciplineCategoriesTests {

        @Test
        @DisplayName("获取学科门类列表成功")
        void getDisciplineCategories_shouldReturnList() throws Exception {
            List<Map<String, Object>> categories = new ArrayList<>();
            Map<String, Object> category = new HashMap<>();
            category.put("id", 1);
            category.put("name", "工学");
            categories.add(category);

            when(recommendationService.getDisciplineCategories())
                    .thenReturn(Result.success(categories));

            mockMvc.perform(get("/api/recommendation/disciplines"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    // ==================== 推荐志愿详情测试 ====================
    @Nested
    @DisplayName("推荐志愿详情接口测试")
    class VolunteerDetailTests {

        @Test
        @DisplayName("获取推荐志愿详情成功")
        void getRecommendationVolunteerDetail_shouldReturnDetail() throws Exception {
            RecommendationVolunteerDetailDTO detailDTO = new RecommendationVolunteerDetailDTO();
            detailDTO.setUniversityId("U001");
            detailDTO.setUniversityName("北京大学");
            detailDTO.setMajorId("M001");
            detailDTO.setMajorName("计算机科学");
            detailDTO.setAdmissionProbability(85.0);

            when(recommendationService.getRecommendationVolunteerDetail("S001", "U001", "M001"))
                    .thenReturn(Result.success(detailDTO));

            mockMvc.perform(get("/api/recommendation/volunteer/detail")
                    .param("studentId", "S001")
                    .param("universityId", "U001")
                    .param("majorId", "M001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.universityName").value("北京大学"))
                    .andExpect(jsonPath("$.data.majorName").value("计算机科学"));
        }
    }
}
