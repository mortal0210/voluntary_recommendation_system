package com.itcao.volunteer_back_system.service.impl;

import com.itcao.volunteer_back_system.Dto.UserProfileDTO;
import com.itcao.volunteer_back_system.common.PageResult;
import com.itcao.volunteer_back_system.common.Result;
import com.itcao.volunteer_back_system.mapper.StudentMapper;
import com.itcao.volunteer_back_system.mapper.UserMapper;
import com.itcao.volunteer_back_system.pojo.Student;
import com.itcao.volunteer_back_system.pojo.User;
import com.itcao.volunteer_back_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentMapper studentMapper;

    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    @Override
    public Result<User> login(String username, String password) {
        User user = userMapper.login(username, password);
        if (user != null) {
            return Result.success("登录成功", user);
        }
        return Result.error("用户名或密码错误");
    }

    /**
     * 获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public Result<User> getUserInfo(Integer userId) {
        User user = userMapper.getUserById(userId);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("获取用户信息失败");
    }

    /**
     * 获取所有用户列表
     * 
     * @return 用户列表
     */
    @Override
    public Result<List<User>> getUserList() {
        List<User> userList = userMapper.getUserList();
        return Result.success(userList);
    }

    /**
     * 分页获取用户列表
     * 
     * @param username 用户名（模糊查询）
     * @param role     角色
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @return 用户分页列表
     */
    @Override
    public Result<PageResult<User>> getUserPage(String username, String role, Integer pageNum, Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        List<User> users = userMapper.getUserPage(username, role, offset, pageSize);
        Long total = userMapper.getUserCount(username, role);
        PageResult<User> pageResult = new PageResult<>(users, total, pageSize, pageNum);
        return Result.success(pageResult);
    }

    /**
     * 获取单个用户信息
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    @Override
    public Result<User> getUser(Integer id) {
        User user = userMapper.getUserById(id);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    /**
     * 新增或更新用户信息
     * 
     * @param user 用户信息
     * @return 操作结果
     */
    @Override
    public Result<Void> updateUser(User user) {
        if (user == null) {
            return Result.error("用户信息不能为空");
        }

        if (user.getUserId() == null) {
            // 走新增逻辑
            return addUser(user);
        }

        // 只有在请求中传递了角色或学生ID时才需要重新校验
        if (user.getRole() != null || user.getStudentId() != null) {
            String validationError = validateAndNormalizeStudentBinding(user);
            if (validationError != null) {
                return Result.error(validationError);
            }
        }

        int result;

        // 更新用户密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            userMapper.updatePassword(user);
        }

        result = userMapper.updateUser(user);

        if (result > 0) {
            return Result.success();
        }
        return Result.error("操作失败");
    }

    /**
     * 删除用户
     * 
     * @param ids 用户ID列表，逗号分隔
     * @return 操作结果
     */
    @Override
    public Result<Void> deleteUser(String ids) {
        List<Integer> idList = Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        int result = userMapper.deleteUsers(idList);
        if (result > 0) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 更新用户状态
     * 
     * @param userId 用户ID
     * @param status 状态
     * @return 操作结果
     */
    @Override
    public Result<Void> updateStatus(Integer userId, Integer status) {
        int result = userMapper.updateStatus(userId, status);
        if (result > 0) {
            return Result.success();
        }
        return Result.error("更新状态失败");
    }

    @Override
    public Result<Void> addUser(User user) {
        if (user == null) {
            return Result.error("用户信息不能为空");
        }

        String validationError = validateAndNormalizeStudentBinding(user);
        if (validationError != null) {
            return Result.error(validationError);
        }

        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        int rows = userMapper.insertUser(user);
        if (rows > 0) {
            return Result.success();
        }
        return Result.error("新增用户失败");
    }

    private String validateAndNormalizeStudentBinding(User user) {
        String role = user.getRole();
        if (isBlank(role)) {
            return "用户角色不能为空";
        }

        if ("student".equalsIgnoreCase(role.trim())) {
            String studentId = user.getStudentId();
            if (isBlank(studentId)) {
                return "学生用户必须绑定学生ID";
            }

            Student student = studentMapper.getStudentById(studentId.trim());
            if (student == null) {
                return "学生ID不存在，请先在学生管理中创建该学生";
            }

            user.setStudentId(studentId.trim());
        } else {
            // 其他角色不绑定学生
            user.setStudentId(null);
        }

        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public Result<UserProfileDTO> getProfile(Integer userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        User user = userMapper.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setUserId(user.getUserId());
        profileDTO.setUsername(user.getUsername());
        profileDTO.setRole(user.getRole());
        profileDTO.setRoleLabel(buildRoleLabel(user.getRole()));
        profileDTO.setStudentId(user.getStudentId());
        profileDTO.setStatus(user.getStatus());
        profileDTO.setCreateTime(user.getCreateTime());
        profileDTO.setLastLoginTime(null);
        profileDTO.setPhone(null);
        profileDTO.setLastAction(null);
        profileDTO.setDepartment(buildDepartment(user.getRole()));

        return Result.success(profileDTO);
    }

    private String buildRoleLabel(String role) {
        if (isBlank(role)) {
            return "未知角色";
        }
        switch (role) {
            case "admin":
                return "管理员";
            case "student":
                return "学生";
            default:
                return "普通用户";
        }
    }

    private String buildDepartment(String role) {
        if ("admin".equalsIgnoreCase(role)) {
            return "招生办";
        }
        if ("student".equalsIgnoreCase(role)) {
            return "考生";
        }
        return null;
    }
}