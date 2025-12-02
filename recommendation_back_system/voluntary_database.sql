-- ============================================================================
--  Schema bootstrap for gaokao_adviser
--  Generated: 2025-12-02
--  Notes: Drops existing tables; run only in dev/test environments.
-- ============================================================================

-- DROP DATABASE IF EXISTS gaokao_adviser;
CREATE DATABASE voluntary_database DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE voluntary_database;

SET FOREIGN_KEY_CHECKS = 0;

-- --------------------------------------------------------------------------
-- 1. Dictionary tables
-- --------------------------------------------------------------------------
CREATE TABLE discipline_category (
                                     category_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '门类编号',
                                     category_name VARCHAR(30) NOT NULL UNIQUE COMMENT '名称',
                                     code VARCHAR(10) NOT NULL UNIQUE COMMENT '代码'
) ENGINE=InnoDB COMMENT='学科门类表';

INSERT INTO discipline_category (category_name, code) VALUES
                                                          ('哲学','01'),('经济学','02'),('法学','03'),('教育学','04'),('文学','05'),
                                                          ('历史学','06'),('理学','07'),('工学','08'),('农学','09'),('医学','10'),
                                                          ('管理学','12'),('艺术学','13');

CREATE TABLE university_type (
                                 type_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '类型编号',
                                 type_name VARCHAR(20) NOT NULL UNIQUE COMMENT '名称',
                                 description VARCHAR(100)
) ENGINE=InnoDB COMMENT='院校类型表';

INSERT INTO university_type (type_name, description) VALUES
                                                         ('综合类','学科齐全的综合性大学'),
                                                         ('理工类','以工科/理科为主'),
                                                         ('医药类','医学、药学特色'),
                                                         ('师范类','教师教育特色'),
                                                         ('财经类','经济管理见长');

CREATE TABLE province (
                          province_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '省份编号',
                          province_name VARCHAR(20) NOT NULL UNIQUE COMMENT '名称',
                          region VARCHAR(20) NOT NULL COMMENT '地区'
) ENGINE=InnoDB COMMENT='省份表';

INSERT INTO province (province_name, region) VALUES
                                                 ('北京市','华北'),('天津市','华北'),('河北省','华北'),('上海市','华东'),
                                                 ('江苏省','华东'),('浙江省','华东'),('安徽省','华东'),('湖北省','华中'),
                                                 ('广东省','华南'),('四川省','西南');

-- --------------------------------------------------------------------------
-- 2. Master tables
-- --------------------------------------------------------------------------
CREATE TABLE university (
                            university_id VARCHAR(10) PRIMARY KEY COMMENT '院校编号',
                            university_name VARCHAR(100) NOT NULL COMMENT '名称',
                            province_id INT NOT NULL,
                            type_id INT NOT NULL,
                            level VARCHAR(20) NOT NULL COMMENT '层次',
                            ranking INT,
                            create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT fk_uni_province FOREIGN KEY (province_id) REFERENCES province(province_id),
                            CONSTRAINT fk_uni_type FOREIGN KEY (type_id) REFERENCES university_type(type_id),
                            UNIQUE KEY uniq_rank (ranking)
) ENGINE=InnoDB COMMENT='院校表';

INSERT INTO university (university_id, university_name, province_id, type_id, level, ranking) VALUES
                                                                                                  ('10001','北京大学',1,1,'双一流A',1),
                                                                                                  ('10002','清华大学',1,2,'双一流A',2),
                                                                                                  ('10003','复旦大学',4,1,'双一流A',3),
                                                                                                  ('10004','上海交通大学',4,2,'双一流A',4),
                                                                                                  ('10005','浙江大学',6,1,'双一流A',5),
                                                                                                  ('10006','南京大学',5,1,'双一流A',7),
                                                                                                  ('10007','武汉大学',8,1,'双一流A',9),
                                                                                                  ('10008','中山大学',9,1,'双一流A',11),
                                                                                                  ('10009','四川大学',10,1,'双一流A',15),
                                                                                                  ('10010','华中师范大学',8,4,'双一流B',35),
                                                                                                  ('10011','上海财经大学',4,5,'双一流B',40),
                                                                                                  ('10012','南方医科大学',9,3,'双一流B',65);

