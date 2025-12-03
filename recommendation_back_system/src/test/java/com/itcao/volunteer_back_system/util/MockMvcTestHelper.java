package com.itcao.volunteer_back_system.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itcao.volunteer_back_system.pojo.User;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * MockMvc 测试辅助工具类
 * 
 * 提供常用的测试操作封装
 */
public class MockMvcTestHelper {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public MockMvcTestHelper(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    /**
     * 执行登录操作并返回带有用户信息的 Session
     *
     * @param username 用户名
     * @param password 密码
     * @return 包含用户信息的 MockHttpSession
     */
    public MockHttpSession performLogin(String username, String password) throws Exception {
        MockHttpSession session = new MockHttpSession();

        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("username", username);
        loginInfo.put("password", password);

        mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginInfo))
                .session(session));

        return session;
    }

    /**
     * 创建一个已登录的 Session（模拟登录状态）
     *
     * @param user 用户对象
     * @return 包含用户信息的 MockHttpSession
     */
    public static MockHttpSession createLoggedInSession(User user) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        return session;
    }

    /**
     * 创建管理员登录 Session
     */
    public static MockHttpSession createAdminSession() {
        return createLoggedInSession(TestDataBuilder.UserBuilder.createAdmin());
    }

    /**
     * 创建学生登录 Session
     */
    public static MockHttpSession createStudentSession(String studentId) {
        return createLoggedInSession(TestDataBuilder.UserBuilder.createStudent(studentId));
    }

    /**
     * 从 MvcResult 中提取响应 JSON 中的指定字段
     */
    public <T> T extractFromResponse(MvcResult result, String jsonPath, Class<T> type) throws Exception {
        String content = result.getResponse().getContentAsString();
        // 简单实现，实际可使用 JsonPath 库
        return objectMapper.readValue(content, type);
    }

    /**
     * 将对象转换为 JSON 字符串
     */
    public String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
