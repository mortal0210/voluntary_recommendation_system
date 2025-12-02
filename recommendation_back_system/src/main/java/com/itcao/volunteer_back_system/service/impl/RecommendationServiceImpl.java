package com.itcao.volunteer_back_system.service.impl;

import com.itcao.volunteer_back_system.Dto.RecommendationStudentInfoDTO;
import com.itcao.volunteer_back_system.Dto.RecommendationVolunteerDetailDTO;
import com.itcao.volunteer_back_system.common.Result;
import com.itcao.volunteer_back_system.Dto.RecommendationItem;
import com.itcao.volunteer_back_system.Dto.RecommendationResult;
import com.itcao.volunteer_back_system.Dto.VolunteerItem;
import com.itcao.volunteer_back_system.Dto.ConflictItem;
import com.itcao.volunteer_back_system.Dto.ConflictResult;
import com.itcao.volunteer_back_system.Dto.ComprehensiveAnalysisResult;
import com.itcao.volunteer_back_system.Dto.ComprehensiveVolunteerItem;
import com.itcao.volunteer_back_system.Dto.UniversityMajorMatchResult;
import com.itcao.volunteer_back_system.mapper.RecommendationMapper;
import com.itcao.volunteer_back_system.mapper.StudentMapper;
import com.itcao.volunteer_back_system.mapper.VolunteerRelationMapper;
import com.itcao.volunteer_back_system.pojo.Student;
import com.itcao.volunteer_back_system.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能推荐服务实现类
 */