CREATE TABLE major (
                       major_id VARCHAR(10) PRIMARY KEY COMMENT '专业编号',
                       major_name VARCHAR(50) NOT NULL COMMENT '名称',
                       discipline_category_id INT NOT NULL,
                       schooling_length TINYINT NOT NULL CHECK (schooling_length BETWEEN 2 AND 8),
                       degree_type VARCHAR(20) NOT NULL,
                       create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                       line_score INT,
                       CONSTRAINT fk_major_cat FOREIGN KEY (discipline_category_id) REFERENCES discipline_category(category_id)
) ENGINE=InnoDB COMMENT='专业表';

INSERT INTO major (major_id, major_name, discipline_category_id, schooling_length, degree_type, line_score) VALUES
                                                                                                                ('080901','计算机科学与技术',8,4,'工学学士',650),
                                                                                                                ('080902','软件工程',8,4,'工学学士',640),
                                                                                                                ('080903','人工智能',8,4,'工学学士',645),
                                                                                                                ('080904','数据科学与大数据技术',8,4,'工学学士',638),
                                                                                                                ('050101','汉语言文学',5,4,'文学学士',610),
                                                                                                                ('050201','英语',5,4,'文学学士',600),
                                                                                                                ('040101','教育学',4,4,'教育学学士',590),
                                                                                                                ('071001','信息与计算科学',7,4,'理学学士',620),
                                                                                                                ('100101','临床医学',10,5,'医学学士',680),
                                                                                                                ('100202','口腔医学',10,5,'医学学士',665),
                                                                                                                ('120203','会计学',12,4,'管理学学士',620),
                                                                                                                ('120204','金融学',12,4,'经济学学士',630);

-- --------------------------------------------------------------------------
-- 3. Business tables
-- --------------------------------------------------------------------------
CREATE TABLE student (
                         student_id VARCHAR(18) PRIMARY KEY COMMENT '学号',
                         student_name VARCHAR(50) NOT NULL,
                         id_card VARCHAR(18) NOT NULL UNIQUE,
                         gender CHAR(1) CHECK (gender IN ('男','女')),
                         college_entrance_exam_score DECIMAL(5,1) NOT NULL,
                         province_id INT NOT NULL,
                         ranking INT,
                         create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_student_province FOREIGN KEY (province_id) REFERENCES province(province_id)
) ENGINE=InnoDB COMMENT='学生表';

INSERT INTO student (student_id, student_name, id_card, gender, college_entrance_exam_score, province_id, ranking) VALUES
                                                                                                                       ('20230001','李明','110101200001010011','男',678.5,1,520),
                                                                                                                       ('20230002','王欣','110101200001020022','女',642.0,1,1800),
                                                                                                                       ('20230003','张琪','120101200001030033','女',625.5,2,2600),
                                                                                                                       ('20230004','赵远','320101200001040044','男',603.0,5,5200),
                                                                                                                       ('20230005','陈然','310101200001050055','女',588.0,4,7800),
                                                                                                                       ('20230006','苏航','330101200001060066','男',612.5,6,4300),
                                                                                                                       ('20230007','林雨','440101200001070077','女',635.0,9,2300),
                                                                                                                       ('20230008','刘泽','510101200001080088','男',598.0,10,9100);

CREATE TABLE system_user (
                             user_id INT AUTO_INCREMENT PRIMARY KEY,
                             username VARCHAR(50) NOT NULL UNIQUE,
                             password VARCHAR(100) NOT NULL,
                             role VARCHAR(20) NOT NULL CHECK (role IN ('admin','student')),
                             student_id VARCHAR(18) UNIQUE,
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             status INT DEFAULT 1,
                             CONSTRAINT fk_user_student FOREIGN KEY (student_id) REFERENCES student(student_id)
) ENGINE=InnoDB COMMENT='系统用户表';

INSERT INTO system_user (username, password, role, student_id) VALUES
                                                                   ('admin','123456','admin',NULL),
                                                                   ('student1','123456','student','20230001'),
                                                                   ('student2','123456','student','20230002'),
                                                                   ('student3','123456','student','20230003'),
                                                                   ('student4','123456','student','20230004'),
                                                                   ('student5','123456','student','20230005'),
                                                                   ('student6','123456','student','20230006'),
                                                                   ('student7','123456','student','20230007'),
                                                                   ('student8','123456','student','20230008');

CREATE TABLE enrollment_plan (
                                 plan_id INT AUTO_INCREMENT PRIMARY KEY,
                                 university_id VARCHAR(10) NOT NULL,
                                 major_id VARCHAR(10) NOT NULL,
                                 enrollment_year YEAR NOT NULL,
                                 enrollment_number INT NOT NULL CHECK (enrollment_number > 0),
                                 CONSTRAINT fk_plan_uni FOREIGN KEY (university_id) REFERENCES university(university_id),
                                 CONSTRAINT fk_plan_major FOREIGN KEY (major_id) REFERENCES major(major_id),
                                 UNIQUE KEY uniq_plan (university_id, major_id, enrollment_year)
) ENGINE=InnoDB COMMENT='招生计划表';

