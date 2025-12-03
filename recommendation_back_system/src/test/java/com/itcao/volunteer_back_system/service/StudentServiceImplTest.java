package com.itcao.volunteer_back_system.service;

import com.itcao.volunteer_back_system.common.PageResult;
import com.itcao.volunteer_back_system.common.Result;
import com.itcao.volunteer_back_system.mapper.StudentMapper;
import com.itcao.volunteer_back_system.pojo.Student;
import com.itcao.volunteer_back_system.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * StudentServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentServiceImpl 单元测试")
class StudentServiceImplTest {

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setStudentId("S001");
        testStudent.setStudentName("张三");
        testStudent.setIdCard("110101200001011234");
        testStudent.setGender("男");
        testStudent.setCollegeEntranceExamScore(650);
        testStudent.setProvinceId(11);
        testStudent.setRanking(1000);
        testStudent.setProvinceName("北京");
    }

    // ==================== 分页查询测试 ====================
    @Nested
    @DisplayName("分页查询学生测试")
    class GetStudentPageTests {

        @Test
        @DisplayName("分页查询成功 - 无筛选条件")
        void getStudentPage_shouldReturnPageResult_withoutFilters() {
            List<Student> students = Arrays.asList(testStudent);
            when(studentMapper.getStudentPage(null, null, null, null, 0, 10))
                    .thenReturn(students);
            when(studentMapper.getStudentCount(null, null, null, null))
                    .thenReturn(1L);

            Result<PageResult<Student>> result = studentService.getStudentPage(
                    null, null, null, null, 1, 10);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getRecords().size());
            assertEquals(1L, result.getData().getTotal());
            assertEquals(10, result.getData().getSize());
            assertEquals(1, result.getData().getCurrent());
        }

        @Test
        @DisplayName("分页查询成功 - 按学生ID筛选")
        void getStudentPage_shouldReturnFilteredResult_byStudentId() {
            when(studentMapper.getStudentPage("S001", null, null, null, 0, 10))
                    .thenReturn(Arrays.asList(testStudent));
            when(studentMapper.getStudentCount("S001", null, null, null))
                    .thenReturn(1L);

            Result<PageResult<Student>> result = studentService.getStudentPage(
                    "S001", null, null, null, 1, 10);

            assertEquals(200, result.getCode());
            assertEquals(1, result.getData().getRecords().size());
            assertEquals("S001", result.getData().getRecords().get(0).getStudentId());
        }

        @Test
        @DisplayName("分页查询成功 - 按姓名筛选")
        void getStudentPage_shouldReturnFilteredResult_byStudentName() {
            when(studentMapper.getStudentPage(null, "张", null, null, 0, 10))
                    .thenReturn(Arrays.asList(testStudent));
            when(studentMapper.getStudentCount(null, "张", null, null))
                    .thenReturn(1L);

            Result<PageResult<Student>> result = studentService.getStudentPage(
                    null, "张", null, null, 1, 10);

            assertEquals(200, result.getCode());
            assertEquals(1, result.getData().getRecords().size());
        }

        @Test
        @DisplayName("分页查询成功 - 按性别筛选")
        void getStudentPage_shouldReturnFilteredResult_byGender() {
            when(studentMapper.getStudentPage(null, null, "男", null, 0, 10))
                    .thenReturn(Arrays.asList(testStudent));
            when(studentMapper.getStudentCount(null, null, "男", null))
                    .thenReturn(1L);

            Result<PageResult<Student>> result = studentService.getStudentPage(
                    null, null, "男", null, 1, 10);

            assertEquals(200, result.getCode());
            assertEquals("男", result.getData().getRecords().get(0).getGender());
        }

        @Test
        @DisplayName("分页查询成功 - 按省份筛选")
        void getStudentPage_shouldReturnFilteredResult_byProvinceId() {
            when(studentMapper.getStudentPage(null, null, null, 11, 0, 10))
                    .thenReturn(Arrays.asList(testStudent));
            when(studentMapper.getStudentCount(null, null, null, 11))
                    .thenReturn(1L);

            Result<PageResult<Student>> result = studentService.getStudentPage(
                    null, null, null, 11, 1, 10);

            assertEquals(200, result.getCode());
            assertEquals(11, result.getData().getRecords().get(0).getProvinceId());
        }

        @Test
        @DisplayName("分页查询 - 空结果")
        void getStudentPage_shouldReturnEmptyList_whenNoMatch() {
            when(studentMapper.getStudentPage(any(), any(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(Collections.emptyList());
            when(studentMapper.getStudentCount(any(), any(), any(), any()))
                    .thenReturn(0L);

            Result<PageResult<Student>> result = studentService.getStudentPage(
                    "NOTEXIST", null, null, null, 1, 10);

            assertEquals(200, result.getCode());
            assertEquals(0, result.getData().getRecords().size());
            assertEquals(0L, result.getData().getTotal());
        }

        @Test
        @DisplayName("分页查询 - 正确计算偏移量")
        void getStudentPage_shouldCalculateOffsetCorrectly() {
            when(studentMapper.getStudentPage(null, null, null, null, 20, 10))
                    .thenReturn(Collections.emptyList());
            when(studentMapper.getStudentCount(null, null, null, null))
                    .thenReturn(25L);

            Result<PageResult<Student>> result = studentService.getStudentPage(
                    null, null, null, null, 3, 10);

            assertEquals(200, result.getCode());
            verify(studentMapper).getStudentPage(null, null, null, null, 20, 10);
        }
    }

    // ==================== 获取学生详情测试 ====================
    @Nested
    @DisplayName("获取学生详情测试")
    class GetStudentDetailTests {

        @Test
        @DisplayName("获取学生详情成功")
        void getStudentDetail_shouldReturnStudent_whenExists() {
            when(studentMapper.getStudentById("S001")).thenReturn(testStudent);

            Result<Student> result = studentService.getStudentDetail("S001");

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals("S001", result.getData().getStudentId());
            assertEquals("张三", result.getData().getStudentName());
            assertEquals(650, result.getData().getCollegeEntranceExamScore());
        }

        @Test
        @DisplayName("获取学生详情失败 - 学生不存在")
        void getStudentDetail_shouldReturnError_whenNotExists() {
            when(studentMapper.getStudentById("S999")).thenReturn(null);

            Result<Student> result = studentService.getStudentDetail("S999");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("学生不存在"));
        }
    }

    // ==================== 保存学生测试 ====================
    @Nested
    @DisplayName("保存学生信息测试")
    class SaveStudentTests {

        @Test
        @DisplayName("新增学生成功")
        void saveStudent_shouldInsert_whenStudentIdEmpty() {
            Student newStudent = new Student();
            newStudent.setStudentName("李四");
            newStudent.setGender("女");
            newStudent.setCollegeEntranceExamScore(680);

            when(studentMapper.insertStudent(any(Student.class))).thenReturn(1);

            Result<Void> result = studentService.saveStudent(newStudent);

            assertEquals(200, result.getCode());
            verify(studentMapper).insertStudent(newStudent);
            verify(studentMapper, never()).updateStudent(any());
        }

        @Test
        @DisplayName("新增学生成功 - studentId为null")
        void saveStudent_shouldInsert_whenStudentIdNull() {
            Student newStudent = new Student();
            newStudent.setStudentId(null);
            newStudent.setStudentName("王五");

            when(studentMapper.insertStudent(any(Student.class))).thenReturn(1);

            Result<Void> result = studentService.saveStudent(newStudent);

            assertEquals(200, result.getCode());
            verify(studentMapper).insertStudent(newStudent);
        }

        @Test
        @DisplayName("更新学生成功")
        void saveStudent_shouldUpdate_whenStudentIdExists() {
            testStudent.setStudentName("张三更新");

            when(studentMapper.updateStudent(any(Student.class))).thenReturn(1);

            Result<Void> result = studentService.saveStudent(testStudent);

            assertEquals(200, result.getCode());
            verify(studentMapper).updateStudent(testStudent);
            verify(studentMapper, never()).insertStudent(any());
        }

        @Test
        @DisplayName("保存学生失败 - 数据库操作失败")
        void saveStudent_shouldFail_whenDatabaseError() {
            when(studentMapper.insertStudent(any(Student.class))).thenReturn(0);

            Student newStudent = new Student();
            newStudent.setStudentName("测试");

            Result<Void> result = studentService.saveStudent(newStudent);

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("操作失败"));
        }
    }

    // ==================== 删除学生测试 ====================
    @Nested
    @DisplayName("删除学生测试")
    class DeleteStudentTests {

        @Test
        @DisplayName("删除单个学生成功")
        void deleteStudent_shouldSucceed_withSingleId() {
            when(studentMapper.deleteStudents(anyList())).thenReturn(1);

            Result<Void> result = studentService.deleteStudent("S001");

            assertEquals(200, result.getCode());
            assertTrue(result.getMessage().contains("删除成功"));
            verify(studentMapper).deleteStudents(Arrays.asList("S001"));
        }

        @Test
        @DisplayName("批量删除学生成功")
        void deleteStudent_shouldSucceed_withMultipleIds() {
            when(studentMapper.deleteStudents(anyList())).thenReturn(3);

            Result<Void> result = studentService.deleteStudent("S001,S002,S003");

            assertEquals(200, result.getCode());
            verify(studentMapper).deleteStudents(Arrays.asList("S001", "S002", "S003"));
        }

        @Test
        @DisplayName("删除学生失败 - 无匹配记录")
        void deleteStudent_shouldFail_whenNoRecordsDeleted() {
            when(studentMapper.deleteStudents(anyList())).thenReturn(0);

            Result<Void> result = studentService.deleteStudent("S999");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("删除失败"));
        }
    }
}
