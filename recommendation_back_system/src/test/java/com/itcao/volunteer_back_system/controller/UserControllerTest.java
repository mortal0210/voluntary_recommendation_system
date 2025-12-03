package com.itcao.volunteer_back_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itcao.volunteer_back_system.Dto.UserProfileDTO;
import com.itcao.volunteer_back_system.common.PageResult;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController 控制器层测试
 */
@WebMvcTest(UserController.class)
@DisplayName("UserController 控制器测试")
class UserControllerTest {

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
        testUser.setRole("student");
        testUser.setStudentId("S001");
        testUser.setStatus(1);

        session = new MockHttpSession();
    }

    // ==================== 获取当前学生ID测试 ====================
    @Nested
    @DisplayName("获取当前学生ID测试")
    class GetCurrentStudentIdTests {

        @Test
        @DisplayName("获取学生ID成功 - 已登录学生用户")
        void getCurrentStudentId_shouldReturnStudentId_whenLoggedIn() throws Exception {
            session.setAttribute("user", testUser);

            mockMvc.perform(get("/user/currentStudentId")
                    .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value(1)); // S001 parsed as Integer
        }

        @Test
        @DisplayName("获取学生ID失败 - 未登录")
        void getCurrentStudentId_shouldReturnError_whenNotLoggedIn() throws Exception {
            mockMvc.perform(get("/user/currentStudentId")
                    .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("用户未登录"));
        }
    }

    // ==================== 获取用户列表测试 ====================
    @Nested
    @DisplayName("获取用户列表测试")
    class GetUserListTests {

        @Test
        @DisplayName("获取用户列表成功")
        void getUserList_shouldReturnList() throws Exception {
            List<User> users = Arrays.asList(testUser);
            when(userService.getUserList()).thenReturn(Result.success(users));

            mockMvc.perform(get("/user/userList"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].username").value("testuser"));
        }
    }

    // ==================== 分页获取用户测试 ====================
    @Nested
    @DisplayName("分页获取用户测试")
    class GetUserPageTests {

        @Test
        @DisplayName("分页获取用户成功 - 默认参数")
        void getUserPage_shouldReturnPageResult_withDefaultParams() throws Exception {
            PageResult<User> pageResult = new PageResult<>(Arrays.asList(testUser), 1L, 10, 1);
            when(userService.getUserPage(null, null, 1, 10))
                    .thenReturn(Result.success(pageResult));

            mockMvc.perform(get("/user/getUserPage"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.records").isArray())
                    .andExpect(jsonPath("$.data.total").value(1));
        }

        @Test
        @DisplayName("分页获取用户成功 - 带筛选条件")
        void getUserPage_shouldReturnFilteredResult() throws Exception {
            PageResult<User> pageResult = new PageResult<>(Arrays.asList(testUser), 1L, 10, 1);
            when(userService.getUserPage("test", "student", 1, 10))
                    .thenReturn(Result.success(pageResult));

            mockMvc.perform(get("/user/getUserPage")
                    .param("username", "test")
                    .param("role", "student")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    // ==================== 获取单个用户测试 ====================
    @Nested
    @DisplayName("获取单个用户测试")
    class GetUserTests {

        @Test
        @DisplayName("获取用户成功")
        void getUser_shouldReturnUser_whenExists() throws Exception {
            when(userService.getUser(1)).thenReturn(Result.success(testUser));

            mockMvc.perform(get("/user/getUser")
                    .param("id", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.userId").value(1))
                    .andExpect(jsonPath("$.data.username").value("testuser"));
        }

        @Test
        @DisplayName("获取用户失败 - 用户不存在")
        void getUser_shouldReturnError_whenNotExists() throws Exception {
            when(userService.getUser(999)).thenReturn(Result.error("用户不存在"));

            mockMvc.perform(get("/user/getUser")
                    .param("id", "999"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("用户不存在"));
        }
    }

    // ==================== 更新用户测试 ====================
    @Nested
    @DisplayName("更新用户测试")
    class UpdateUserTests {

        @Test
        @DisplayName("更新用户成功")
        void updateUser_shouldReturnSuccess() throws Exception {
            when(userService.updateUser(any(User.class))).thenReturn(Result.success());

            mockMvc.perform(post("/user/updateUser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    // ==================== 新增用户测试 ====================
    @Nested
    @DisplayName("新增用户测试")
    class AddUserTests {

        @Test
        @DisplayName("新增用户成功")
        void addUser_shouldReturnSuccess() throws Exception {
            when(userService.addUser(any(User.class))).thenReturn(Result.success());

            User newUser = new User();
            newUser.setUsername("newuser");
            newUser.setPassword("password");
            newUser.setRole("admin");

            mockMvc.perform(post("/user/addUser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    // ==================== 删除用户测试 ====================
    @Nested
    @DisplayName("删除用户测试")
    class DeleteUserTests {

        @Test
        @DisplayName("删除单个用户成功")
        void deleteUser_shouldReturnSuccess_withSingleId() throws Exception {
            when(userService.deleteUser("1")).thenReturn(Result.success("删除成功"));

            mockMvc.perform(delete("/user/deleteUser/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("批量删除用户成功")
        void deleteUser_shouldReturnSuccess_withMultipleIds() throws Exception {
            when(userService.deleteUser("1,2,3")).thenReturn(Result.success("删除成功"));

            mockMvc.perform(delete("/user/deleteUser/1,2,3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    // ==================== 更新状态测试 ====================
    @Nested
    @DisplayName("更新用户状态测试")
    class UpdateStatusTests {

        @Test
        @DisplayName("更新状态成功")
        void updateStatus_shouldReturnSuccess() throws Exception {
            when(userService.updateStatus(1, 0)).thenReturn(Result.success());

            Map<String, Integer> params = new HashMap<>();
            params.put("userId", 1);
            params.put("status", 0);

            mockMvc.perform(post("/user/updateStatus")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    // ==================== 获取个人信息测试 ====================
    @Nested
    @DisplayName("获取个人信息测试")
    class GetProfileTests {

        @Test
        @DisplayName("获取个人信息成功")
        void getProfile_shouldReturnProfile_whenLoggedIn() throws Exception {
            session.setAttribute("user", testUser);

            UserProfileDTO profileDTO = new UserProfileDTO();
            profileDTO.setUserId(1);
            profileDTO.setUsername("testuser");
            profileDTO.setRole("student");
            profileDTO.setRoleLabel("学生");

            when(userService.getProfile(1)).thenReturn(Result.success(profileDTO));

            mockMvc.perform(get("/user/profile")
                    .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.username").value("testuser"));
        }

        @Test
        @DisplayName("获取个人信息失败 - 未登录")
        void getProfile_shouldReturnError_whenNotLoggedIn() throws Exception {
            mockMvc.perform(get("/user/profile")
                    .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("登录已过期，请重新登录"));
        }
    }
}
