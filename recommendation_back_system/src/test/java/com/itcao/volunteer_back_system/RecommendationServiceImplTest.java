package com.itcao.volunteer_back_system;

import com.itcao.volunteer_back_system.Dto.RecommendationStudentInfoDTO;
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

import java.util.HashMap;
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
}
