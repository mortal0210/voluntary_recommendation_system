package com.itcao.volunteer_back_system.mapper;

import com.itcao.volunteer_back_system.pojo.Student;
import org.junit.jupiter.api.*;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * StudentMapper 数据访问层测试
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("StudentMapper 数据访问层测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentMapperTest {

    @Autowired
    private StudentMapper studentMapper;

    // ==================== 分页查询测试 ====================
    @Nested
    @DisplayName("分页查询测试")
    class PageQueryTests {

        @Test
        @DisplayName("分页查询 - 无筛选条件")
        void getStudentPage_shouldReturnList_withoutFilters() {
            List<Student> students = studentMapper.getStudentPage(
                    null, null, null, null, 0, 10);
            
            assertThat(students).isNotNull();
            assertThat(students.size()).isLessThanOrEqualTo(10);
        }

        @Test
        @DisplayName("分页查询 - 按学生ID筛选")
        void getStudentPage_shouldFilterByStudentId() {
            List<Student> students = studentMapper.getStudentPage(
                    "1", null, null, null, 0, 10);
            
            assertThat(students).isNotNull();
            // 验证结果中的学生ID包含筛选条件
            students.forEach(student -> 
                assertThat(student.getStudentId()).contains("1")
            );
        }

        @Test
        @DisplayName("分页查询 - 按性别筛选")
        void getStudentPage_shouldFilterByGender() {
            List<Student> students = studentMapper.getStudentPage(
                    null, null, "男", null, 0, 10);
            
            assertThat(students).isNotNull();
            students.forEach(student -> 
                assertThat(student.getGender()).isEqualTo("男")
            );
        }

        @Test
        @DisplayName("分页查询 - 按省份筛选")
        void getStudentPage_shouldFilterByProvinceId() {
            List<Student> students = studentMapper.getStudentPage(
                    null, null, null, 11, 0, 10);
            
            assertThat(students).isNotNull();
            students.forEach(student -> 
                assertThat(student.getProvinceId()).isEqualTo(11)
            );
        }

        @Test
        @DisplayName("获取学生总数")
        void getStudentCount_shouldReturnCount() {
            Long count = studentMapper.getStudentCount(null, null, null, null);
            
            assertThat(count).isNotNull();
            assertThat(count).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("获取学生总数 - 带筛选条件")
        void getStudentCount_shouldReturnFilteredCount() {
            Long totalCount = studentMapper.getStudentCount(null, null, null, null);
            Long maleCount = studentMapper.getStudentCount(null, null, "男", null);
            
            assertThat(maleCount).isLessThanOrEqualTo(totalCount);
        }
    }

    // ==================== 单个学生查询测试 ====================
    @Nested
    @DisplayName("单个学生查询测试")
    class SingleQueryTests {

        @Test
        @DisplayName("根据ID查询学生 - 存在的学生")
        void getStudentById_shouldReturnStudent_whenExists() {
            // 先获取一个存在的学生
            List<Student> students = studentMapper.getStudentPage(
                    null, null, null, null, 0, 1);
            
            if (!students.isEmpty()) {
                String studentId = students.get(0).getStudentId();
                Student student = studentMapper.getStudentById(studentId);
                
                assertThat(student).isNotNull();
                assertThat(student.getStudentId()).isEqualTo(studentId);
            }
        }

        @Test
        @DisplayName("根据ID查询学生 - 不存在的学生")
        void getStudentById_shouldReturnNull_whenNotExists() {
            Student student = studentMapper.getStudentById("NOTEXIST999999");
            
            assertThat(student).isNull();
        }
    }

    // ==================== 删除学生测试 ====================
    @Nested
    @DisplayName("删除学生测试")
    class DeleteTests {

        @Test
        @DisplayName("删除不存在的学生")
        void deleteStudents_shouldReturnZero_whenStudentsNotExist() {
            int result = studentMapper.deleteStudents(
                    Arrays.asList("NOTEXIST1", "NOTEXIST2"));
            
            assertThat(result).isEqualTo(0);
        }
    }

    // ==================== 数据完整性测试 ====================
    @Nested
    @DisplayName("数据完整性测试")
    class DataIntegrityTests {

        @Test
        @DisplayName("学生数据应包含必要字段")
        void student_shouldHaveRequiredFields() {
            List<Student> students = studentMapper.getStudentPage(
                    null, null, null, null, 0, 5);
            
            students.forEach(student -> {
                assertThat(student.getStudentId()).isNotNull();
                assertThat(student.getStudentName()).isNotNull();
                // 高考分数应该在合理范围内
                assertThat(student.getCollegeEntranceExamScore())
                        .isBetween(0f, 750f);
            });
        }
    }
}
