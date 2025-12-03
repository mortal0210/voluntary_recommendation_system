# 志愿推荐系统 - 测试文档

## 测试架构概述

本项目采用分层测试策略，包含以下测试类型：

### 1. 单元测试 (Unit Tests)

位置：`src/test/java/com/itcao/volunteer_back_system/service/`

| 测试类 | 被测类 | 说明 |
|--------|--------|------|
| `UserServiceImplTest` | `UserServiceImpl` | 用户服务单元测试 |
| `StudentServiceImplTest` | `StudentServiceImpl` | 学生服务单元测试 |
| `RecommendationServiceImplTest` | `RecommendationServiceImpl` | 推荐服务单元测试 |

**特点：**

- 使用 Mockito 模拟 Mapper 层依赖
- 不启动 Spring 容器，执行速度快
- 覆盖业务逻辑的各种分支

### 2. 控制器测试 (Controller Tests)

位置：`src/test/java/com/itcao/volunteer_back_system/controller/`

| 测试类 | 被测类 | 说明 |
|--------|--------|------|
| `AdminControllerTest` | `AdminController` | 管理员控制器测试 |
| `UserControllerTest` | `UserController` | 用户控制器测试 |
| `RecommendationControllerTest` | `RecommendationController` | 推荐控制器测试 |

**特点：**
- 使用 `@WebMvcTest` 进行切片测试
- 使用 MockMvc 模拟 HTTP 请求
- 验证请求路由、参数绑定、响应格式

### 3. 数据访问层测试 (Mapper Tests)

位置：`src/test/java/com/itcao/volunteer_back_system/mapper/`

| 测试类 | 被测类 | 说明 |
|--------|--------|------|
| `UserMapperTest` | `UserMapper` | 用户 Mapper 测试 |
| `StudentMapperTest` | `StudentMapper` | 学生 Mapper 测试 |

**特点：**
- 使用 `@MybatisTest` 进行 MyBatis 切片测试
- 连接真实数据库验证 SQL 执行
- 使用 AssertJ 进行流畅断言

### 4. 集成测试 (Integration Tests)

位置：`src/test/java/com/itcao/volunteer_back_system/integration/`

| 测试类 | 说明 |
|--------|------|
| `UserIntegrationTest` | 用户模块端到端测试 |
| `RecommendationIntegrationTest` | 推荐模块端到端测试 |

**特点：**
- 使用 `@SpringBootTest` 启动完整应用
- 测试完整的请求-响应流程
- 使用 `@Transactional` 回滚测试数据

## 运行测试

### 运行所有测试
```bash
mvn test
```

### 运行特定测试类
```bash
mvn test -Dtest=UserServiceImplTest
```

### 运行特定测试方法
```bash
mvn test -Dtest=UserServiceImplTest#login_shouldReturnSuccess_whenCredentialsCorrect
```

### 运行特定包下的测试
```bash
mvn test -Dtest="com.itcao.volunteer_back_system.service.*"
```

### 生成测试报告
```bash
mvn surefire-report:report
```

## 测试配置

### 测试环境配置文件
`src/test/resources/application-test.properties`

### 激活测试配置
在测试类上添加 `@ActiveProfiles("test")`

## 测试工具类

### TestDataBuilder
位置：`src/test/java/com/itcao/volunteer_back_system/util/TestDataBuilder.java`

提供测试数据的快速构建：
```java
// 创建用户
User admin = TestDataBuilder.UserBuilder.createAdmin();
User student = TestDataBuilder.UserBuilder.createStudent("S001");

// 使用 Builder 模式
User customUser = TestDataBuilder.user()
    .userId(1)
    .username("custom")
    .role("admin")
    .build();

// 创建学生
Student highScore = TestDataBuilder.StudentBuilder.createHighScoreStudent();
```

### MockMvcTestHelper

位置：`src/test/java/com/itcao/volunteer_back_system/util/MockMvcTestHelper.java`

提供 MockMvc 测试的辅助方法：
```java
// 创建已登录的 Session
MockHttpSession session = MockMvcTestHelper.createAdminSession();
MockHttpSession studentSession = MockMvcTestHelper.createStudentSession("S001");
```

## 测试命名规范

采用 `方法名_场景_期望结果` 的命名风格：
- `login_shouldReturnSuccess_whenCredentialsCorrect`
- `getUserPage_shouldReturnEmptyList_whenNoMatch`
- `deleteUser_shouldFail_whenNoRecordsDeleted`

## 测试覆盖的功能模块

### 用户管理

- [x] 用户登录/登出
- [x] 用户信息获取
- [x] 用户分页查询
- [x] 用户新增/更新/删除
- [x] 用户状态管理
- [x] 个人中心信息

### 学生管理
- [x] 学生分页查询
- [x] 学生详情获取
- [x] 学生新增/更新/删除

### 智能推荐
- [x] 志愿推荐
- [x] 冲突检测
- [x] 综合分析
- [x] 院校专业匹配
- [x] 录取概率预测
- [x] 就业前景分析
- [x] 省份分数线分析

## 注意事项

1. **数据库依赖**：集成测试和 Mapper 测试需要连接数据库
2. **测试数据**：使用 `@Transactional` 确保测试数据回滚
3. **并行执行**：单元测试可并行执行，集成测试建议串行
4. **敏感数据**：测试配置中的数据库密码应使用环境变量
