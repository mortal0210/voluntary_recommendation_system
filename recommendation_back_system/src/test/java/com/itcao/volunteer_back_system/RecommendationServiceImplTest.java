package com.itcao.volunteer_back_system;

import com.itcao.volunteer_back_system.Dto.ComprehensiveAnalysisResult;
import com.itcao.volunteer_back_system.Dto.ConflictResult;
import com.itcao.volunteer_back_system.Dto.RecommendationResult;
import com.itcao.volunteer_back_system.Dto.RecommendationStudentInfoDTO;
import com.itcao.volunteer_back_system.Dto.RecommendationVolunteerDetailDTO;
import com.itcao.volunteer_back_system.Dto.UniversityMajorMatchResult;
import com.itcao.volunteer_back_system.common.Result;
import com.itcao.volunteer_back_system.mapper.RecommendationMapper;
import com.itcao.volunteer_back_system.mapper.StudentMapper;
import com.itcao.volunteer_back_system.mapper.VolunteerRelationMapper;
import com.itcao.volunteer_back_system.pojo.Student;
import com.itcao.volunteer_back_system.service.impl.RecommendationServiceImpl;
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
import static org.mockito.Mockito.when;

/**
 * RecommendationServiceImpl 的单元测试
 *
 * <p>
 * 设计思路：
 * <ul>
 * <li>不启动 Spring 容器，仅使用 JUnit5 + Mockito，对业务逻辑做“纯单元测试”；</li>
 * <li>对外部依赖（Mapper）全部使用 {@link Mock}；</li>
 * <li>每个测试方法名称采用「方法名_场景_期望结果」的风格，方便快速定位测试意图。</li>
 * </ul>
 * 重点覆盖：
 * <ul>
 * <li>{@link RecommendationServiceImpl#getAdmissionProbability(String, String, String)}
 * 的参数校验、学生不存在、存在/不存在统计数据等分支；</li>
 * <li>{@link RecommendationServiceImpl#getRecommendationStudentInfo(String)}
 * 的参数校验、学生不存在以及正常返回 DTO 的情况。</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private VolunteerRelationMapper volunteerRelationMapper;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    // ---------- getVolunteerRecommendation：智能志愿推荐 ----------

    /**
     * getVolunteerRecommendation：studentId 为空时返回错误。
     */
    @Test
    void getVolunteerRecommendation_shouldReturnError_whenStudentIdBlank() {
        Result<RecommendationResult> result = recommendationService.getVolunteerRecommendation("  ");

        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("学生ID不能为空"));
    }

    /**
     * getVolunteerRecommendation：学生不存在时返回 404。
     */
    @Test
    void getVolunteerRecommendation_shouldReturn404_whenStudentNotFound() {
        String studentId = "S001";
        when(studentMapper.getStudentById(studentId)).thenReturn(null);

        Result<RecommendationResult> result = recommendationService.getVolunteerRecommendation(studentId);

        assertEquals(404, result.getCode());
        assertTrue(result.getMessage().contains("未找到对应学生信息"));
    }

    /**
     * getVolunteerRecommendation：推荐结果列表为空时，返回空列表但 code=200。
     */
    @Test
    void getVolunteerRecommendation_shouldReturnEmptyList_whenNoResults() {
        String studentId = "S001";
        Student stu = new Student();
        stu.setStudentId(studentId);
        stu.setCollegeEntranceExamScore(650);
        when(studentMapper.getStudentById(studentId)).thenReturn(stu);
        when(recommendationMapper.queryFlexibleRecommendations(studentId)).thenReturn(new ArrayList<>());

        Result<RecommendationResult> result = recommendationService.getVolunteerRecommendation(studentId);

        assertEquals(200, result.getCode());
        RecommendationResult data = result.getData();
        assertNotNull(data);
        assertEquals(650.0, data.getStudentScore());
        assertNotNull(data.getRecommendations());
        assertEquals(0, data.getRecommendations().size());
    }

    // ---------- getAdmissionProbability：录取概率预测 ----------

    /**
     * 任意一个入参为 null 或空字符串时，应直接返回业务错误（code=500），提示“不能为空”。
     */
    @Test
    void getAdmissionProbability_shouldReturnError_whenAnyParamBlank() {
        Result<Map<String, Object>> r1 = recommendationService.getAdmissionProbability(null, "u1", "m1");
        Result<Map<String, Object>> r2 = recommendationService.getAdmissionProbability("s1", " ", "m1");

        assertEquals(500, r1.getCode());
        assertTrue(r1.getMessage().contains("不能为空"));
        assertEquals(500, r2.getCode());
        assertTrue(r2.getMessage().contains("不能为空"));
    }

    /**
     * 当根据 studentId 查询不到学生时，应返回 code=404 且错误信息中包含“未找到对应学生信息”。
     */
    @Test
    void getAdmissionProbability_shouldReturn404_whenStudentNotFound() {
        String studentId = "S001";
        when(studentMapper.getStudentById(studentId)).thenReturn(null);

        Result<Map<String, Object>> result = recommendationService.getAdmissionProbability(studentId, "U001", "M001");

        assertEquals(404, result.getCode());
        assertTrue(result.getMessage().contains("未找到对应学生信息"));
    }

    /**
     * 正常情况：存在可靠的历史统计数据（avgScore/stdScore/dataCount），
     * 应正确填充结果中的：
     * <ul>
     * <li>考生成绩 your_score；</li>
     * <li>历史平均录取分 avg_admission_score；</li>
     * <li>历史样本数量 historical_data_count；</li>
     * <li>录取概率 admission_probability（0~100 之间）；</li>
     * <li>概率等级 probability_level（高/中/低/极小概率）。</li>
     * </ul>
     */
    @Test
    void getAdmissionProbability_shouldUseHistoryStats_whenAvailable() {
        String studentId = "S001";
        String universityId = "U001";
        String majorId = "M001";

        Student stu = new Student();
        stu.setStudentId(studentId);
        stu.setCollegeEntranceExamScore(650);
        when(studentMapper.getStudentById(studentId)).thenReturn(stu);

        Map<String, Object> stats = new HashMap<>();
        stats.put("avgScore", 640);
        stats.put("stdScore", 5.0);
        stats.put("dataCount", 100);
        when(recommendationMapper.selectAdmissionStats(universityId, majorId)).thenReturn(stats);

        Result<Map<String, Object>> result = recommendationService.getAdmissionProbability(studentId, universityId,
                majorId);

        assertEquals(200, result.getCode());
        Map<String, Object> data = result.getData();
        assertNotNull(data);
        assertEquals(650.0, (double) data.get("your_score"));
        assertEquals(640.0, (double) data.get("avg_admission_score"));
        assertEquals(100, data.get("historical_data_count"));

        Double probability = (Double) data.get("admission_probability");
        assertNotNull(probability);
        assertTrue(probability >= 0.0 && probability <= 100.0);

        String level = (String) data.get("probability_level");
        assertNotNull(level);
        assertTrue(level.equals("高概率")
                || level.equals("中等概率")
                || level.equals("低概率")
                || level.equals("极小概率"));
    }

    /**
     * 当统计表中没有任何历史数据（selectAdmissionStats 返回 null），
     * 应退化为使用推荐明细中的 last_year_admission 作为参考平均分。
     */
    @Test
    void getAdmissionProbability_shouldFallbackToLastYearScore_whenNoStats() {
        String studentId = "S001";
        String universityId = "U001";
        String majorId = "M001";

        Student stu = new Student();
        stu.setStudentId(studentId);
        stu.setCollegeEntranceExamScore(620);
        when(studentMapper.getStudentById(studentId)).thenReturn(stu);

        // 没有 stats
        when(recommendationMapper.selectAdmissionStats(universityId, majorId)).thenReturn(null);

        Map<String, Object> detail = new HashMap<>();
        detail.put("last_year_admission", 610);
        when(recommendationMapper.selectRecommendationDetail(studentId, universityId, majorId))
                .thenReturn(detail);

        Result<Map<String, Object>> result = recommendationService.getAdmissionProbability(studentId, universityId,
                majorId);

        assertEquals(200, result.getCode());
        Map<String, Object> data = result.getData();
        assertNotNull(data);
        assertEquals(610.0, (double) data.get("avg_admission_score"));
    }

    // ---------- getRecommendationStudentInfo：学生推荐信息概要 ----------

    /**
     * studentId 为空字符串时，应返回 code=500 且错误信息包含“学生ID不能为空”。
     */
    @Test
    void getRecommendationStudentInfo_shouldReturnError_whenStudentIdBlank() {
        Result<RecommendationStudentInfoDTO> result = recommendationService.getRecommendationStudentInfo("  ");

        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("学生ID不能为空"));
    }

    /**
     * 当根据 studentId 查询不到学生时，应返回 code=404，提示“未找到对应学生信息”。
     */
    @Test
    void getRecommendationStudentInfo_shouldReturn404_whenStudentNotFound() {
        String studentId = "S001";
        when(studentMapper.getStudentById(studentId)).thenReturn(null);

        Result<RecommendationStudentInfoDTO> result = recommendationService.getRecommendationStudentInfo(studentId);

        assertEquals(404, result.getCode());
        assertTrue(result.getMessage().contains("未找到对应学生信息"));
    }

    /**
     * 正常情况：学生存在并能查到志愿数量和匹配数量，
     * 应正确封装为 RecommendationStudentInfoDTO 并返回 code=200。
     */
    @Test
    void getRecommendationStudentInfo_shouldReturnDto_whenStudentExists() {
        String studentId = "S001";
        Student stu = new Student();
        stu.setStudentId(studentId);
        stu.setStudentName("张三");
        stu.setCollegeEntranceExamScore(630);
        stu.setRanking(12345);
        when(studentMapper.getStudentById(studentId)).thenReturn(stu);

        when(volunteerRelationMapper.countByStudentId(studentId)).thenReturn(5);
        when(recommendationMapper.countAdmissionMatches(studentId)).thenReturn(2);

        Result<RecommendationStudentInfoDTO> result = recommendationService.getRecommendationStudentInfo(studentId);

        assertEquals(200, result.getCode());
        RecommendationStudentInfoDTO dto = result.getData();
        assertNotNull(dto);
        assertEquals(studentId, dto.getStudentId());
        assertEquals("张三", dto.getStudentName());
        assertEquals(630.0, dto.getExamScore());
        assertEquals(12345, dto.getProvinceRank());
        assertEquals(5, dto.getVolunteerCount());
        assertEquals(2, dto.getAdmissionMatchCount());
    }

    // ---------- getVolunteerConflict：志愿冲突检测 ----------

    /**
     * getVolunteerConflict：当存储过程无结果（null/空列表）时返回错误。
     */
    @Test
    void getVolunteerConflict_shouldReturnError_whenNoResults() {
        String studentId = "S001";
        when(recommendationMapper.callCheckVolunteerConflict(studentId)).thenReturn(null);

        Result<ConflictResult> result = recommendationService.getVolunteerConflict(studentId);

        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("未找到志愿冲突检测结果"));
    }

    /**
     * getVolunteerConflict：正常返回时，能够解析出学生存在标记和志愿/冲突列表。
     */
    @Test
    void getVolunteerConflict_shouldParseResultCorrectly() {
        String studentId = "S001";
        List<Map<String, Object>> rows = new ArrayList<>();

        // 第一行：debug 信息
        Map<String, Object> debugRow = new HashMap<>();
        debugRow.put("debug_info", "学生存在: 1");
        rows.add(debugRow);

        // 第二行：志愿信息
        Map<String, Object> volunteerRow = new HashMap<>();
        volunteerRow.put("volunteer_order", 1);
        volunteerRow.put("university_name", "大学A");
        volunteerRow.put("major_name", "专业A");
        volunteerRow.put("year", 2024);
        volunteerRow.put("admission_number", 100);
        rows.add(volunteerRow);

        // 第三行：冲突信息
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

        when(recommendationMapper.callCheckVolunteerConflict(studentId)).thenReturn(rows);

        Result<ConflictResult> result = recommendationService.getVolunteerConflict(studentId);

        assertEquals(200, result.getCode());
        ConflictResult data = result.getData();
        assertNotNull(data);
        assertEquals(1, data.getStudentExists());
        assertNotNull(data.getVolunteers());
        assertEquals(1, data.getVolunteers().size());
        assertNotNull(data.getConflicts());
        assertEquals(1, data.getConflicts().size());
    }

    // ---------- getComprehensiveAnalysis：志愿综合分析 ----------

    /**
     * getComprehensiveAnalysis：无结果返回时给出业务错误。
     */
    @Test
    void getComprehensiveAnalysis_shouldReturnError_whenNoResults() {
        String studentId = "S001";
        when(recommendationMapper.callAnalyzeStudentVolunteerStrategy(studentId)).thenReturn(new ArrayList<>());

        Result<ComprehensiveAnalysisResult> result = recommendationService.getComprehensiveAnalysis(studentId);

        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("未找到志愿综合分析结果"));
    }

    /**
     * getComprehensiveAnalysis：正常情况下可以解析基础信息和志愿列表。
     */
    @Test
    void getComprehensiveAnalysis_shouldParseResultCorrectly() {
        String studentId = "S001";
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

        when(recommendationMapper.callAnalyzeStudentVolunteerStrategy(studentId)).thenReturn(rows);

        Result<ComprehensiveAnalysisResult> result = recommendationService.getComprehensiveAnalysis(studentId);

        assertEquals(200, result.getCode());
        ComprehensiveAnalysisResult data = result.getData();
        assertNotNull(data);
        assertEquals("张三", data.getStudentName());
        assertEquals(630, data.getScore());
        assertEquals(3, data.getVolunteerCount());
        assertNotNull(data.getVolunteers());
        assertEquals(1, data.getVolunteers().size());
        assertNotNull(data.getRiskDistribution());
        assertEquals(1, data.getRiskDistribution().get("high"));
    }

    // ---------- getUniversityMajorMatch：院校专业匹配 ----------

    /**
     * getUniversityMajorMatch：无结果时返回错误。
     */
    @Test
    void getUniversityMajorMatch_shouldReturnError_whenNoResults() {
        when(recommendationMapper.callAnalyzeUniversityMajorMatch("U001", "M001"))
                .thenReturn(new ArrayList<>());

        Result<UniversityMajorMatchResult> result = recommendationService.getUniversityMajorMatch("U001", "M001");

        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("未找到院校专业匹配结果"));
    }

    /**
     * getUniversityMajorMatch：正常情况下可以解析第一行结果。
     */
    @Test
    void getUniversityMajorMatch_shouldParseFirstRow() {
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("university_name", "大学A");
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

        when(recommendationMapper.callAnalyzeUniversityMajorMatch("U001", "M001"))
                .thenReturn(rows);

        Result<UniversityMajorMatchResult> result = recommendationService.getUniversityMajorMatch("U001", "M001");

        assertEquals(200, result.getCode());
        UniversityMajorMatchResult data = result.getData();
        assertNotNull(data);
        assertEquals("大学A", data.getUniversityName());
        assertEquals("U001", data.getUniversityId());
        assertEquals("计算机科学", data.getMajorName());
        assertEquals(650, data.getAdmissionScore());
    }

    // ---------- getEmploymentProspects：专业就业前景分析 ----------

    /**
     * getEmploymentProspects：majorId 为空时返回错误。
     */
    @Test
    void getEmploymentProspects_shouldReturnError_whenMajorIdBlank() {
        Result<Map<String, Object>> result = recommendationService.getEmploymentProspects("  ");

        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("专业ID不能为空"));
    }

    /**
     * getEmploymentProspects：基础数据为空时返回 404。
     */
    @Test
    void getEmploymentProspects_shouldReturn404_whenBaseInfoMissing() {
        String majorId = "M001";
        when(recommendationMapper.selectMajorEmploymentBase(majorId))
                .thenReturn(null);

        Result<Map<String, Object>> result = recommendationService.getEmploymentProspects(majorId);

        assertEquals(404, result.getCode());
        assertTrue(result.getMessage().contains("未找到该专业信息"));
    }

    /**
     * getEmploymentProspects：针对“工学”类别正常计算就业率与薪资等。
     */
    @Test
    void getEmploymentProspects_shouldCalculateForEngineeringCategory() {
        String majorId = "M001";
        Map<String, Object> base = new HashMap<>();
        base.put("discipline_category", "工学");
        base.put("line_score", 650);
        base.put("volunteer_count", 25);
        when(recommendationMapper.selectMajorEmploymentBase(majorId)).thenReturn(base);

        Result<Map<String, Object>> result = recommendationService.getEmploymentProspects(majorId);

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

    // ---------- getProvinceScoreTrends：省份分数线分析 ----------

    /**
     * getProvinceScoreTrends：无结果时返回错误。
     */
    @Test
    void getProvinceScoreTrends_shouldReturnError_whenNoResults() {
        when(recommendationMapper.callAnalyzeProvinceScoreTrends(11, 1))
                .thenReturn(new ArrayList<>());

        Result<Map<String, Object>> result = recommendationService.getProvinceScoreTrends(11, 1);

        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("未找到省份分数线分析结果"));
    }

    /**
     * getProvinceScoreTrends：当第一行 message 以“错误:”开头时，直接返回该错误信息。
     */
    @Test
    void getProvinceScoreTrends_shouldReturnError_whenMessageIndicatesError() {
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("message", "错误: 参数不合法");
        rows.add(row);

        when(recommendationMapper.callAnalyzeProvinceScoreTrends(11, 1))
                .thenReturn(rows);

        Result<Map<String, Object>> result = recommendationService.getProvinceScoreTrends(11, 1);

        assertEquals(500, result.getCode());
        assertEquals("错误: 参数不合法", result.getMessage());
    }

    /**
     * getProvinceScoreTrends：正常返回时，直接包裹第一行结果。
     */
    @Test
    void getProvinceScoreTrends_shouldWrapFirstRowOnSuccess() {
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("province_name", "北京");
        row.put("year", 2024);
        row.put("score", 600);
        rows.add(row);

        when(recommendationMapper.callAnalyzeProvinceScoreTrends(11, 1))
                .thenReturn(rows);

        Result<Map<String, Object>> result = recommendationService.getProvinceScoreTrends(11, 1);

        assertEquals(200, result.getCode());
        Map<String, Object> data = result.getData();
        assertNotNull(data);
        assertEquals("北京", data.get("province_name"));
        assertEquals(2024, data.get("year"));
        assertEquals(600, data.get("score"));
    }

    // ---------- getDisciplineCategories：学科门类列表 ----------

    /**
     * getDisciplineCategories：当结果为空时返回错误。
     */
    @Test
    void getDisciplineCategories_shouldReturnError_whenNoData() {
        when(recommendationMapper.getDisciplineCategories())
                .thenReturn(new ArrayList<>());

        Result<Object> result = recommendationService.getDisciplineCategories();

        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("未找到学科门类数据"));
    }

    /**
     * getDisciplineCategories：正常情况返回列表并 code=200。
     */
    @Test
    void getDisciplineCategories_shouldReturnListOnSuccess() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("id", 1);
        row.put("name", "工学");
        list.add(row);
        when(recommendationMapper.getDisciplineCategories())
                .thenReturn(list);

        Result<Object> result = recommendationService.getDisciplineCategories();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    // ---------- getRecommendationVolunteerDetail：推荐志愿详情 ----------

    /**
     * getRecommendationVolunteerDetail：任一参数为空时返回错误。
     */
    @Test
    void getRecommendationVolunteerDetail_shouldReturnError_whenAnyParamBlank() {
        Result<RecommendationVolunteerDetailDTO> result = recommendationService.getRecommendationVolunteerDetail("s1",
                " ", "m1");

        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("均不能为空"));
    }

    /**
     * getRecommendationVolunteerDetail：学生不存在时返回 404。
     */
    @Test
    void getRecommendationVolunteerDetail_shouldReturn404_whenStudentNotFound() {
        String studentId = "S001";
        when(studentMapper.getStudentById(studentId)).thenReturn(null);

        Result<RecommendationVolunteerDetailDTO> result = recommendationService
                .getRecommendationVolunteerDetail(studentId, "U001", "M001");

        assertEquals(404, result.getCode());
        assertTrue(result.getMessage().contains("未找到对应学生信息"));
    }

    /**
     * getRecommendationVolunteerDetail：推荐明细不存在时返回 404。
     */
    @Test
    void getRecommendationVolunteerDetail_shouldReturn404_whenDetailNotFound() {
        String studentId = "S001";
        Student stu = new Student();
        stu.setStudentId(studentId);
        stu.setCollegeEntranceExamScore(640);
        when(studentMapper.getStudentById(studentId)).thenReturn(stu);
        when(recommendationMapper.selectRecommendationDetail(studentId, "U001", "M001"))
                .thenReturn(null);

        Result<RecommendationVolunteerDetailDTO> result = recommendationService
                .getRecommendationVolunteerDetail(studentId, "U001", "M001");

        assertEquals(404, result.getCode());
        assertTrue(result.getMessage().contains("未找到对应推荐数据"));
    }

    /**
     * getRecommendationVolunteerDetail：正常情况下可计算录取概率并填充建议。
     */
    @Test
    void getRecommendationVolunteerDetail_shouldCalculateProbabilityAndFillAdvice() {
        String studentId = "S001";
        String universityId = "U001";
        String majorId = "M001";

        Student stu = new Student();
        stu.setStudentId(studentId);
        stu.setCollegeEntranceExamScore(650);
        when(studentMapper.getStudentById(studentId)).thenReturn(stu);

        Map<String, Object> detailRow = new HashMap<>();
        detailRow.put("university_name", "大学A");
        detailRow.put("major_name", "计算机科学");
        detailRow.put("last_year_admission", 640);
        when(recommendationMapper.selectRecommendationDetail(studentId, universityId, majorId))
                .thenReturn(detailRow);

        Map<String, Object> stats = new HashMap<>();
        stats.put("avgScore", 640);
        stats.put("stdScore", 5.0);
        stats.put("dataCount", 50);
        when(recommendationMapper.selectAdmissionStats(universityId, majorId))
                .thenReturn(stats);

        Result<RecommendationVolunteerDetailDTO> result = recommendationService
                .getRecommendationVolunteerDetail(studentId, universityId, majorId);

        assertEquals(200, result.getCode());
        RecommendationVolunteerDetailDTO dto = result.getData();
        assertNotNull(dto);
        assertEquals(universityId, dto.getUniversityId());
        assertEquals("大学A", dto.getUniversityName());
        assertEquals(majorId, dto.getMajorId());
        assertEquals("计算机科学", dto.getMajorName());
        assertEquals(640.0, dto.getLastYearScore());
        assertNotNull(dto.getAdmissionProbability());
        assertNotNull(dto.getRecommendationRate());
        assertNotNull(dto.getAdvantageAnalysis());
        assertNotNull(dto.getRiskAdvice());
        assertNotNull(dto.getSuggestion());
    }
}