INSERT INTO enrollment_plan (university_id, major_id, enrollment_year, enrollment_number) VALUES
                                                                                              ('10001','080901',2024,180),('10001','050101',2024,80),('10001','120204',2024,90),
                                                                                              ('10002','080901',2024,200),('10002','080902',2024,120),('10002','080903',2024,110),
                                                                                              ('10003','080901',2024,150),('10003','050201',2024,70),('10003','120203',2024,60),
                                                                                              ('10004','100101',2024,220),('10004','100202',2024,150),
                                                                                              ('10005','080904',2024,160),('10005','040101',2024,90),
                                                                                              ('10006','071001',2024,140),('10006','080901',2024,120),
                                                                                              ('10007','040101',2024,130),('10007','050101',2024,110),
                                                                                              ('10008','120203',2024,100),('10008','120204',2024,120),
                                                                                              ('10009','100101',2024,180),('10009','100202',2024,130),
                                                                                              ('10010','040101',2024,160),('10010','050101',2024,90),
                                                                                              ('10011','120203',2024,110),('10011','120204',2024,100),
                                                                                              ('10012','100101',2024,200),('10012','100202',2024,150);

CREATE TABLE historical_admission (
                                      data_id INT AUTO_INCREMENT PRIMARY KEY,
                                      university_id VARCHAR(10) NOT NULL,
                                      major_id VARCHAR(10) NOT NULL,
                                      year YEAR NOT NULL,
                                      admission_number INT NOT NULL COMMENT '录取分数线',
                                      CONSTRAINT fk_hist_uni FOREIGN KEY (university_id) REFERENCES university(university_id),
                                      CONSTRAINT fk_hist_major FOREIGN KEY (major_id) REFERENCES major(major_id),
                                      UNIQUE KEY uniq_hist (university_id, major_id, year)
) ENGINE=InnoDB COMMENT='历年录取数据表';

INSERT INTO historical_admission (university_id, major_id, year, admission_number) VALUES
-- Peking University
('10001','080901',2023,674),('10001','080901',2022,672),('10001','080901',2021,669),
('10001','050101',2023,640),('10001','050101',2022,638),('10001','120204',2023,650),
-- Tsinghua
('10002','080901',2023,680),('10002','080901',2022,678),('10002','080902',2023,668),
('10002','080903',2023,669),
-- Fudan
('10003','080901',2023,663),('10003','050201',2023,623),('10003','120203',2023,631),
-- Shanghai Jiaotong
('10004','100101',2023,693),('10004','100202',2023,678),
-- Zhejiang
('10005','080904',2023,655),('10005','040101',2023,608),
-- Nanjing
('10006','071001',2023,630),('10006','080901',2023,648),
-- Wuhan
('10007','040101',2023,603),('10007','050101',2023,618),
-- Sun Yat-sen
('10008','120203',2023,610),('10008','120204',2023,622),
-- Sichuan
('10009','100101',2023,670),('10009','100202',2023,655),
-- CCNU
('10010','040101',2023,595),('10010','050101',2023,602),
-- SHUFE
('10011','120203',2023,628),('10011','120204',2023,636),
-- Southern Medical
('10012','100101',2023,663),('10012','100202',2023,651);

CREATE TABLE volunteer_relation (
                                    relation_id INT AUTO_INCREMENT PRIMARY KEY,
                                    student_id VARCHAR(18) NOT NULL,
                                    university_id VARCHAR(10) NOT NULL,
                                    major_id VARCHAR(10) NOT NULL,
                                    volunteer_order TINYINT NOT NULL CHECK (volunteer_order BETWEEN 1 AND 30),
                                    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                    UNIQUE KEY uniq_student_order (student_id, volunteer_order),
                                    CONSTRAINT fk_vr_student FOREIGN KEY (student_id) REFERENCES student(student_id),
                                    CONSTRAINT fk_vr_uni FOREIGN KEY (university_id) REFERENCES university(university_id),
                                    CONSTRAINT fk_vr_major FOREIGN KEY (major_id) REFERENCES major(major_id)
) ENGINE=InnoDB COMMENT='考生志愿关系表';

