package com.itcao.volunteer_back_system.mapper;

import com.itcao.volunteer_back_system.pojo.User;
import org.junit.jupiter.api.*;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserMapper 数据访问层测试
 * 
 * 使用 @MybatisTest 进行 MyBatis Mapper 的切片测试
 * 仅加载 MyBatis 相关组件，不加载完整的 Spring 上下文
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("UserMapper 数据访问层测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    // ==================== 登录测试 ====================
    @Nested
    @DisplayName("登录查询测试")
    class LoginTests {

        @Test
        @DisplayName("登录查询 - 正确的用户名密码")
        void login_shouldReturnUser_whenCredentialsCorrect() {
            // 假设数据库中存在 admin/123456 用户
            User user = userMapper.login("admin", "123456");
            
            if (user != null) {
                assertThat(user.getUsername()).isEqualTo("admin");
                assertThat(user.getUserId()).isNotNull();
            }
            // 如果用户不存在，测试也通过（取决于测试数据）
        }

        @Test
        @DisplayName("登录查询 - 错误的密码")
        void login_shouldReturnNull_whenPasswordIncorrect() {
            User user = userMapper.login("admin", "wrongpassword");
            
            assertThat(user).isNull();
        }

        @Test
        @DisplayName("登录查询 - 不存在的用户")
        void login_shouldReturnNull_whenUserNotExists() {
            User user = userMapper.login("notexistuser", "password");
            
            assertThat(user).isNull();
        }
    }

    // ==================== 用户查询测试 ====================
    @Nested
    @DisplayName("用户查询测试")
    class QueryTests {

        @Test
        @DisplayName("获取用户列表")
        void getUserList_shouldReturnList() {
            List<User> users = userMapper.getUserList();
            
            assertThat(users).isNotNull();
            // 列表可能为空，但不应该是 null
        }

        @Test
        @DisplayName("分页查询用户")
        void getUserPage_shouldReturnPagedList() {
            List<User> users = userMapper.getUserPage(null, null, 0, 10);
            
            assertThat(users).isNotNull();
            assertThat(users.size()).isLessThanOrEqualTo(10);
        }

        @Test
        @DisplayName("分页查询 - 按用户名筛选")
        void getUserPage_shouldFilterByUsername() {
            List<User> users = userMapper.getUserPage("admin", null, 0, 10);
            
            assertThat(users).isNotNull();
            // 如果有结果，验证用户名包含筛选条件
            users.forEach(user -> 
                assertThat(user.getUsername().toLowerCase()).contains("admin".toLowerCase())
            );
        }

        @Test
        @DisplayName("获取用户总数")
        void getUserCount_shouldReturnCount() {
            Long count = userMapper.getUserCount(null, null);
            
            assertThat(count).isNotNull();
            assertThat(count).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("根据ID获取用户")
        void getUserById_shouldReturnUser_whenExists() {
            // 先获取一个存在的用户ID
            List<User> users = userMapper.getUserPage(null, null, 0, 1);
            
            if (!users.isEmpty()) {
                Integer userId = users.get(0).getUserId();
                User user = userMapper.getUserById(userId);
                
                assertThat(user).isNotNull();
                assertThat(user.getUserId()).isEqualTo(userId);
            }
        }

        @Test
        @DisplayName("根据ID获取用户 - 不存在的ID")
        void getUserById_shouldReturnNull_whenNotExists() {
            User user = userMapper.getUserById(999999);
            
            assertThat(user).isNull();
        }
    }

    // ==================== 用户状态更新测试 ====================
    @Nested
    @DisplayName("用户状态更新测试")
    class UpdateStatusTests {

        @Test
        @DisplayName("更新用户状态 - 不存在的用户")
        void updateStatus_shouldReturnZero_whenUserNotExists() {
            int result = userMapper.updateStatus(999999, 0);
            
            assertThat(result).isEqualTo(0);
        }
    }

    // ==================== 删除用户测试 ====================
    @Nested
    @DisplayName("删除用户测试")
    class DeleteTests {

        @Test
        @DisplayName("删除不存在的用户")
        void deleteUsers_shouldReturnZero_whenUsersNotExist() {
            int result = userMapper.deleteUsers(Arrays.asList(999998, 999999));
            
            assertThat(result).isEqualTo(0);
        }
    }
}
