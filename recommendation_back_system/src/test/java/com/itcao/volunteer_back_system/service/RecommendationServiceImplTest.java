package com.itcao.volunteer_back_system.service;

import com.itcao.volunteer_back_system.Dto.*;
import com.itcao.volunteer_back_system.common.Result;
import com.itcao.volunteer_back_system.mapper.RecommendationMapper;
import com.itcao.volunteer_back_system.mapper.StudentMapper;
import com.itcao.volunteer_back_system.mapper.VolunteerRelationMapper;
import com.itcao.volunteer_back_system.pojo.Student;
import com.itcao.volunteer_back_system.service.impl.RecommendationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * RecommendationServiceImpl 单元测试
 * 
 * 设计思路：
 * - 不启动 Spring 容器，仅使用 JUnit5 + Mockito 进行纯单元测试
 * - 对外部依赖（Mapper）全部使用 Mock
 * - 测试方法命名采用「方法名_场景_期望结果」风格
 * - 使用 @Nested 按功能模块组织测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RecommendationServiceImpl 单元测试")
class RecommendationServiceImplTest {

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private VolunteerRelationMapper volunteerRelationMapper;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setStudentId("S001");
        testStudent.setStudentName("张三");
        testStudent.setCollegeEntranceExamScore(650);
        testStudent.setRanking(12345);
        testStudent.setProvinceId(11);
    }

    // ==================== getVolunteerRecommendation：智能志愿推荐 ====================
    @Nested
    @DisplayName("智能志愿推荐测试")
    class GetVolunteerRecommendationTests {

        @Test
        @DisplayName("studentId 为 null 时返回错误")
        void shouldReturnError_whenStudentIdNull() {
            Result<RecommendationResult> result = recommendationService.getVolunteerRecommendation(null);

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("学生ID不能为空"));
        }

        @Test
        @DisplayName("studentId 为空白字符串时返回错误")
        void shouldReturnError_whenStudentIdBlank() {
            Result<RecommendationResult> result = recommendationService.getVolunteerRecommendation("  ");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("学生ID不能为空"));
        }

        @Test
        @DisplayName("学生不存在时返回 404")
        void shouldReturn404_whenStudentNotFound() {
            when(studentMapper.getStudentById("S001")).thenReturn(null);

            Result<RecommendationResult> result = recommendationService.getVolunteerRecommendation("S001");

            assertEquals(404, result.getCode());
            assertTrue(result.getMessage().contains("未找到对应学生信息"));
        }

        @Test
        @DisplayName("推荐结果为空时返回空列表")
        void shouldReturnEmptyList_whenNoResults() {
            when(studentMapper.getStudentById("S001")).thenReturn(testStudent);
            when(recommendationMapper.queryFlexibleRecommendations("S001")).thenReturn(new ArrayList<>());

            Result<RecommendationResult> result = recommendationService.getVolunteerRecommendation("S001");

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(650.0, result.getData().getStudentScore());
            assertEquals(0, result.getData().getRecommendations().size());
        }

        @Test
        @DisplayName("推荐结果为 null 时返回空列表")
        void shouldReturnEmptyList_whenResultsNull() {
            when(studentMapper.getStudentById("S001")).thenReturn(testStudent);
            when(recommendationMapper.queryFlexibleRecommendations("S001")).thenReturn(null);

            Result<RecommendationResult> result = recommendationService.getVolunteerRecommendation("S001");

            assertEquals(200, result.getCode());
            assertEquals(0, result.getData().getRecommendations().size());
        }

        @Test
        @DisplayName("正常返回推荐列表并计算录取概率")
        void shouldReturnRecommendations_withProbability() {
            when(studentMapper.getStudentById("S001")).thenReturn(testStudent);

            List<Map<String, Object>> mockResults = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("university_name", "北京大学");
            row.put("university_id", "U001");
            row.put("major_name", "计算机科学");
            row.put("major_id", "M001");
            row.put("last_year_admission", 640);
            row.put("score_difference", 10);
            mockResults.add(row);

            when(recommendationMapper.queryFlexibleRecommendations("S001")).thenReturn(mockResults);

            Map<String, Object> stats = new HashMap<>();
            stats.put("avgScore", 640);
            stats.put("stdScore", 5.0);
            stats.put("dataCount", 100);
            when(recommendationMapper.selectAdmissionStats("U001", "M001")).thenReturn(stats);

            Result<RecommendationResult> result = recommendationService.getVolunteerRecommendation("S001");

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getRecommendations().size());
            assertEquals("北京大学", result.getData().getRecommendations().get(0).getUniversityName());
            assertNotNull(result.getData().getRecommendations().get(0).getAdmissionProbability());
        }

        @Test
        @DisplayName("无统计数据时使用去年分数线作为参考")
        void shouldUseFallbackScore_whenNoStats() {
            when(studentMapper.getStudentById("S001")).thenReturn(testStudent);

            List<Map<String, Object>> mockResults = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("university_name", "清华大学");
            row.put("university_id", "U002");
            row.put("major_name", "软件工程");
            row.put("major_id", "M002");
            row.put("last_year_admission", 660);
            row.put("score_difference", -10);
            mockResults.add(row);

            when(recommendationMapper.queryFlexibleRecommendations("S001")).thenReturn(mockResults);
            when(recommendationMapper.selectAdmissionStats("U002", "M002")).thenReturn(null);

            Result<RecommendationResult> result = recommendationService.getVolunteerRecommendation("S001");

            assertEquals(200, result.getCode());
            assertEquals(1, result.getData().getRecommendations().size());
        }

        @Test
        @DisplayName("studentId 带空格时应自动 trim")
        void shouldTrimStudentId() {
            when(studentMapper.getStudentById("S001")).thenReturn(testStudent);
            when(recommendationMapper.queryFlexibleRecommendations("S001")).thenReturn(new ArrayList<>());

            Result<RecommendationResult> result = recommendationService.getVolunteerRecommendation("  S001  ");

            assertEquals(200, result.getCode());
            verify(studentMapper).getStudentById("S001");
        }
    }

    // ==================== getAdmissionProbability：录取概率预测 ====================
    @Nested
    @DisplayName("录取概率预测测试")
    class GetAdmissionProbabilityTests {

        @Test
        @DisplayName("studentId 为 null 时返回错误")
        void shouldReturnError_whenStudentIdNull() {
            Result<Map<String, Object>> result = recommendationService.getAdmissionProbability(null, "U001", "M001");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("不能为空"));
        }

        @Test
        @DisplayName("universityId 为空白时返回错误")
        void shouldReturnError_whenUniversityIdBlank() {
            Result<Map<String, Object>> result = recommendationService.getAdmissionProbability("S001", "  ", "M001");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("不能为空"));
        }

        @Test
        @DisplayName("majorId 为空白时返回错误")
        void shouldReturnError_whenMajorIdBlank() {
            Result<Map<String, Object>> result = recommendationService.getAdmissionProbability("S001", "U001", "");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("不能为空"));
        }

        @Test
        @DisplayName("学生不存在时返回 404")
        void shouldReturn404_whenStudentNotFound() {
            when(studentMapper.getStudentById("S001")).thenReturn(null);

            Result<Map<String, Object>> result = recommendationService.getAdmissionProbability("S001", "U001", "M001");

            assertEquals(404, result.getCode());
            assertTrue(result.getMessage().contains("未找到对应学生信息"));
        }

        @Test
        @DisplayName("正常情况 - 使用历史统计数据计算概率")
        void shouldUseHistoryStats_whenAvailable() {
            when(studentMapper.getStudentById("S001")).thenReturn(testStudent);

            Map<String, Object> stats = new HashMap<>();
            stats.put("avgScore", 640);
            stats.put("stdScore", 5.0);
            stats.put("dataCount", 100);
            when(recommendationMapper.selectAdmissionStats("U001", "M001")).thenReturn(stats);

            Result<Map<String, Object>> result = recommendationService.getAdmissionProbability("S001", "U001", "M001");

            assertEquals(200, result.getCode());
            Map<String, Object> data = result.getData();
            assertNotNull(data);
            assertEquals(650.0, (double) data.get("your_score"));
            assertEquals(640.0, (double) data.get("avg_admission_score"));
            assertEquals(100, data.get("historical_data_count"));

            Double probability = (Double) data.get("admission_probability");
            assertNotNull(probability);
            assertTrue(probability >= 0.0 && probability <= 100.0);
        }

        @Test
        @DisplayName("无统计数据时使用去年分数线")
        void shouldFallbackToLastYearScore_whenNoStats() {
            Student lowScoreStudent = new Student();
            lowScoreStudent.setStudentId("S001");
            lowScoreStudent.setCollegeEntranceExamScore(620);
            when(studentMapper.getStudentById("S001")).thenReturn(lowScoreStudent);

            when(recommendationMapper.selectAdmissionStats("U001", "M001")).thenReturn(null);

            Map<String, Object> detail = new HashMap<>();
            detail.put("last_year_admission", 610);
            when(recommendationMapper.selectRecommendationDetail("S001", "U001", "M001")).thenReturn(detail);

            Result<Map<String, Object>> result = recommendationService.getAdmissionProbability("S001", "U001", "M001");

            assertEquals(200, result.getCode());
            assertEquals(610.0, (double) result.getData().get("avg_admission_score"));
        }

        @Test
        @DisplayName("无任何参考数据时使用默认分数 600")
        void shouldUseDefaultScore_whenNoDataAvailable() {
            when(studentMapper.getStudentById("S001")).thenReturn(testStudent);
            when(recommendationMapper.selectAdmissionStats("U001", "M001")).thenReturn(null);
            when(recommendationMapper.selectRecommendationDetail("S001", "U001", "M001")).thenReturn(null);

            Result<Map<String, Object>> result = recommendationService.getAdmissionProbability("S001", "U001", "M001");

            assertEquals(200, result.getCode());
            assertEquals(600.0, (double) result.getData().get("avg_admission_score"));
        }

        @Test
        @DisplayName("概率等级 - 高概率 (>=85)")
        void shouldReturnHighProbabilityLevel() {
            Student highScoreStudent = new Student();
            highScoreStudent.setStudentId("S001");
            highScoreStudent.setCollegeEntranceExamScore(700);
            when(studentMapper.getStudentById("S001")).thenReturn(highScoreStudent);

            Map<String, Object> stats = new HashMap<>();
            stats.put("avgScore", 640);
            stats.put("stdScore", 5.0);
            stats.put("dataCount", 100);
            when(recommendationMapper.selectAdmissionStats("U001", "M001")).thenReturn(stats);

            Result<Map<String, Object>> result = recommendationService.getAdmissionProbability("S001", "U001", "M001");

            assertEquals("高概率", result.getData().get("probability_level"));
        }

        @Test
        @DisplayName("概率等级 - 极小概率 (<35)")
        void shouldReturnVeryLowProbabilityLevel() {
            Student lowScoreStudent = new Student();
            lowScoreStudent.setStudentId("S001");
            lowScoreStudent.setCollegeEntranceExamScore(550);
            when(studentMapper.getStudentById("S001")).thenReturn(lowScoreStudent);

            Map<String, Object> stats = new HashMap<>();
            stats.put("avgScore", 640);
            stats.put("stdScore", 5.0);
            stats.put("dataCount", 100);
            when(recommendationMapper.selectAdmissionStats("U001", "M001")).thenReturn(stats);

            Result<Map<String, Object>> result = recommendationService.getAdmissionProbability("S001", "U001", "M001");

            assertEquals("极小概率", result.getData().get("probability_level"));
        }
    }

    // ==================== getRecommendationStudentInfo：学生推荐信息概要 ====================
    @Nested
    @DisplayName("学生推荐信息概要测试")
    class GetRecommendationStudentInfoTests {

        @Test
        @DisplayName("studentId 为 null 时返回错误")
        void shouldReturnError_whenStudentIdNull() {
            Result<RecommendationStudentInfoDTO> result = recommendationService.getRecommendationStudentInfo(null);

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("学生ID不能为空"));
        }

        @Test
        @DisplayName("studentId 为空白时返回错误")
        void shouldReturnError_whenStudentIdBlank() {
            Result<RecommendationStudentInfoDTO> result = recommendationService.getRecommendationStudentInfo("  ");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("学生ID不能为空"));
        }

        @Test
        @DisplayName("学生不存在时返回 404")
        void shouldReturn404_whenStudentNotFound() {
            when(studentMapper.getStudentById("S001")).thenReturn(null);

            Result<RecommendationStudentInfoDTO> result = recommendationService.getRecommendationStudentInfo("S001");

            assertEquals(404, result.getCode());
            assertTrue(result.getMessage().contains("未找到对应学生信息"));
        }

        @Test
        @DisplayName("正常返回学生信息 DTO")
        void shouldReturnDto_whenStudentExists() {
            when(studentMapper.getStudentById("S001")).thenReturn(testStudent);
            when(volunteerRelationMapper.countByStudentId("S001")).thenReturn(5);
            when(recommendationMapper.countAdmissionMatches("S001")).thenReturn(2);

            Result<RecommendationStudentInfoDTO> result = recommendationService.getRecommendationStudentInfo("S001");

            assertEquals(200, result.getCode());
            RecommendationStudentInfoDTO dto = result.getData();
            assertNotNull(dto);
            assertEquals("S001", dto.getStudentId());
            assertEquals("张三", dto.getStudentName());
            assertEquals(650.0, dto.getExamScore());
            assertEquals(12345, dto.getProvinceRank());
            assertEquals(5, dto.getVolunteerCount());
            assertEquals(2, dto.getAdmissionMatchCount());
        }

        @Test
        @DisplayName("录取匹配数为 null 时返回 0")
        void shouldReturnZero_whenAdmissionMatchCountNull() {
            when(studentMapper.getStudentById("S001")).thenReturn(testStudent);
            when(volunteerRelationMapper.countByStudentId("S001")).thenReturn(3);
            when(recommendationMapper.countAdmissionMatches("S001")).thenReturn(null);

            Result<RecommendationStudentInfoDTO> result = recommendationService.getRecommendationStudentInfo("S001");

            assertEquals(200, result.getCode());
            assertEquals(0, result.getData().getAdmissionMatchCount());
        }
    }

    // ==================== getVolunteerConflict：志愿冲突检测 ====================
    @Nested
    @DisplayName("志愿冲突检测测试")
    class GetVolunteerConflictTests {

        @Test
        @DisplayName("结果为 null 时返回错误")
        void shouldReturnError_whenResultsNull() {
            when(recommendationMapper.callCheckVolunteerConflict("S001")).thenReturn(null);

            Result<ConflictResult> result = recommendationService.getVolunteerConflict("S001");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("未找到志愿冲突检测结果"));
        }

        @Test
        @DisplayName("结果为空列表时返回错误")
        void shouldReturnError_whenResultsEmpty() {
            when(recommendationMapper.callCheckVolunteerConflict("S001")).thenReturn(new ArrayList<>());

            Result<ConflictResult> result = recommendationService.getVolunteerConflict("S001");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("未找到志愿冲突检测结果"));
        }

        @Test
        @DisplayName("正常解析调试信息、志愿列表和冲突列表")
        void shouldParseResultCorrectly() {
            List<Map<String, Object>> rows = new ArrayList<>();

            // debug 信息
            Map<String, Object> debugRow1 = new HashMap<>();
            debugRow1.put("debug_info", "学生存在: 1");
            rows.add(debugRow1);

            Map<String, Object> debugRow2 = new HashMap<>();
            debugRow2.put("debug_info", "志愿数量: 3");
            rows.add(debugRow2);

            Map<String, Object> debugRow3 = new HashMap<>();
            debugRow3.put("debug_info", "匹配的历史录取数据: 5");
            rows.add(debugRow3);

            // 志愿信息
            Map<String, Object> volunteerRow = new HashMap<>();
            volunteerRow.put("volunteer_order", 1);
            volunteerRow.put("university_name", "大学A");
            volunteerRow.put("major_name", "专业A");
            volunteerRow.put("year", 2024);
            volunteerRow.put("admission_number", 100);
            rows.add(volunteerRow);

            // 冲突信息
            Map<String, Object> conflictRow = new HashMap<>();
            conflictRow.put("order1", 1);
            conflictRow.put("uni1", "大学A");
            conflictRow.put("major1", "专业A");
            conflictRow.put("score1", 650);
            conflictRow.put("order2", 2);
            conflictRow.put("uni2", "大学B");
            conflictRow.put("major2", "专业B");
            conflictRow.put("score2", 640);
            conflictRow.put("advice", "建议调整顺序");
            rows.add(conflictRow);

            when(recommendationMapper.callCheckVolunteerConflict("S001")).thenReturn(rows);

            Result<ConflictResult> result = recommendationService.getVolunteerConflict("S001");

            assertEquals(200, result.getCode());
            ConflictResult data = result.getData();
            assertNotNull(data);
            assertEquals(1, data.getStudentExists());
            assertEquals(3, data.getVolunteerCount());
            assertEquals(5, data.getAdmissionCount());
            assertEquals(1, data.getVolunteers().size());
            assertEquals(1, data.getConflicts().size());
            assertEquals("建议调整顺序", data.getConflicts().get(0).getAdvice());
        }

        @Test
        @DisplayName("无冲突时返回空冲突列表")
        void shouldReturnEmptyConflicts_whenNoConflicts() {
            List<Map<String, Object>> rows = new ArrayList<>();

            Map<String, Object> debugRow = new HashMap<>();
            debugRow.put("debug_info", "学生存在: 1");
            rows.add(debugRow);

            Map<String, Object> volunteerRow = new HashMap<>();
            volunteerRow.put("volunteer_order", 1);
            volunteerRow.put("university_name", "大学A");
            volunteerRow.put("major_name", "专业A");
            rows.add(volunteerRow);

            when(recommendationMapper.callCheckVolunteerConflict("S001")).thenReturn(rows);

            Result<ConflictResult> result = recommendationService.getVolunteerConflict("S001");

            assertEquals(200, result.getCode());
            assertEquals(0, result.getData().getConflicts().size());
        }
    }

    // ==================== getComprehensiveAnalysis：志愿综合分析 ====================
    @Nested
    @DisplayName("志愿综合分析测试")
    class GetComprehensiveAnalysisTests {

        @Test
        @DisplayName("结果为 null 时返回错误")
        void shouldReturnError_whenResultsNull() {
            when(recommendationMapper.callAnalyzeStudentVolunteerStrategy("S001")).thenReturn(null);

            Result<ComprehensiveAnalysisResult> result = recommendationService.getComprehensiveAnalysis("S001");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("未找到志愿综合分析结果"));
        }

        @Test
        @DisplayName("结果为空列表时返回错误")
        void shouldReturnError_whenResultsEmpty() {
            when(recommendationMapper.callAnalyzeStudentVolunteerStrategy("S001")).thenReturn(new ArrayList<>());

            Result<ComprehensiveAnalysisResult> result = recommendationService.getComprehensiveAnalysis("S001");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("未找到志愿综合分析结果"));
        }

        @Test
        @DisplayName("正常解析综合分析结果")
        void shouldParseResultCorrectly() {
            List<Map<String, Object>> rows = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("student_name", "张三");
            row.put("score", 630);
            row.put("rank", 12345);
            row.put("volunteer_count", 3);
            row.put("overall_rating", "良好");
            row.put("rationality_score", 85.5);
            row.put("safety_score", 88.0);
            row.put("gradient_score", 80.0);
            row.put("major_match_score", 90.0);
            row.put("risk_analysis", "风险适中");
            row.put("gradient_analysis", "梯度合理");
            row.put("major_match_analysis", "专业匹配度高");
            row.put("overall_suggestion", "整体方案可行");
            row.put("high", 1);
            row.put("medium", 1);
            row.put("low", 1);
            row.put("order", 1);
            row.put("university_name", "大学A");
            row.put("major_name", "专业A");
            row.put("last_year_score", 620);
            row.put("score_difference", 10);
            row.put("admission_probability", 80.0);
            row.put("risk_level", "中");
            rows.add(row);

            when(recommendationMapper.callAnalyzeStudentVolunteerStrategy("S001")).thenReturn(rows);

            Result<ComprehensiveAnalysisResult> result = recommendationService.getComprehensiveAnalysis("S001");

            assertEquals(200, result.getCode());
            ComprehensiveAnalysisResult data = result.getData();
            assertNotNull(data);
            assertEquals("张三", data.getStudentName());
            assertEquals(630, data.getScore());
            assertEquals(12345, data.getRank());
            assertEquals(3, data.getVolunteerCount());
            assertEquals("良好", data.getOverallRating());
            assertEquals(85.5, data.getRationalityScore());
            assertEquals(1, data.getVolunteers().size());
            assertEquals(1, data.getRiskDistribution().get("high"));
            assertEquals(1, data.getRiskDistribution().get("medium"));
            assertEquals(1, data.getRiskDistribution().get("low"));
        }

        @Test
        @DisplayName("多行志愿数据正确解析")
        void shouldParseMultipleVolunteers() {
            List<Map<String, Object>> rows = new ArrayList<>();

            for (int i = 1; i <= 3; i++) {
                Map<String, Object> row = new HashMap<>();
                row.put("student_name", "张三");
                row.put("score", 630);
                row.put("order", i);
                row.put("university_name", "大学" + i);
                row.put("major_name", "专业" + i);
                rows.add(row);
            }

            when(recommendationMapper.callAnalyzeStudentVolunteerStrategy("S001")).thenReturn(rows);

            Result<ComprehensiveAnalysisResult> result = recommendationService.getComprehensiveAnalysis("S001");

            assertEquals(200, result.getCode());
            assertEquals(3, result.getData().getVolunteers().size());
        }
    }

    // ==================== getUniversityMajorMatch：院校专业匹配 ====================
    @Nested
    @DisplayName("院校专业匹配测试")
    class GetUniversityMajorMatchTests {

        @Test
        @DisplayName("结果为 null 时返回错误")
        void shouldReturnError_whenResultsNull() {
            when(recommendationMapper.callAnalyzeUniversityMajorMatch("U001", "M001")).thenReturn(null);

            Result<UniversityMajorMatchResult> result = recommendationService.getUniversityMajorMatch("U001", "M001");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("未找到院校专业匹配结果"));
        }

        @Test
        @DisplayName("结果为空列表时返回错误")
        void shouldReturnError_whenResultsEmpty() {
            when(recommendationMapper.callAnalyzeUniversityMajorMatch("U001", "M001")).thenReturn(new ArrayList<>());

            Result<UniversityMajorMatchResult> result = recommendationService.getUniversityMajorMatch("U001", "M001");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("未找到院校专业匹配结果"));
        }

        @Test
        @DisplayName("正常解析第一行结果")
        void shouldParseFirstRow() {
            List<Map<String, Object>> rows = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("university_name", "北京大学");
            row.put("university_id", "U001");
            row.put("university_level", "双一流");
            row.put("major_name", "计算机科学");
            row.put("major_id", "M001");
            row.put("major_category", "工学");
            row.put("location", "北京");
            row.put("admission_score", 650);
            row.put("score_difference", 10);
            row.put("match_rate", 90);
            rows.add(row);

            when(recommendationMapper.callAnalyzeUniversityMajorMatch("U001", "M001")).thenReturn(rows);

            Result<UniversityMajorMatchResult> result = recommendationService.getUniversityMajorMatch("U001", "M001");

            assertEquals(200, result.getCode());
            UniversityMajorMatchResult data = result.getData();
            assertNotNull(data);
            assertEquals("北京大学", data.getUniversityName());
            assertEquals("U001", data.getUniversityId());
            assertEquals("双一流", data.getUniversityLevel());
            assertEquals("计算机科学", data.getMajorName());
            assertEquals("M001", data.getMajorId());
            assertEquals("工学", data.getMajorCategory());
            assertEquals("北京", data.getLocation());
            assertEquals(650, data.getAdmissionScore());
            assertEquals(10, data.getScoreDifference());
            assertEquals(90, data.getMatchRate());
        }
    }

    // ==================== getEmploymentProspects：专业就业前景分析 ====================
    @Nested
    @DisplayName("专业就业前景分析测试")
    class GetEmploymentProspectsTests {

        @Test
        @DisplayName("majorId 为空时返回错误")
        void shouldReturnError_whenMajorIdBlank() {
            Result<Map<String, Object>> result = recommendationService.getEmploymentProspects("  ");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("专业ID不能为空"));
        }

        @Test
        @DisplayName("基础数据为空时返回 404")
        void shouldReturn404_whenBaseInfoMissing() {
            when(recommendationMapper.selectMajorEmploymentBase("M001")).thenReturn(null);

            Result<Map<String, Object>> result = recommendationService.getEmploymentProspects("M001");

            assertEquals(404, result.getCode());
            assertTrue(result.getMessage().contains("未找到该专业信息"));
        }

        @Test
        @DisplayName("工学类别正常计算就业率与薪资")
        void shouldCalculateForEngineeringCategory() {
            Map<String, Object> base = new HashMap<>();
            base.put("discipline_category", "工学");
            base.put("line_score", 650);
            base.put("volunteer_count", 25);
            when(recommendationMapper.selectMajorEmploymentBase("M001")).thenReturn(base);

            Result<Map<String, Object>> result = recommendationService.getEmploymentProspects("M001");

            assertEquals(200, result.getCode());
            Map<String, Object> data = result.getData();
            assertNotNull(data);
            assertTrue(data.containsKey("average_employment_rate"));
            assertTrue(data.containsKey("average_salary"));
            assertEquals("就业率高", data.get("employment_status"));
            assertNotNull(data.get("industry_outlook"));
            assertNotNull(data.get("demand_level"));
            assertNotNull(data.get("career_advice"));
        }

        @Test
        @DisplayName("医学类别正常计算")
        void shouldCalculateForMedicalCategory() {
            Map<String, Object> base = new HashMap<>();
            base.put("discipline_category", "医学");
            base.put("line_score", 620);
            base.put("volunteer_count", 15);
            when(recommendationMapper.selectMajorEmploymentBase("M002")).thenReturn(base);

            Result<Map<String, Object>> result = recommendationService.getEmploymentProspects("M002");

            assertEquals(200, result.getCode());
            assertTrue(result.getData().get("industry_outlook").toString().contains("医疗"));
        }

        @Test
        @DisplayName("管理类别正常计算")
        void shouldCalculateForManagementCategory() {
            Map<String, Object> base = new HashMap<>();
            base.put("discipline_category", "管理学");
            base.put("line_score", 580);
            base.put("volunteer_count", 10);
            when(recommendationMapper.selectMajorEmploymentBase("M003")).thenReturn(base);

            Result<Map<String, Object>> result = recommendationService.getEmploymentProspects("M003");

            assertEquals(200, result.getCode());
            assertTrue(result.getData().get("industry_outlook").toString().contains("管理"));
        }

        @Test
        @DisplayName("文学类别正常计算")
        void shouldCalculateForLiteratureCategory() {
            Map<String, Object> base = new HashMap<>();
            base.put("discipline_category", "文学");
            base.put("line_score", 550);
            base.put("volunteer_count", 8);
            when(recommendationMapper.selectMajorEmploymentBase("M004")).thenReturn(base);

            Result<Map<String, Object>> result = recommendationService.getEmploymentProspects("M004");

            assertEquals(200, result.getCode());
            assertTrue(result.getData().get("industry_outlook").toString().contains("文化"));
        }

        @Test
        @DisplayName("其他类别使用默认值")
        void shouldUseDefaultForOtherCategory() {
            Map<String, Object> base = new HashMap<>();
            base.put("discipline_category", "艺术学");
            base.put("line_score", 500);
            base.put("volunteer_count", 5);
            when(recommendationMapper.selectMajorEmploymentBase("M005")).thenReturn(base);

            Result<Map<String, Object>> result = recommendationService.getEmploymentProspects("M005");

            assertEquals(200, result.getCode());
            assertNotNull(result.getData().get("average_employment_rate"));
        }
    }

    // ==================== getProvinceScoreTrends：省份分数线分析 ====================
    @Nested
    @DisplayName("省份分数线分析测试")
    class GetProvinceScoreTrendsTests {

        @Test
        @DisplayName("无结果时返回错误")
        void shouldReturnError_whenNoResults() {
            when(recommendationMapper.callAnalyzeProvinceScoreTrends(11, 1)).thenReturn(new ArrayList<>());

            Result<Map<String, Object>> result = recommendationService.getProvinceScoreTrends(11, 1);

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("未找到省份分数线分析结果"));
        }

        @Test
        @DisplayName("结果为 null 时返回错误")
        void shouldReturnError_whenResultsNull() {
            when(recommendationMapper.callAnalyzeProvinceScoreTrends(11, 1)).thenReturn(null);

            Result<Map<String, Object>> result = recommendationService.getProvinceScoreTrends(11, 1);

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("未找到省份分数线分析结果"));
        }

        @Test
        @DisplayName("message 以'错误:'开头时返回该错误信息")
        void shouldReturnError_whenMessageIndicatesError() {
            List<Map<String, Object>> rows = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("message", "错误: 参数不合法");
            rows.add(row);

            when(recommendationMapper.callAnalyzeProvinceScoreTrends(11, 1)).thenReturn(rows);

            Result<Map<String, Object>> result = recommendationService.getProvinceScoreTrends(11, 1);

            assertEquals(500, result.getCode());
            assertEquals("错误: 参数不合法", result.getMessage());
        }

        @Test
        @DisplayName("正常返回时包裹第一行结果")
        void shouldWrapFirstRowOnSuccess() {
            List<Map<String, Object>> rows = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("province_name", "北京");
            row.put("year", 2024);
            row.put("score", 600);
            rows.add(row);

            when(recommendationMapper.callAnalyzeProvinceScoreTrends(11, 1)).thenReturn(rows);

            Result<Map<String, Object>> result = recommendationService.getProvinceScoreTrends(11, 1);

            assertEquals(200, result.getCode());
            Map<String, Object> data = result.getData();
            assertNotNull(data);
            assertEquals("北京", data.get("province_name"));
            assertEquals(2024, data.get("year"));
            assertEquals(600, data.get("score"));
        }
    }

    // ==================== getDisciplineCategories：学科门类列表 ====================
    @Nested
    @DisplayName("学科门类列表测试")
    class GetDisciplineCategoriesTests {

        @Test
        @DisplayName("结果为空时返回错误")
        void shouldReturnError_whenNoData() {
            when(recommendationMapper.getDisciplineCategories()).thenReturn(new ArrayList<>());

            Result<Object> result = recommendationService.getDisciplineCategories();

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("未找到学科门类数据"));
        }

        @Test
        @DisplayName("结果为 null 时返回错误")
        void shouldReturnError_whenResultsNull() {
            when(recommendationMapper.getDisciplineCategories()).thenReturn(null);

            Result<Object> result = recommendationService.getDisciplineCategories();

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("未找到学科门类数据"));
        }

        @Test
        @DisplayName("正常情况返回列表")
        void shouldReturnListOnSuccess() {
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("id", 1);
            row.put("name", "工学");
            list.add(row);
            when(recommendationMapper.getDisciplineCategories()).thenReturn(list);

            Result<Object> result = recommendationService.getDisciplineCategories();

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
        }
    }

    // ==================== getRecommendationVolunteerDetail：推荐志愿详情 ====================
    @Nested
    @DisplayName("推荐志愿详情测试")
    class GetRecommendationVolunteerDetailTests {

        @Test
        @DisplayName("任一参数为空时返回错误")
        void shouldReturnError_whenAnyParamBlank() {
            Result<RecommendationVolunteerDetailDTO> result = recommendationService
                    .getRecommendationVolunteerDetail("S001", " ", "M001");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("均不能为空"));
        }

        @Test
        @DisplayName("学生不存在时返回 404")
        void shouldReturn404_whenStudentNotFound() {
            when(studentMapper.getStudentById("S001")).thenReturn(null);

            Result<RecommendationVolunteerDetailDTO> result = recommendationService
                    .getRecommendationVolunteerDetail("S001", "U001", "M001");

            assertEquals(404, result.getCode());
            assertTrue(result.getMessage().contains("未找到对应学生信息"));
        }

        @Test
        @DisplayName("推荐明细不存在时返回 404")
        void shouldReturn404_whenDetailNotFound() {
            when(studentMapper.getStudentById("S001")).thenReturn(testStudent);
            when(recommendationMapper.selectRecommendationDetail("S001", "U001", "M001")).thenReturn(null);

            Result<RecommendationVolunteerDetailDTO> result = recommendationService
                    .getRecommendationVolunteerDetail("S001", "U001", "M001");

            assertEquals(404, result.getCode());
            assertTrue(result.getMessage().contains("未找到对应推荐数据"));
        }

        @Test
        @DisplayName("正常情况下计算录取概率并填充建议 - 高概率")
        void shouldCalculateProbabilityAndFillAdvice_highProbability() {
            when(studentMapper.getStudentById("S001")).thenReturn(testStudent);

            Map<String, Object> detailRow = new HashMap<>();
            detailRow.put("university_name", "大学A");
            detailRow.put("major_name", "计算机科学");
            detailRow.put("last_year_admission", 600);
            when(recommendationMapper.selectRecommendationDetail("S001", "U001", "M001")).thenReturn(detailRow);

            Map<String, Object> stats = new HashMap<>();
            stats.put("avgScore", 600);
            stats.put("stdScore", 5.0);
            stats.put("dataCount", 50);
            when(recommendationMapper.selectAdmissionStats("U001", "M001")).thenReturn(stats);

            Result<RecommendationVolunteerDetailDTO> result = recommendationService
                    .getRecommendationVolunteerDetail("S001", "U001", "M001");

            assertEquals(200, result.getCode());
            RecommendationVolunteerDetailDTO dto = result.getData();
            assertNotNull(dto);
            assertEquals("U001", dto.getUniversityId());
            assertEquals("大学A", dto.getUniversityName());
            assertEquals("M001", dto.getMajorId());
            assertEquals("计算机科学", dto.getMajorName());
            assertEquals(600.0, dto.getLastYearScore());
            assertNotNull(dto.getAdmissionProbability());
            assertNotNull(dto.getRecommendationRate());
            assertNotNull(dto.getAdvantageAnalysis());
            assertNotNull(dto.getRiskAdvice());
            assertNotNull(dto.getSuggestion());
        }

        @Test
        @DisplayName("低概率时填充冲刺建议")
        void shouldFillRushAdvice_whenLowProbability() {
            Student lowScoreStudent = new Student();
            lowScoreStudent.setStudentId("S001");
            lowScoreStudent.setCollegeEntranceExamScore(580);
            when(studentMapper.getStudentById("S001")).thenReturn(lowScoreStudent);

            Map<String, Object> detailRow = new HashMap<>();
            detailRow.put("university_name", "大学A");
            detailRow.put("major_name", "计算机科学");
            detailRow.put("last_year_admission", 640);
            when(recommendationMapper.selectRecommendationDetail("S001", "U001", "M001")).thenReturn(detailRow);

            Map<String, Object> stats = new HashMap<>();
            stats.put("avgScore", 640);
            stats.put("stdScore", 5.0);
            stats.put("dataCount", 50);
            when(recommendationMapper.selectAdmissionStats("U001", "M001")).thenReturn(stats);

            Result<RecommendationVolunteerDetailDTO> result = recommendationService
                    .getRecommendationVolunteerDetail("S001", "U001", "M001");

            assertEquals(200, result.getCode());
            assertTrue(result.getData().getAdvantageAnalysis().contains("冲刺") ||
                    result.getData().getRiskAdvice().contains("冲刺") ||
                    result.getData().getSuggestion().contains("冲刺") ||
                    result.getData().getAdvantageAnalysis().contains("差距"));
        }

        @Test
        @DisplayName("极低概率时填充风险警告")
        void shouldFillRiskWarning_whenVeryLowProbability() {
            Student veryLowScoreStudent = new Student();
            veryLowScoreStudent.setStudentId("S001");
            veryLowScoreStudent.setCollegeEntranceExamScore(500);
            when(studentMapper.getStudentById("S001")).thenReturn(veryLowScoreStudent);

            Map<String, Object> detailRow = new HashMap<>();
            detailRow.put("university_name", "大学A");
            detailRow.put("major_name", "计算机科学");
            detailRow.put("last_year_admission", 650);
            when(recommendationMapper.selectRecommendationDetail("S001", "U001", "M001")).thenReturn(detailRow);

            Map<String, Object> stats = new HashMap<>();
            stats.put("avgScore", 650);
            stats.put("stdScore", 5.0);
            stats.put("dataCount", 50);
            when(recommendationMapper.selectAdmissionStats("U001", "M001")).thenReturn(stats);

            Result<RecommendationVolunteerDetailDTO> result = recommendationService
                    .getRecommendationVolunteerDetail("S001", "U001", "M001");

            assertEquals(200, result.getCode());
            assertTrue(result.getData().getRiskAdvice().contains("冒进") ||
                    result.getData().getAdvantageAnalysis().contains("极低") ||
                    result.getData().getAdvantageAnalysis().contains("远高于"));
        }
    }
}
