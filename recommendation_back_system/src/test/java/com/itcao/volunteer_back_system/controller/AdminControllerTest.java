package com.itcao.volunteer_back_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itcao.volunteer_back_system.common.Result;
import com.itcao.volunteer_back_system.pojo.User;
import com.itcao.volunteer_back_system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AdminController 控制器层测试
 * 使用 MockMvc 进行 HTTP 请求模拟测试
 */
@WebMvcTest(AdminController.class)
@DisplayName("AdminController 控制器测试")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User testUser;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setRole("admin");
        testUser.setStatus(1);

        session = new MockHttpSession();
    }

    // ==================== 登录接口测试 ====================
    @Nested
    @DisplayName("登录接口测试")
    class LoginTests {

        @Test
        @DisplayName("登录成功")
        void login_shouldReturnSuccess_whenCredentialsCorrect() throws Exception {
            when(userService.login("testuser", "password123"))
                    .thenReturn(Result.success("登录成功", testUser));

            Map<String, String> loginInfo = new HashMap<>();
            loginInfo.put("username", "testuser");
            loginInfo.put("password", "password123");

            mockMvc.perform(post("/admin/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginInfo)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.username").value("testuser"))
                    .andExpect(jsonPath("$.data.role").value("admin"));
        }

        @Test
        @DisplayName("登录失败 - 用户名或密码错误")
        void login_shouldReturnError_whenCredentialsIncorrect() throws Exception {
            when(userService.login("testuser", "wrongpassword"))
                    .thenReturn(Result.error("用户名或密码错误"));

            Map<String, String> loginInfo = new HashMap<>();
            loginInfo.put("username", "testuser");
            loginInfo.put("password", "wrongpassword");

            mockMvc.perform(post("/admin/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginInfo)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("用户名或密码错误"));
        }
    }

    // ==================== 登出接口测试 ====================
    @Nested
    @DisplayName("登出接口测试")
    class LogoutTests {

        @Test
        @DisplayName("登出成功")
        void logout_shouldReturnSuccess() throws Exception {
            session.setAttribute("user", testUser);

            mockMvc.perform(post("/admin/logout")
                    .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("登出成功"));
        }
    }

    // ==================== 获取用户信息接口测试 ====================
    @Nested
    @DisplayName("获取用户信息接口测试")
    class GetUserInfoTests {

        @Test
        @DisplayName("获取用户信息成功 - 已登录")
        void getUserInfo_shouldReturnUser_whenLoggedIn() throws Exception {
            session.setAttribute("user", testUser);
            when(userService.getUserInfo(1)).thenReturn(Result.success(testUser));

            mockMvc.perform(get("/admin/info")
                    .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.username").value("testuser"));
        }

        @Test
        @DisplayName("获取用户信息失败 - 未登录")
        void getUserInfo_shouldReturnError_whenNotLoggedIn() throws Exception {
            mockMvc.perform(get("/admin/info")
                    .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("未登录"));
        }
    }

    // ==================== 会话状态接口测试 ====================
    @Nested
    @DisplayName("会话状态接口测试")
    class SessionStatusTests {

        @Test
        @DisplayName("获取会话状态 - 已登录")
        void getSessionStatus_shouldReturnUserInfo_whenLoggedIn() throws Exception {
            session.setAttribute("user", testUser);

            mockMvc.perform(get("/admin/session-status")
                    .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.sessionId").exists())
                    .andExpect(jsonPath("$.data.user.username").value("testuser"));
        }

        @Test
        @DisplayName("获取会话状态 - 未登录")
        void getSessionStatus_shouldReturnNullUser_whenNotLoggedIn() throws Exception {
            mockMvc.perform(get("/admin/session-status")
                    .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.sessionId").exists())
                    .andExpect(jsonPath("$.data.user").isEmpty());
        }
    }
}
