package com.itcao.volunteer_back_system.util;

import com.itcao.volunteer_back_system.pojo.Student;
import com.itcao.volunteer_back_system.pojo.User;

/**
 * 测试数据构建器
 * 
 * 提供便捷的测试数据创建方法，遵循 Builder 模式
 */
public class TestDataBuilder {

    // ==================== User 构建器 ====================
    public static class UserBuilder {
        private Integer userId;
        private String username = "testuser";
        private String password = "password123";
        private String role = "student";
        private String studentId;
        private Integer status = 1;
        private String createTime;

        public UserBuilder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder role(String role) {
            this.role = role;
            return this;
        }

        public UserBuilder studentId(String studentId) {
            this.studentId = studentId;
            return this;
        }

        public UserBuilder status(Integer status) {
            this.status = status;
            return this;
        }

        public UserBuilder createTime(String createTime) {
            this.createTime = createTime;
            return this;
        }

        public User build() {
            User user = new User();
            user.setUserId(userId);
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(role);
            user.setStudentId(studentId);
            user.setStatus(status);
            user.setCreateTime(createTime);
            return user;
        }

        /**
         * 创建管理员用户
         */
        public static User createAdmin() {
            return new UserBuilder()
                    .userId(1)
                    .username("admin")
                    .password("123456")
                    .role("admin")
                    .status(1)
                    .build();
        }

        /**
         * 创建学生用户
         */
        public static User createStudent(String studentId) {
            return new UserBuilder()
                    .userId(2)
                    .username("student_" + studentId)
                    .password("123456")
                    .role("student")
                    .studentId(studentId)
                    .status(1)
                    .build();
        }
    }

    // ==================== Student 构建器 ====================
    public static class StudentBuilder {
        private String studentId = "S001";
        private String studentName = "测试学生";
        private String idCard = "110101200001011234";
        private String gender = "男";
        private float collegeEntranceExamScore = 600;
        private int provinceId = 11;
        private int ranking = 10000;
        private String createTime;
        private String provinceName = "北京";

        public StudentBuilder studentId(String studentId) {
            this.studentId = studentId;
            return this;
        }

        public StudentBuilder studentName(String studentName) {
            this.studentName = studentName;
            return this;
        }

        public StudentBuilder idCard(String idCard) {
            this.idCard = idCard;
            return this;
        }

        public StudentBuilder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public StudentBuilder score(float score) {
            this.collegeEntranceExamScore = score;
            return this;
        }

        public StudentBuilder provinceId(int provinceId) {
            this.provinceId = provinceId;
            return this;
        }

        public StudentBuilder ranking(int ranking) {
            this.ranking = ranking;
            return this;
        }

        public StudentBuilder provinceName(String provinceName) {
            this.provinceName = provinceName;
            return this;
        }

        public Student build() {
            Student student = new Student();
            student.setStudentId(studentId);
            student.setStudentName(studentName);
            student.setIdCard(idCard);
            student.setGender(gender);
            student.setCollegeEntranceExamScore(collegeEntranceExamScore);
            student.setProvinceId(provinceId);
            student.setRanking(ranking);
            student.setCreateTime(createTime);
            student.setProvinceName(provinceName);
            return student;
        }

        /**
         * 创建高分学生
         */
        public static Student createHighScoreStudent() {
            return new StudentBuilder()
                    .studentId("HIGH001")
                    .studentName("高分学生")
                    .score(680)
                    .ranking(1000)
                    .build();
        }

        /**
         * 创建中等分数学生
         */
        public static Student createMediumScoreStudent() {
            return new StudentBuilder()
                    .studentId("MED001")
                    .studentName("中等学生")
                    .score(550)
                    .ranking(50000)
                    .build();
        }

        /**
         * 创建低分学生
         */
        public static Student createLowScoreStudent() {
            return new StudentBuilder()
                    .studentId("LOW001")
                    .studentName("低分学生")
                    .score(450)
                    .ranking(100000)
                    .build();
        }
    }

    // ==================== 静态工厂方法 ====================
    
    public static UserBuilder user() {
        return new UserBuilder();
    }

    public static StudentBuilder student() {
        return new StudentBuilder();
    }
}
