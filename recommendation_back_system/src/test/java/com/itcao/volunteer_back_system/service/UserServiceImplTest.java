package com.itcao.volunteer_back_system.service;

import com.itcao.volunteer_back_system.common.PageResult;
import com.itcao.volunteer_back_system.common.Result;
import com.itcao.volunteer_back_system.Dto.UserProfileDTO;
import com.itcao.volunteer_back_system.mapper.StudentMapper;
import com.itcao.volunteer_back_system.mapper.UserMapper;
import com.itcao.volunteer_back_system.pojo.Student;
import com.itcao.volunteer_back_system.pojo.User;
import com.itcao.volunteer_back_system.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserServiceImpl 单元测试
 * 
 * 测试策略：
 * - 使用 Mockito 模拟 Mapper 层依赖
 * - 覆盖正常流程和边界条件
 * - 测试方法命名：方法名_场景_期望结果
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl 单元测试")
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setRole("student");
        testUser.setStudentId("S001");
        testUser.setStatus(1);

        testStudent = new Student();
        testStudent.setStudentId("S001");
        testStudent.setStudentName("张三");
    }

    // ==================== 登录测试 ====================
    @Nested
    @DisplayName("登录功能测试")
    class LoginTests {

        @Test
        @DisplayName("登录成功 - 用户名密码正确")
        void login_shouldReturnSuccess_whenCredentialsCorrect() {
            when(userMapper.login("testuser", "password123")).thenReturn(testUser);

            Result<User> result = userService.login("testuser", "password123");

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals("testuser", result.getData().getUsername());
            verify(userMapper).login("testuser", "password123");
        }

        @Test
        @DisplayName("登录失败 - 用户名或密码错误")
        void login_shouldReturnError_whenCredentialsIncorrect() {
            when(userMapper.login("testuser", "wrongpassword")).thenReturn(null);

            Result<User> result = userService.login("testuser", "wrongpassword");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("用户名或密码错误"));
        }
    }

    // ==================== 获取用户信息测试 ====================
    @Nested
    @DisplayName("获取用户信息测试")
    class GetUserInfoTests {

        @Test
        @DisplayName("获取用户信息成功")
        void getUserInfo_shouldReturnUser_whenUserExists() {
            when(userMapper.getUserById(1)).thenReturn(testUser);

            Result<User> result = userService.getUserInfo(1);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getUserId());
        }

        @Test
        @DisplayName("获取用户信息失败 - 用户不存在")
        void getUserInfo_shouldReturnError_whenUserNotExists() {
            when(userMapper.getUserById(999)).thenReturn(null);

            Result<User> result = userService.getUserInfo(999);

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("获取用户信息失败"));
        }
    }

    // ==================== 分页查询测试 ====================
    @Nested
    @DisplayName("分页查询用户测试")
    class GetUserPageTests {

        @Test
        @DisplayName("分页查询成功 - 无筛选条件")
        void getUserPage_shouldReturnPageResult_withoutFilters() {
            List<User> users = Arrays.asList(testUser);
            when(userMapper.getUserPage(null, null, 0, 10)).thenReturn(users);
            when(userMapper.getUserCount(null, null)).thenReturn(1L);

            Result<PageResult<User>> result = userService.getUserPage(null, null, 1, 10);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getRecords().size());
            assertEquals(1L, result.getData().getTotal());
        }

        @Test
        @DisplayName("分页查询成功 - 带用户名筛选")
        void getUserPage_shouldReturnFilteredResult_withUsernameFilter() {
            List<User> users = Arrays.asList(testUser);
            when(userMapper.getUserPage("test", null, 0, 10)).thenReturn(users);
            when(userMapper.getUserCount("test", null)).thenReturn(1L);

            Result<PageResult<User>> result = userService.getUserPage("test", null, 1, 10);

            assertEquals(200, result.getCode());
            assertEquals(1, result.getData().getRecords().size());
        }

        @Test
        @DisplayName("分页查询 - 第二页")
        void getUserPage_shouldCalculateOffsetCorrectly_forSecondPage() {
            when(userMapper.getUserPage(null, null, 10, 10)).thenReturn(Arrays.asList());
            when(userMapper.getUserCount(null, null)).thenReturn(15L);

            Result<PageResult<User>> result = userService.getUserPage(null, null, 2, 10);

            assertEquals(200, result.getCode());
            verify(userMapper).getUserPage(null, null, 10, 10);
        }
    }

    // ==================== 新增用户测试 ====================
    @Nested
    @DisplayName("新增用户测试")
    class AddUserTests {

        @Test
        @DisplayName("新增管理员用户成功")
        void addUser_shouldSucceed_whenRoleIsAdmin() {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword("admin123");
            adminUser.setRole("admin");

            when(userMapper.insertUser(any(User.class))).thenReturn(1);

            Result<Void> result = userService.addUser(adminUser);

            assertEquals(200, result.getCode());
            assertNull(adminUser.getStudentId()); // 管理员不绑定学生ID
            verify(userMapper).insertUser(adminUser);
        }

        @Test
        @DisplayName("新增学生用户成功 - 学生ID存在")
        void addUser_shouldSucceed_whenStudentExists() {
            User studentUser = new User();
            studentUser.setUsername("student1");
            studentUser.setPassword("pass123");
            studentUser.setRole("student");
            studentUser.setStudentId("S001");

            when(studentMapper.getStudentById("S001")).thenReturn(testStudent);
            when(userMapper.insertUser(any(User.class))).thenReturn(1);

            Result<Void> result = userService.addUser(studentUser);

            assertEquals(200, result.getCode());
            verify(studentMapper).getStudentById("S001");
        }

        @Test
        @DisplayName("新增学生用户失败 - 学生ID不存在")
        void addUser_shouldFail_whenStudentNotExists() {
            User studentUser = new User();
            studentUser.setUsername("student1");
            studentUser.setPassword("pass123");
            studentUser.setRole("student");
            studentUser.setStudentId("S999");

            when(studentMapper.getStudentById("S999")).thenReturn(null);

            Result<Void> result = userService.addUser(studentUser);

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("学生ID不存在"));
            verify(userMapper, never()).insertUser(any());
        }

        @Test
        @DisplayName("新增学生用户失败 - 未绑定学生ID")
        void addUser_shouldFail_whenStudentIdMissing() {
            User studentUser = new User();
            studentUser.setUsername("student1");
            studentUser.setPassword("pass123");
            studentUser.setRole("student");
            // 未设置 studentId

            Result<Void> result = userService.addUser(studentUser);

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("学生用户必须绑定学生ID"));
        }

        @Test
        @DisplayName("新增用户失败 - 用户信息为空")
        void addUser_shouldFail_whenUserIsNull() {
            Result<Void> result = userService.addUser(null);

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("用户信息不能为空"));
        }
    }

    // ==================== 更新用户测试 ====================
    @Nested
    @DisplayName("更新用户测试")
    class UpdateUserTests {

        @Test
        @DisplayName("更新用户成功 - 不修改角色")
        void updateUser_shouldSucceed_withoutRoleChange() {
            User updateUser = new User();
            updateUser.setUserId(1);
            updateUser.setUsername("updatedUser");

            when(userMapper.updateUser(any(User.class))).thenReturn(1);

            Result<Void> result = userService.updateUser(updateUser);

            assertEquals(200, result.getCode());
            verify(userMapper).updateUser(updateUser);
        }

        @Test
        @DisplayName("更新用户成功 - 包含密码修改")
        void updateUser_shouldUpdatePassword_whenPasswordProvided() {
            User updateUser = new User();
            updateUser.setUserId(1);
            updateUser.setPassword("newPassword");

            when(userMapper.updateUser(any(User.class))).thenReturn(1);

            Result<Void> result = userService.updateUser(updateUser);

            assertEquals(200, result.getCode());
            verify(userMapper).updatePassword(updateUser);
            verify(userMapper).updateUser(updateUser);
        }

        @Test
        @DisplayName("更新用户失败 - 用户信息为空")
        void updateUser_shouldFail_whenUserIsNull() {
            Result<Void> result = userService.updateUser(null);

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("用户信息不能为空"));
        }

        @Test
        @DisplayName("更新用户 - userId为空时走新增逻辑")
        void updateUser_shouldCallAddUser_whenUserIdIsNull() {
            User newUser = new User();
            newUser.setUsername("newuser");
            newUser.setRole("admin");

            when(userMapper.insertUser(any(User.class))).thenReturn(1);

            Result<Void> result = userService.updateUser(newUser);

            assertEquals(200, result.getCode());
            verify(userMapper).insertUser(newUser);
        }
    }

    // ==================== 删除用户测试 ====================
    @Nested
    @DisplayName("删除用户测试")
    class DeleteUserTests {

        @Test
        @DisplayName("删除单个用户成功")
        void deleteUser_shouldSucceed_withSingleId() {
            when(userMapper.deleteUsers(anyList())).thenReturn(1);

            Result<Void> result = userService.deleteUser("1");

            assertEquals(200, result.getCode());
            assertTrue(result.getMessage().contains("删除成功"));
        }

        @Test
        @DisplayName("批量删除用户成功")
        void deleteUser_shouldSucceed_withMultipleIds() {
            when(userMapper.deleteUsers(anyList())).thenReturn(3);

            Result<Void> result = userService.deleteUser("1,2,3");

            assertEquals(200, result.getCode());
            verify(userMapper).deleteUsers(Arrays.asList(1, 2, 3));
        }

        @Test
        @DisplayName("删除用户失败 - 无匹配记录")
        void deleteUser_shouldFail_whenNoRecordsDeleted() {
            when(userMapper.deleteUsers(anyList())).thenReturn(0);

            Result<Void> result = userService.deleteUser("999");

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("删除失败"));
        }
    }

    // ==================== 更新状态测试 ====================
    @Nested
    @DisplayName("更新用户状态测试")
    class UpdateStatusTests {

        @Test
        @DisplayName("启用用户成功")
        void updateStatus_shouldSucceed_whenEnabling() {
            when(userMapper.updateStatus(1, 1)).thenReturn(1);

            Result<Void> result = userService.updateStatus(1, 1);

            assertEquals(200, result.getCode());
        }

        @Test
        @DisplayName("禁用用户成功")
        void updateStatus_shouldSucceed_whenDisabling() {
            when(userMapper.updateStatus(1, 0)).thenReturn(1);

            Result<Void> result = userService.updateStatus(1, 0);

            assertEquals(200, result.getCode());
        }

        @Test
        @DisplayName("更新状态失败")
        void updateStatus_shouldFail_whenUpdateFails() {
            when(userMapper.updateStatus(999, 1)).thenReturn(0);

            Result<Void> result = userService.updateStatus(999, 1);

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("更新状态失败"));
        }
    }

    // ==================== 获取个人信息测试 ====================
    @Nested
    @DisplayName("获取个人中心信息测试")
    class GetProfileTests {

        @Test
        @DisplayName("获取个人信息成功 - 管理员")
        void getProfile_shouldReturnAdminProfile() {
            User adminUser = new User();
            adminUser.setUserId(1);
            adminUser.setUsername("admin");
            adminUser.setRole("admin");
            adminUser.setStatus(1);

            when(userMapper.getUserById(1)).thenReturn(adminUser);

            Result<UserProfileDTO> result = userService.getProfile(1);

            assertEquals(200, result.getCode());
            UserProfileDTO profile = result.getData();
            assertNotNull(profile);
            assertEquals("管理员", profile.getRoleLabel());
            assertEquals("招生办", profile.getDepartment());
        }

        @Test
        @DisplayName("获取个人信息成功 - 学生")
        void getProfile_shouldReturnStudentProfile() {
            when(userMapper.getUserById(1)).thenReturn(testUser);

            Result<UserProfileDTO> result = userService.getProfile(1);

            assertEquals(200, result.getCode());
            UserProfileDTO profile = result.getData();
            assertNotNull(profile);
            assertEquals("学生", profile.getRoleLabel());
            assertEquals("考生", profile.getDepartment());
            assertEquals("S001", profile.getStudentId());
        }

        @Test
        @DisplayName("获取个人信息失败 - userId为空")
        void getProfile_shouldFail_whenUserIdIsNull() {
            Result<UserProfileDTO> result = userService.getProfile(null);

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("用户ID不能为空"));
        }

        @Test
        @DisplayName("获取个人信息失败 - 用户不存在")
        void getProfile_shouldFail_whenUserNotExists() {
            when(userMapper.getUserById(999)).thenReturn(null);

            Result<UserProfileDTO> result = userService.getProfile(999);

            assertEquals(500, result.getCode());
            assertTrue(result.getMessage().contains("用户不存在"));
        }
    }
}