INSERT INTO volunteer_relation (student_id, university_id, major_id, volunteer_order) VALUES
                                                                                          ('20230001','10002','080901',1),
                                                                                          ('20230001','10001','080901',2),
                                                                                          ('20230001','10003','080901',3),
                                                                                          ('20230002','10001','050101',1),
                                                                                          ('20230002','10005','080904',2),
                                                                                          ('20230003','10005','080904',1),
                                                                                          ('20230003','10001','120204',2),
                                                                                          ('20230003','10004','100202',3),
                                                                                          ('20230004','10009','100101',1),
                                                                                          ('20230004','10007','040101',2),
                                                                                          ('20230005','10010','040101',1),
                                                                                          ('20230005','10011','120203',2),
                                                                                          ('20230006','10002','080902',1),
                                                                                          ('20230006','10001','080901',2),
                                                                                          ('20230007','10004','100101',1),
                                                                                          ('20230007','10008','120204',2),
                                                                                          ('20230008','10012','100101',1),
                                                                                          ('20230008','10005','040101',2);

CREATE TABLE volunteer_audit (
                                 audit_id INT AUTO_INCREMENT PRIMARY KEY,
                                 student_id VARCHAR(18) NOT NULL,
                                 operation_type ENUM('INSERT','UPDATE','DELETE') NOT NULL,
                                 university_id_old VARCHAR(10),
                                 university_name_old VARCHAR(100),
                                 major_id_old VARCHAR(10),
                                 volunteer_order_old TINYINT,
                                 university_id_new VARCHAR(10),
                                 university_name_new VARCHAR(100),
                                 major_id_new VARCHAR(10),
                                 volunteer_order_new TINYINT,
                                 change_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 changed_by VARCHAR(50) NOT NULL
) ENGINE=InnoDB COMMENT='志愿审计表';

CREATE TABLE major_hotness (
                               university_id VARCHAR(10) NOT NULL,
                               major_id VARCHAR(10) NOT NULL,
                               current_year INT NOT NULL,
                               volunteer_count INT DEFAULT 0,
                               first_choice_count INT DEFAULT 0,
                               last_updated DATETIME NOT NULL,
                               university_name VARCHAR(100),
                               major_name VARCHAR(50),
                               PRIMARY KEY (university_id, major_id, current_year),
                               KEY idx_hotness (volunteer_count DESC, first_choice_count DESC)
) ENGINE=InnoDB COMMENT='专业热度统计';

INSERT INTO major_hotness VALUES
                              ('10002','080901',2025,5,3,NOW(),'清华大学','计算机科学与技术'),
                              ('10001','050101',2025,3,2,NOW(),'北京大学','汉语言文学'),
                              ('10004','100101',2025,4,4,NOW(),'上海交通大学','临床医学');

SET FOREIGN_KEY_CHECKS = 1;

-- --------------------------------------------------------------------------
-- 4. Triggers & Stored Procedures
--    (与原项目一致，只保留与志愿/推荐逻辑直接相关的几个)
-- --------------------------------------------------------------------------
DELIMITER //

CREATE TRIGGER trg_volunteer_audit_insert
    AFTER INSERT ON volunteer_relation
    FOR EACH ROW
BEGIN
    DECLARE uni_name VARCHAR(100);
    SELECT university_name INTO uni_name FROM university WHERE university_id = NEW.university_id;
    INSERT INTO volunteer_audit (student_id, operation_type,
                                 university_id_new, university_name_new,
                                 major_id_new, volunteer_order_new,
                                 changed_by)
    VALUES (NEW.student_id, 'INSERT', NEW.university_id, IFNULL(uni_name,'未知大学'),
            NEW.major_id, NEW.volunteer_order, CURRENT_USER());
END//

CREATE TRIGGER trg_volunteer_audit_update
    AFTER UPDATE ON volunteer_relation
    FOR EACH ROW
BEGIN
    DECLARE uni_old VARCHAR(100);
    DECLARE uni_new VARCHAR(100);
    SELECT university_name INTO uni_old FROM university WHERE university_id = OLD.university_id;
    SELECT university_name INTO uni_new FROM university WHERE university_id = NEW.university_id;
    INSERT INTO volunteer_audit (student_id, operation_type,
                                 university_id_old, university_name_old,
                                 major_id_old, volunteer_order_old,
                                 university_id_new, university_name_new,
                                 major_id_new, volunteer_order_new,
                                 changed_by)
    VALUES (NEW.student_id, 'UPDATE',
            OLD.university_id, IFNULL(uni_old,'未知大学'),
            OLD.major_id, OLD.volunteer_order,
            NEW.university_id, IFNULL(uni_new,'未知大学'),
            NEW.major_id, NEW.volunteer_order,
            CURRENT_USER());
