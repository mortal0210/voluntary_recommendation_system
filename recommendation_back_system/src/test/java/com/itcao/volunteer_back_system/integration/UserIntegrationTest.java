package com.itcao.volunteer_back_system.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itcao.volunteer_back_system.pojo.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户模块集成测试
 * 
 * 测试完整的请求-响应流程，包括：
 * - Controller -> Service -> Mapper -> Database
 * 
 * 注意：需要配置测试数据库或使用 @Transactional 回滚测试数据
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("用户模块集成测试")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
    }

    // ==================== 登录流程集成测试 ====================
    @Nested
    @DisplayName("登录流程集成测试")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class LoginFlowTests {

        @Test
        @Order(1)
        @DisplayName("完整登录流程 - 登录 -> 获取信息 -> 登出")
        void fullLoginFlow() throws Exception {
            // 1. 登录
            Map<String, String> loginInfo = new HashMap<>();
            loginInfo.put("username", "admin");
            loginInfo.put("password", "123456");

            MvcResult loginResult = mockMvc.perform(post("/admin/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginInfo))
                    .session(session))
                    .andExpect(status().isOk())
                    .andReturn();

            // 检查登录结果
            String loginResponse = loginResult.getResponse().getContentAsString();
            System.out.println("登录响应: " + loginResponse);

            // 2. 获取用户信息（使用同一个session）
            mockMvc.perform(get("/admin/info")
                    .session(session))
                    .andExpect(status().isOk());

            // 3. 获取会话状态
            mockMvc.perform(get("/admin/session-status")
                    .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.sessionId").exists());

            // 4. 登出
            mockMvc.perform(post("/admin/logout")
                    .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("登出成功"));
        }
    }

    // ==================== 用户管理集成测试 ====================
    @Nested
    @DisplayName("用户管理集成测试")
    @Transactional // 测试后回滚
    class UserManagementTests {

        @Test
        @DisplayName("分页查询用户")
        void getUserPage_shouldReturnPagedUsers() throws Exception {
            mockMvc.perform(get("/user/getUserPage")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.records").isArray());
        }

        @Test
        @DisplayName("获取用户列表")
        void getUserList_shouldReturnAllUsers() throws Exception {
            mockMvc.perform(get("/user/userList"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    // ==================== 会话管理集成测试 ====================
    @Nested
    @DisplayName("会话管理集成测试")
    class SessionManagementTests {

        @Test
        @DisplayName("未登录状态获取用户信息应返回错误")
        void getUserInfo_shouldReturnError_whenNotLoggedIn() throws Exception {
            mockMvc.perform(get("/admin/info"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("未登录"));
        }

        @Test
        @DisplayName("未登录状态获取个人信息应返回401")
        void getProfile_shouldReturn401_whenNotLoggedIn() throws Exception {
            mockMvc.perform(get("/user/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("未登录状态获取学生ID应返回401")
        void getCurrentStudentId_shouldReturn401_whenNotLoggedIn() throws Exception {
            mockMvc.perform(get("/user/currentStudentId"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }
}