@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private RecommendationMapper recommendationMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private VolunteerRelationMapper volunteerRelationMapper;

    /**
     * 智能志愿推荐
     * 
     * @param studentId 学生ID
     * @return 推荐结果
     */
    @Override
    public Result<RecommendationResult> getVolunteerRecommendation(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return Result.error("学生ID不能为空");
        }

        try {
            // 1. 获取学生分数
            Student student = studentMapper.getStudentById(studentId.trim());
            if (student == null) {
                return Result.error(404, "未找到对应学生信息");
            }
            Double studentScore = (double) student.getCollegeEntranceExamScore();

            // 2. 基于成绩查询推荐列表（单结果集）
            List<Map<String, Object>> results = recommendationMapper.queryFlexibleRecommendations(studentId.trim());
            if (results == null || results.isEmpty()) {
                RecommendationResult emptyResult = new RecommendationResult(studentScore, new ArrayList<>());
                return Result.success(emptyResult);
            }

            List<RecommendationItem> recommendations = new ArrayList<>();
            for (Map<String, Object> result : results) {
                if (result.containsKey("university_name") &&
                        result.containsKey("major_name") &&
                        result.containsKey("last_year_admission") &&
                        result.containsKey("score_difference")) {

                    String universityId = result.containsKey("university_id") ? (String) result.get("university_id")
                            : null;
                    String majorId = result.containsKey("major_id") ? (String) result.get("major_id") : null;

                    Integer lastYearAdmission = result.get("last_year_admission") != null
                            ? Integer.valueOf(result.get("last_year_admission").toString())
                            : null;
                    Integer scoreDifference = result.get("score_difference") != null
                            ? Integer.valueOf(result.get("score_difference").toString())
                            : null;

                    RecommendationItem item = new RecommendationItem(
                            (String) result.get("university_name"),
                            universityId,
                            (String) result.get("major_name"),
                            majorId,
                            lastYearAdmission,
                            scoreDifference);

                    Map<String, Object> stats = recommendationMapper.selectAdmissionStats(universityId, majorId);
                    Double avgScore = stats != null ? toDouble(stats.get("avgScore")) : null;
                    Double stdScore = stats != null ? toDouble(stats.get("stdScore")) : null;
                    int dataCount = stats != null && stats.get("dataCount") != null
                            ? Integer.parseInt(stats.get("dataCount").toString())
                            : 0;

                    if (avgScore == null || avgScore == 0) {
                        avgScore = lastYearAdmission != null ? lastYearAdmission.doubleValue() : 600D;
                    }

                    double probability = calculateProbability(studentScore, avgScore, stdScore, dataCount);
                    double rate = Math.round((probability / 20.0) * 10.0) / 10.0;
                    item.setAdmissionProbability(probability);
                    item.setRecommendationRate(rate);

                    recommendations.add(item);
                }
            }

            RecommendationResult recommendationResult = new RecommendationResult(studentScore, recommendations);
            return Result.success(recommendationResult);
        } catch (Exception e) {
            return Result.error("获取志愿推荐失败: " + e.getMessage());
        }
    }

    /**
     * 志愿冲突检测
     * 
     * @param studentId 学生ID
     * @return 冲突检测结果
     */
    @Override
    public Result<ConflictResult> getVolunteerConflict(String studentId) {
        try {
            List<Map<String, Object>> results = recommendationMapper.callCheckVolunteerConflict(studentId);

            if (results == null || results.isEmpty()) {
                return Result.error("未找到志愿冲突检测结果");
            }

            Integer studentExists = null;
            Integer volunteerCount = null;
            Integer admissionCount = null;
            List<VolunteerItem> volunteers = new ArrayList<>();
            List<ConflictItem> conflicts = new ArrayList<>();

            // 处理调试信息
            for (Map<String, Object> result : results) {
                if (result.containsKey("debug_info")) {
                    String debugInfo = (String) result.get("debug_info");
                    if (debugInfo.startsWith("学生存在:")) {
                        studentExists = Integer.valueOf(debugInfo.substring(debugInfo.indexOf(":") + 1).trim());
                    } else if (debugInfo.startsWith("志愿数量:")) {
                        volunteerCount = Integer.valueOf(debugInfo.substring(debugInfo.indexOf(":") + 1).trim());
                    } else if (debugInfo.startsWith("匹配的历史录取数据:")) {
                        admissionCount = Integer.valueOf(debugInfo.substring(debugInfo.indexOf(":") + 1).trim());
                    }
                }
            }

            // 提取志愿列表
            for (Map<String, Object> result : results) {
                if (result.containsKey("volunteer_order") &&
                        result.containsKey("university_name") &&
                        result.containsKey("major_name")) {

                    VolunteerItem item = new VolunteerItem(
                            Integer.valueOf(result.get("volunteer_order").toString()),
                            (String) result.get("university_name"),
                            (String) result.get("major_name"),
                            result.containsKey("year") ? Integer.valueOf(result.get("year").toString()) : null,
                            result.containsKey("admission_number")
                                    ? Integer.valueOf(result.get("admission_number").toString())
                                    : null);
                    volunteers.add(item);
                }
            }

            // 提取冲突列表
            for (Map<String, Object> result : results) {
                if (result.containsKey("order1") &&
                        result.containsKey("uni1") &&
                        result.containsKey("advice")) {

                    ConflictItem item = new ConflictItem(
                            Integer.valueOf(result.get("order1").toString()),
                            (String) result.get("uni1"),
                            (String) result.get("major1"),
                            result.containsKey("score1") ? Integer.valueOf(result.get("score1").toString()) : null,
                            Integer.valueOf(result.get("order2").toString()),
                            (String) result.get("uni2"),
                            (String) result.get("major2"),
                            result.containsKey("score2") ? Integer.valueOf(result.get("score2").toString()) : null,
                            (String) result.get("advice"));
                    conflicts.add(item);
                }
            }

            // 创建冲突检测结果实体对象
            ConflictResult conflictResult = new ConflictResult(
                    studentExists,
                    volunteerCount,
                    admissionCount,
                    volunteers,
                    conflicts);

            return Result.success(conflictResult);
        } catch (Exception e) {
            return Result.error("获取志愿冲突检测失败: " + e.getMessage());
        }
    }

    /**
     * 志愿综合分析
     * 
     * @param studentId 学生ID
     * @return 志愿综合分析结果
     */
    @Override
    public Result<ComprehensiveAnalysisResult> getComprehensiveAnalysis(String studentId) {
        try {
            List<Map<String, Object>> results = recommendationMapper.callAnalyzeStudentVolunteerStrategy(studentId);

            if (results == null || results.isEmpty()) {
                return Result.error("未找到志愿综合分析结果");
            }

            ComprehensiveAnalysisResult analysisResult = new ComprehensiveAnalysisResult();
            List<ComprehensiveVolunteerItem> volunteers = new ArrayList<>();
            Map<String, Integer> riskDistribution = new HashMap<>();

            // 解析结果
            for (Map<String, Object> result : results) {
                // 解析学生基本信息
                if (result.containsKey("student_name")) {
                    analysisResult.setStudentName((String) result.get("student_name"));
                }
                if (result.containsKey("score")) {
                    analysisResult.setScore(Integer.valueOf(result.get("score").toString()));
                }
                if (result.containsKey("rank")) {
                    analysisResult.setRank(Integer.valueOf(result.get("rank").toString()));
                }
                if (result.containsKey("volunteer_count")) {
                    analysisResult.setVolunteerCount(Integer.valueOf(result.get("volunteer_count").toString()));
                }
                if (result.containsKey("overall_rating")) {
                    analysisResult.setOverallRating((String) result.get("overall_rating"));
                }
                if (result.containsKey("rationality_score")) {
                    analysisResult.setRationalityScore(Double.valueOf(result.get("rationality_score").toString()));
                }
                if (result.containsKey("safety_score")) {
                    analysisResult.setSafetyScore(Double.valueOf(result.get("safety_score").toString()));
                }
                if (result.containsKey("gradient_score")) {
                    analysisResult.setGradientScore(Double.valueOf(result.get("gradient_score").toString()));
                }
                if (result.containsKey("major_match_score")) {
                    analysisResult.setMajorMatchScore(Double.valueOf(result.get("major_match_score").toString()));
                }

                // 解析风险分析
                if (result.containsKey("risk_analysis")) {
                    analysisResult.setRiskAnalysis((String) result.get("risk_analysis"));
                }
                if (result.containsKey("gradient_analysis")) {
                    analysisResult.setGradientAnalysis((String) result.get("gradient_analysis"));
                }
                if (result.containsKey("major_match_analysis")) {
                    analysisResult.setMajorMatchAnalysis((String) result.get("major_match_analysis"));
                }
                if (result.containsKey("overall_suggestion")) {
                    analysisResult.setOverallSuggestion((String) result.get("overall_suggestion"));
                }

                // 解析风险分布
                if (result.containsKey("high")) {
                    riskDistribution.put("high", Integer.valueOf(result.get("high").toString()));
                }
                if (result.containsKey("medium")) {
                    riskDistribution.put("medium", Integer.valueOf(result.get("medium").toString()));
                }
                if (result.containsKey("low")) {
                    riskDistribution.put("low", Integer.valueOf(result.get("low").toString()));
                }

                // 解析志愿列表
                if (result.containsKey("order") &&
                        result.containsKey("university_name") &&
                        result.containsKey("major_name")) {

                    ComprehensiveVolunteerItem item = new ComprehensiveVolunteerItem();
                    item.setOrder(Integer.valueOf(result.get("order").toString()));
                    item.setUniversityName((String) result.get("university_name"));
                    item.setMajorName((String) result.get("major_name"));

                    if (result.containsKey("last_year_score")) {
                        item.setLastYearScore(Integer.valueOf(result.get("last_year_score").toString()));
                    }
                    if (result.containsKey("score_difference")) {
                        item.setScoreDifference(Integer.valueOf(result.get("score_difference").toString()));
                    }
                    if (result.containsKey("admission_probability")) {
                        item.setAdmissionProbability(Double.valueOf(result.get("admission_probability").toString()));
                    }
                    if (result.containsKey("risk_level")) {
                        item.setRiskLevel((String) result.get("risk_level"));
                    }

                    volunteers.add(item);
                }
            }

            analysisResult.setVolunteers(volunteers);
            analysisResult.setRiskDistribution(riskDistribution);

            return Result.success(analysisResult);
        } catch (Exception e) {
            return Result.error("获取志愿综合分析失败: " + e.getMessage());
        }
    }

    /**
     * 院校专业匹配
     * 
     * @param universityId 院校ID
     * @param majorId      专业ID
     * @return 院校专业匹配结果
     */
    @Override
    public Result<UniversityMajorMatchResult> getUniversityMajorMatch(String universityId, String majorId) {
        try {
            List<Map<String, Object>> results = recommendationMapper.callAnalyzeUniversityMajorMatch(universityId,
                    majorId);

            if (results == null || results.isEmpty()) {
                return Result.error("未找到院校专业匹配结果");
            }

            // 获取第一条结果
            Map<String, Object> result = results.get(0);
            UniversityMajorMatchResult matchResult = new UniversityMajorMatchResult();

            // 解析结果
            if (result.containsKey("university_name")) {
                matchResult.setUniversityName((String) result.get("university_name"));
            }
            if (result.containsKey("university_id")) {
                matchResult.setUniversityId((String) result.get("university_id"));
            }
            if (result.containsKey("university_level")) {
                matchResult.setUniversityLevel((String) result.get("university_level"));
            }
            if (result.containsKey("major_name")) {
                matchResult.setMajorName((String) result.get("major_name"));
            }
            if (result.containsKey("major_id")) {
                matchResult.setMajorId((String) result.get("major_id"));
            }
            if (result.containsKey("major_category")) {
                matchResult.setMajorCategory((String) result.get("major_category"));
            }
            if (result.containsKey("location")) {
                matchResult.setLocation((String) result.get("location"));
            }
            if (result.containsKey("admission_score")) {
                matchResult.setAdmissionScore(Integer.valueOf(result.get("admission_score").toString()));
            }
            if (result.containsKey("score_difference")) {
                matchResult.setScoreDifference(Integer.valueOf(result.get("score_difference").toString()));
            }
            if (result.containsKey("match_rate")) {
                matchResult.setMatchRate(Integer.valueOf(result.get("match_rate").toString()));
            }

            return Result.success(matchResult);
        } catch (Exception e) {
            return Result.error("获取院校专业匹配失败: " + e.getMessage());
        }
    }

    /**
     * 录取概率预测
     * 
     * @param studentId    学生ID
     * @param universityId 院校ID
     * @param majorId      专业ID
     * @return 录取概率预测结果
     */
    @Override
    public Result<Map<String, Object>> getAdmissionProbability(String studentId, String universityId, String majorId) {
        if (isBlank(studentId) || isBlank(universityId) || isBlank(majorId)) {
            return Result.error("studentId、universityId、majorId 均不能为空");
        }

        try {
            String trimStudentId = studentId.trim();
            String trimUniversityId = universityId.trim();
            String trimMajorId = majorId.trim();

            Student student = studentMapper.getStudentById(trimStudentId);
            if (student == null) {
                return Result.error(404, "未找到对应学生信息");
            }

            Map<String, Object> stats = recommendationMapper.selectAdmissionStats(trimUniversityId, trimMajorId);
            Double avgScore = stats != null ? toDouble(stats.get("avgScore")) : null;
            Double stdScore = stats != null ? toDouble(stats.get("stdScore")) : null;
            int dataCount = stats != null && stats.get("dataCount") != null
                    ? Integer.parseInt(stats.get("dataCount").toString())
                    : 0;

            // 如果没有历史数据，使用专业分数线作为参考
            if (avgScore == null || avgScore == 0) {
                Map<String, Object> detail = recommendationMapper.selectRecommendationDetail(
                        trimStudentId, trimUniversityId, trimMajorId);
                if (detail != null) {
                    Double lastYearScore = toDouble(detail.get("last_year_admission"));
                    if (lastYearScore != null) {
                        avgScore = lastYearScore;
                    }
                }
                if (avgScore == null || avgScore == 0) {
                    avgScore = 600D;
                }
            }

            double studentScore = student.getCollegeEntranceExamScore();
            double probability = calculateProbability(studentScore, avgScore, stdScore, dataCount);

            Map<String, Object> result = new HashMap<>();
            result.put("your_score", studentScore);
            result.put("avg_admission_score", avgScore);
            result.put("score_std_dev", stdScore != null ? stdScore : 0);
            result.put("historical_data_count", dataCount);
            result.put("admission_probability", probability);

            String level;
            if (probability >= 85) {
                level = "高概率";
            } else if (probability >= 65) {
                level = "中等概率";
            } else if (probability >= 35) {
                level = "低概率";
            } else {
                level = "极小概率";
            }
            result.put("probability_level", level);

            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取录取概率预测失败: " + e.getMessage());
        }
    }

    /**
     * 专业就业前景分析
     * 
     * @param majorId 专业ID
     * @return 就业前景分析结果
     */
    @Override
    public Result<Map<String, Object>> getEmploymentProspects(String majorId) {
        if (isBlank(majorId)) {
            return Result.error("专业ID不能为空");
        }

        try {
            Map<String, Object> base = recommendationMapper.selectMajorEmploymentBase(majorId.trim());
            if (base == null || base.isEmpty()) {
                return Result.error(404, "未找到该专业信息");
            }

            Map<String, Object> result = new HashMap<>(base);

            String category = (String) base.get("discipline_category");
            int lineScore = base.get("line_score") != null
                    ? Integer.parseInt(base.get("line_score").toString())
                    : 0;
            int volunteerCount = base.get("volunteer_count") != null
                    ? Integer.parseInt(base.get("volunteer_count").toString())
                    : 0;

            double avgEmploymentRate;
            double avgSalary;
            String outlook;

            if (category != null && category.contains("工学")) {
                avgEmploymentRate = 90;
                avgSalary = 12000;
                outlook = "工程技术和信息技术行业持续增长，就业前景整体向好。";
            } else if (category != null && category.contains("医学")) {
                avgEmploymentRate = 95;
                avgSalary = 15000;
                outlook = "医疗健康行业需求稳定，人才长期紧缺。";
            } else if (category != null && category.contains("管理")) {
                avgEmploymentRate = 85;
                avgSalary = 11000;
                outlook = "管理类岗位覆盖面广，对复合型人才需求较高。";
            } else if (category != null && category.contains("文学")) {
                avgEmploymentRate = 80;
                avgSalary = 9000;
                outlook = "传统就业相对分散，新媒体与文化创意产业提供新增岗位。";
            } else {
                avgEmploymentRate = 82;
                avgSalary = 9500;
                outlook = "该专业就业情况整体稳定，需结合区域与行业实际选择。";
            }

            // 根据分数线和志愿热度微调
            if (lineScore >= 650) {
                avgEmploymentRate += 3;
                avgSalary += 2000;
            } else if (lineScore >= 600) {
                avgEmploymentRate += 1;
                avgSalary += 1000;
            }
            if (volunteerCount > 20) {
                avgEmploymentRate += 1.5;
            }

            if (avgEmploymentRate > 98)
                avgEmploymentRate = 98;

            result.put("average_employment_rate", Math.round(avgEmploymentRate * 100.0) / 100.0);
            result.put("average_salary", Math.round(avgSalary));

            String employmentStatus;
            if (avgEmploymentRate >= 90) {
                employmentStatus = "就业率高";
            } else if (avgEmploymentRate >= 80) {
                employmentStatus = "就业率良好";
            } else {
                employmentStatus = "就业率一般";
            }
            result.put("employment_status", employmentStatus);
            result.put("industry_outlook", outlook);

            String demandLevel;
            if (volunteerCount > 30 || lineScore >= 640) {
                demandLevel = "高需求行业";
            } else if (volunteerCount > 10 || lineScore >= 600) {
                demandLevel = "稳定需求行业";
            } else {
                demandLevel = "竞争激烈行业";
            }
            result.put("demand_level", demandLevel);

            String advice;
            if ("高需求行业".equals(demandLevel)) {
                advice = "建议积极参与校内外实践，积累项目经验，冲刺头部单位。";
            } else if ("稳定需求行业".equals(demandLevel)) {
                advice = "建议关注行业前沿变化，提升核心技能和复合能力。";
            } else {
                advice = "建议尽早规划方向，打造差异化优势，并拓展跨行业机会。";
            }
            result.put("career_advice", advice);

            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取专业就业前景分析失败: " + e.getMessage());
        }
    }

    /**
     * 省份分数线分析
     * 
     * @param provinceId           省份ID
     * @param disciplineCategoryId 学科门类ID
     * @return 省份分数线分析结果
     */
    @Override
    public Result<Map<String, Object>> getProvinceScoreTrends(Integer provinceId, Integer disciplineCategoryId) {
        try {
            List<Map<String, Object>> results = recommendationMapper.callAnalyzeProvinceScoreTrends(provinceId,
                    disciplineCategoryId);

            if (results == null || results.isEmpty()) {
                return Result.error("未找到省份分数线分析结果");
            }

            // 检查是否返回了错误信息
            if (results.get(0).containsKey("message") &&
                    results.get(0).get("message").toString().startsWith("错误:")) {
                return Result.error(results.get(0).get("message").toString());
            }

            return Result.success(results.get(0));
        } catch (Exception e) {
            return Result.error("获取省份分数线分析失败: " + e.getMessage());
        }
    }

    /**
     * 获取学科门类列表
     * 
     * @return 学科门类列表
     */
    @Override
    public Result<Object> getDisciplineCategories() {
        try {
            List<Map<String, Object>> categories = recommendationMapper.getDisciplineCategories();

            if (categories == null || categories.isEmpty()) {
                return Result.error("未找到学科门类数据");
            }

            return Result.success(categories);
        } catch (Exception e) {
            return Result.error("获取学科门类列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<RecommendationStudentInfoDTO> getRecommendationStudentInfo(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return Result.error("学生ID不能为空");
        }

        Student student = studentMapper.getStudentById(studentId.trim());
        if (student == null) {
            return Result.error(404, "未找到对应学生信息");
        }

        RecommendationStudentInfoDTO dto = new RecommendationStudentInfoDTO();
        dto.setStudentId(student.getStudentId());
        dto.setStudentName(student.getStudentName());
        dto.setExamScore((double) student.getCollegeEntranceExamScore());
        dto.setProvinceRank(student.getRanking());

        int volunteerCount = volunteerRelationMapper.countByStudentId(studentId.trim());
        dto.setVolunteerCount(volunteerCount);

        Integer admissionMatchCount = recommendationMapper.countAdmissionMatches(studentId.trim());
        dto.setAdmissionMatchCount(admissionMatchCount == null ? 0 : admissionMatchCount);

        return Result.success(dto);
    }

    @Override
    public Result<RecommendationVolunteerDetailDTO> getRecommendationVolunteerDetail(String studentId,
            String universityId, String majorId) {
        if (isBlank(studentId) || isBlank(universityId) || isBlank(majorId)) {
            return Result.error("studentId、universityId、majorId 均不能为空");
        }

        try {
            String trimStudentId = studentId.trim();
            String trimUniversityId = universityId.trim();
            String trimMajorId = majorId.trim();

            Student student = studentMapper.getStudentById(trimStudentId);
            if (student == null) {
                return Result.error(404, "未找到对应学生信息");
            }

            Map<String, Object> detailRow = recommendationMapper.selectRecommendationDetail(
                    trimStudentId, trimUniversityId, trimMajorId);
            if (detailRow == null || detailRow.isEmpty()) {
                return Result.error(404, "未找到对应推荐数据");
            }

            RecommendationVolunteerDetailDTO dto = new RecommendationVolunteerDetailDTO();
            dto.setUniversityId(trimUniversityId);
            dto.setUniversityName(String.valueOf(detailRow.get("university_name")));
            dto.setMajorId(trimMajorId);
            dto.setMajorName(String.valueOf(detailRow.get("major_name")));

            Double lastYearScore = toDouble(detailRow.get("last_year_admission"));
            dto.setLastYearScore(lastYearScore);

            Double studentScore = (double) student.getCollegeEntranceExamScore();
            if (lastYearScore != null) {
                dto.setScoreDifference(studentScore - lastYearScore);
            }

            // 录取概率估算
            Map<String, Object> stats = recommendationMapper.selectAdmissionStats(trimUniversityId, trimMajorId);
            Double avgScore = stats != null ? toDouble(stats.get("avgScore")) : null;
            Double stdScore = stats != null ? toDouble(stats.get("stdScore")) : null;
            int dataCount = stats != null && stats.get("dataCount") != null
                    ? Integer.parseInt(stats.get("dataCount").toString())
                    : 0;

            if (avgScore == null || avgScore == 0) {
                avgScore = lastYearScore != null ? lastYearScore : 600D;
            }

            // TODO
            double probability = calculateProbability(studentScore, avgScore, stdScore, dataCount);
            dto.setAdmissionProbability(probability);
            dto.setRecommendationRate(Math.round((probability / 20.0) * 10.0) / 10.0);

            fillAdviceByProbability(dto, probability);

            return Result.success(dto);
        } catch (Exception e) {
            return Result.error("获取推荐详情失败: " + e.getMessage());
        }
    }

    private double calculateProbability(double studentScore, double referenceScore, Double stdScore, int dataCount) {
        double diff = studentScore - referenceScore;
        // 每高出1分增加3%概率，每低1分减少3%
        double base = 50 + diff * 3;

        double stabilityBonus = 0;
        if (stdScore != null && stdScore > 0) {
            stabilityBonus = Math.max(0, 10 - stdScore);
        } else if (dataCount == 0) {
            stabilityBonus = -5; // 参考数据缺失，略微减分
        }

        double prob = base + stabilityBonus;
        if (prob > 100) {
            prob = 100;
        } else if (prob < 0) {
            prob = 0;
        }
        return Math.round(prob * 100.0) / 100.0;
    }

    private void fillAdviceByProbability(RecommendationVolunteerDetailDTO dto, double probability) {
        if (probability >= 85) {
            dto.setAdvantageAnalysis("该院校专业与当前成绩高度匹配，录取把握较大。");
            dto.setRiskAdvice("整体风险较低，但仍需关注当年录取线波动。");
            dto.setSuggestion("可作为重点志愿，同时准备 1-2 个梯度略低的备选。");
        } else if (probability >= 65) {
            dto.setAdvantageAnalysis("该志愿与当前成绩匹配度较好，有一定录取机会。");
            dto.setRiskAdvice("存在中等风险，需关注专业热度变化。");
            dto.setSuggestion("建议与一到两个更稳妥的志愿搭配，并关注调剂策略。");
        } else if (probability >= 35) {
            dto.setAdvantageAnalysis("院校实力较强，但与当前成绩存在一定差距。");
            dto.setRiskAdvice("属于冲刺志愿，录取不确定性较大。");
            dto.setSuggestion("建议作为冲刺选项，同时准备 2-3 个更稳妥志愿确保录取。");
        } else {
            dto.setAdvantageAnalysis("该志愿分数要求远高于当前成绩，成功率极低。");
            dto.setRiskAdvice("过于冒进易导致志愿整体风险过高。");
            dto.setSuggestion("建议优先考虑分数更匹配的院校/专业，仅在综合权衡后保留一个冲刺位。");
        }
    }

    private Double toDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}