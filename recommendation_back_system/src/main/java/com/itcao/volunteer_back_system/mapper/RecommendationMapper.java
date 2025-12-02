package com.itcao.volunteer_back_system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 智能推荐数据访问层
 */
@Mapper
public interface RecommendationMapper {

        /**
         * 调用智能志愿推荐存储过程（仅用于调试，不再在业务中直接使用）
         *
         * @param studentId 学生ID
         * @return 结果集
         */
        List<Map<String, Object>> callFlexibleRecommendation(@Param("studentId") String studentId);

        /**
         * 基于学生成绩的智能志愿推荐查询（单结果集）
         *
         * @param studentId 学生ID
         * @return 推荐结果列表
         */
        List<Map<String, Object>> queryFlexibleRecommendations(@Param("studentId") String studentId);

        /**
         * 查询指定学生-院校-专业的推荐详情，与列表保持一致
         *
         * @param studentId    学生ID
         * @param universityId 院校ID
         * @param majorId      专业ID
         * @return 详情数据
         */
        Map<String, Object> selectRecommendationDetail(@Param("studentId") String studentId,
                        @Param("universityId") String universityId,
                        @Param("majorId") String majorId);

        /**
         * 录取数据统计（均值/标准差/数据量）
         *
         * @param universityId 院校ID
         * @param majorId      专业ID
         * @return 统计信息
         */
        Map<String, Object> selectAdmissionStats(@Param("universityId") String universityId,
                        @Param("majorId") String majorId);

        /**
         * 获取专业基础信息及就业分析所需字段
         *
         * @param majorId 专业ID
         * @return 基础信息
         */
        Map<String, Object> selectMajorEmploymentBase(@Param("majorId") String majorId);

        /**
         * 调用志愿冲突检测存储过程
         * 
         * @param studentId 学生ID
         * @return 冲突检测结果
         */
        List<Map<String, Object>> callCheckVolunteerConflict(@Param("studentId") String studentId);

        /**
         * 调用专业就业前景分析存储过程
         * 
         * @param majorId 专业ID
         * @return 就业前景分析结果
         */
        List<Map<String, Object>> callAnalyzeMajorEmploymentProspects(@Param("majorId") String majorId);

        /**
         * 调用省份分数线分析存储过程
         * 
         * @param provinceId           省份ID
         * @param disciplineCategoryId 学科门类ID
         * @return 省份分数线分析结果
         */
        List<Map<String, Object>> callAnalyzeProvinceScoreTrends(
                        @Param("provinceId") Integer provinceId,
                        @Param("disciplineCategoryId") Integer disciplineCategoryId);

        /**
         * 调用志愿综合分析存储过程
         * 
         * @param studentId 学生ID
         * @return 志愿综合分析结果
         */
        List<Map<String, Object>> callAnalyzeStudentVolunteerStrategy(@Param("studentId") String studentId);

        /**
         * 调用院校专业匹配度分析存储过程
         * 
         * @param universityId 院校ID
         * @param majorId      专业ID
         * @return 院校专业匹配度分析结果
         */
        List<Map<String, Object>> callAnalyzeUniversityMajorMatch(
                        @Param("universityId") String universityId,
                        @Param("majorId") String majorId);

        /**
         * 获取学科门类列表
         * 
         * @return 学科门类列表
         */
        List<Map<String, Object>> getDisciplineCategories();

        /**
         * 统计与学生志愿匹配的历史录取数据数量
         *
         * @param studentId 学生ID
         * @return 匹配数量
         */
        Integer countAdmissionMatches(@Param("studentId") String studentId);
}