END//

CREATE TRIGGER trg_volunteer_audit_delete
    AFTER DELETE ON volunteer_relation
    FOR EACH ROW
BEGIN
    DECLARE uni_old VARCHAR(100);
    SELECT university_name INTO uni_old FROM university WHERE university_id = OLD.university_id;
    INSERT INTO volunteer_audit (student_id, operation_type,
                                 university_id_old, university_name_old,
                                 major_id_old, volunteer_order_old,
                                 changed_by)
    VALUES (OLD.student_id, 'DELETE',
            OLD.university_id, IFNULL(uni_old,'未知大学'),
            OLD.major_id, OLD.volunteer_order,
            CURRENT_USER());
END//

DELIMITER ;

-- ----------------- Stored Procedures (保持与项目一致) ---------------------
DELIMITER //

CREATE PROCEDURE get_student_volunteers(IN stu_id VARCHAR(18))
BEGIN
    SELECT vr.volunteer_order, u.university_name, m.major_name
    FROM volunteer_relation vr
             JOIN university u ON vr.university_id = u.university_id
             JOIN major m ON vr.major_id = m.major_id
    WHERE vr.student_id = stu_id
    ORDER BY vr.volunteer_order;
END//

CREATE PROCEDURE flexible_recommendation(IN stu_id VARCHAR(18))
BEGIN
    DECLARE stu_score DECIMAL(5,1);
    SELECT IFNULL(college_entrance_exam_score,600) INTO stu_score
    FROM student WHERE student_id = stu_id;
    IF stu_score IS NULL THEN SET stu_score = 600; END IF;

    SELECT stu_score AS student_score;

    SELECT u.university_name,
           m.major_name,
           ha.admission_number AS last_year_admission,
           ABS(stu_score - ha.admission_number) AS score_difference
    FROM university u
             JOIN historical_admission ha ON u.university_id = ha.university_id
             JOIN major m ON ha.major_id = m.major_id
    ORDER BY score_difference
    LIMIT 20;
END//

CREATE PROCEDURE predict_admission_probability(
    IN stu_id VARCHAR(18),
    IN uni_id VARCHAR(10),
    IN maj_id VARCHAR(10)
)
BEGIN
    DECLARE stu_score DECIMAL(5,1);
    DECLARE avg_admit DECIMAL(5,1);
    DECLARE std_dev DECIMAL(5,1);
    DECLARE prob DECIMAL(5,2);
    DECLARE record_count INT;

    SELECT IFNULL(college_entrance_exam_score,0) INTO stu_score
    FROM student WHERE student_id = stu_id;

    SELECT COUNT(*) INTO record_count
    FROM historical_admission
    WHERE university_id = uni_id AND major_id = maj_id;

    IF record_count = 0 THEN
        SELECT line_score INTO avg_admit FROM major WHERE major_id = maj_id;
        SET std_dev = 10;
    ELSE
        SELECT AVG(admission_number), IFNULL(STDDEV(admission_number),10)
        INTO avg_admit, std_dev
        FROM historical_admission
        WHERE university_id = uni_id AND major_id = maj_id;
    END IF;

    SET avg_admit = IFNULL(avg_admit,600);
    SET std_dev = IFNULL(std_dev,10);

    IF std_dev > 0 THEN
        SET prob = 50 + 50 * TANH( (stu_score - avg_admit) / (std_dev*0.8) );
    ELSE
        SET prob = IF(stu_score >= avg_admit,100,0);
    END IF;

    SELECT u.university_name,
           m.major_name,
           stu_score AS your_score,
           avg_admit AS avg_admission_score,
           std_dev AS score_std_dev,
           record_count AS historical_data_count,
           ROUND(GREATEST(0,LEAST(100,prob)),2) AS admission_probability,
           CASE
               WHEN prob > 80 THEN '高概率'
               WHEN prob > 50 THEN '中概率'
               WHEN prob > 20 THEN '低概率'
               ELSE '极小概率'
               END AS probability_level
    FROM university u
             JOIN major m ON m.major_id = maj_id
    WHERE u.university_id = uni_id;
END//

DELIMITER ;

-- ============================================================================
--  Done.  (Total sample data：省份10、院校12、专业12、学生8、历史录取32、志愿18)
-- ============================================================================