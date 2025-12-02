-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: gaokao_adviser
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `discipline_category`
--

DROP TABLE IF EXISTS `discipline_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discipline_category` (
  `category_id` int NOT NULL AUTO_INCREMENT COMMENT '门类编号',
  `category_name` varchar(30) NOT NULL COMMENT '名称',
  `code` varchar(10) NOT NULL COMMENT '代码',
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_name` (`category_name`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学科门类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discipline_category`
--

LOCK TABLES `discipline_category` WRITE;
/*!40000 ALTER TABLE `discipline_category` DISABLE KEYS */;
INSERT INTO `discipline_category` (`category_id`, `category_name`, `code`) VALUES (1,'哲学','01'),(2,'经济学','02'),(3,'法学','03'),(4,'教育学','04'),(5,'文学','05'),(6,'历史学','06'),(7,'理学','07'),(8,'工学','08'),(9,'农学','09'),(10,'医学','10'),(11,'军事学','11'),(12,'管理学','12'),(13,'艺术学','13');
/*!40000 ALTER TABLE `discipline_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enrollment_plan`
--

DROP TABLE IF EXISTS `enrollment_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enrollment_plan` (
  `plan_id` int NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `university_id` varchar(10) NOT NULL COMMENT '院校编号',
  `major_id` varchar(10) NOT NULL COMMENT '专业编号',
  `enrollment_year` year NOT NULL COMMENT '招生年份',
  `enrollment_number` int NOT NULL COMMENT '招生人数',
  PRIMARY KEY (`plan_id`),
  UNIQUE KEY `university_id` (`university_id`,`major_id`,`enrollment_year`),
  KEY `major_id` (`major_id`),
  CONSTRAINT `enrollment_plan_ibfk_1` FOREIGN KEY (`university_id`) REFERENCES `university` (`university_id`),
  CONSTRAINT `enrollment_plan_ibfk_2` FOREIGN KEY (`major_id`) REFERENCES `major` (`major_id`),
  CONSTRAINT `enrollment_plan_chk_1` CHECK ((`enrollment_number` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='招生计划表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollment_plan`
--

LOCK TABLES `enrollment_plan` WRITE;
/*!40000 ALTER TABLE `enrollment_plan` DISABLE KEYS */;
INSERT INTO `enrollment_plan` (`plan_id`, `university_id`, `major_id`, `enrollment_year`, `enrollment_number`) VALUES (1,'10001','080901',2023,120),(2,'10001','050101',2023,80),(3,'10001','070101',2023,60),(4,'10002','080901',2023,150),(5,'10002','080902',2023,100),(6,'10002','081001',2023,90),(7,'10003','040101',2023,70),(8,'10004','100101',2023,200),(9,'10004','100701',2023,150),(10,'10005','120203',2023,100),(11,'10005','020101',2023,60),(12,'10006','090101',2023,50),(13,'10007','130201',2023,30),(14,'10007','130301',2023,30),(15,'10008','050201',2023,70),(16,'10009','080901',2023,180),(17,'10009','080902',2023,120),(18,'10010','080901',2023,100),(19,'10010','081001',2023,80),(20,'10011','020101',2023,60);
/*!40000 ALTER TABLE `enrollment_plan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historical_admission`
--

DROP TABLE IF EXISTS `historical_admission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historical_admission` (
  `data_id` int NOT NULL AUTO_INCREMENT COMMENT '数据ID',
  `university_id` varchar(10) NOT NULL COMMENT '院校编号',
  `major_id` varchar(10) NOT NULL COMMENT '专业编号',
  `year` year NOT NULL COMMENT '年份',
  `admission_number` int NOT NULL COMMENT '录取人数',
  PRIMARY KEY (`data_id`),
  UNIQUE KEY `university_id` (`university_id`,`major_id`,`year`),
  KEY `major_id` (`major_id`),
  CONSTRAINT `historical_admission_ibfk_1` FOREIGN KEY (`university_id`) REFERENCES `university` (`university_id`),
  CONSTRAINT `historical_admission_ibfk_2` FOREIGN KEY (`major_id`) REFERENCES `major` (`major_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='历年录取数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historical_admission`
--

LOCK TABLES `historical_admission` WRITE;
/*!40000 ALTER TABLE `historical_admission` DISABLE KEYS */;
INSERT INTO `historical_admission` (`data_id`, `university_id`, `major_id`, `year`, `admission_number`) VALUES (1,'10001','080901',2022,118),(2,'10001','080901',2021,115),(3,'10001','080901',2020,112),(4,'10001','050101',2022,78),(5,'10001','050101',2021,75),(6,'10001','050101',2020,72),(7,'10002','080901',2022,145),(8,'10002','080901',2021,140),(9,'10002','080901',2020,138),(10,'10002','080902',2022,98),(11,'10002','080902',2021,95),(12,'10002','080902',2020,90),(13,'10003','040101',2022,68),(14,'10003','040101',2021,65),(15,'10003','040101',2020,62),(16,'10004','100101',2022,195),(17,'10004','100101',2021,190),(18,'10004','100101',2020,185),(19,'10005','120203',2022,98),(20,'10005','120203',2021,95),(21,'10005','120203',2020,92);
/*!40000 ALTER TABLE `historical_admission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `major`
--

DROP TABLE IF EXISTS `major`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `major` (
  `major_id` varchar(10) NOT NULL COMMENT '专业编号',
  `major_name` varchar(50) NOT NULL COMMENT '名称',
  `discipline_category_id` int NOT NULL COMMENT '学科门类',
  `schooling_length` tinyint NOT NULL COMMENT '学制',
  `degree_type` varchar(20) NOT NULL COMMENT '学位类型',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `line_score` int DEFAULT NULL COMMENT '分数线',
  PRIMARY KEY (`major_id`),
  KEY `discipline_category_id` (`discipline_category_id`),
  CONSTRAINT `major_ibfk_1` FOREIGN KEY (`discipline_category_id`) REFERENCES `discipline_category` (`category_id`),
  CONSTRAINT `major_chk_1` CHECK ((`schooling_length` between 2 and 8))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='专业表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `major`
--

LOCK TABLES `major` WRITE;
/*!40000 ALTER TABLE `major` DISABLE KEYS */;
INSERT INTO `major` (`major_id`, `major_name`, `discipline_category_id`, `schooling_length`, `degree_type`, `create_time`, `line_score`) VALUES ('020101','经济学',2,4,'经济学学士','2025-06-20 00:17:36',351),('030101','法学',3,4,'法学学士','2025-06-20 00:17:36',123),('040101','教育学',4,4,'教育学学士','2025-06-20 00:17:36',162),('050101','汉语言文学',5,4,'文学学士','2025-06-20 00:17:36',618),('050201','英语',5,4,'文学学士','2025-06-20 00:17:36',520),('070101','数学与应用数学',7,4,'理学学士','2025-06-20 00:17:36',312),('080901','计算机科学与技术',8,4,'工学学士','2025-06-20 00:17:36',543),('080902','软件工程',8,4,'工学学士','2025-06-20 00:17:36',663),('080903','人工智能',8,4,'工学学士','2025-12-02 15:12:24',660),('080904','数据科学与大数据技术',8,4,'工学学士',NULL,655),('080905','信息安全',8,4,'工学学士','2025-12-02 15:14:48',670),('081001','土木工程',8,4,'工学学士','2025-06-20 00:17:36',452),('090101','农学',9,4,'农学学士','2025-06-20 00:17:36',453),('100101','临床医学',10,5,'医学学士','2025-06-20 00:17:36',685),('100701','护理学',10,4,'理学学士','2025-06-20 00:17:36',321),('120201','工商管理',12,4,'管理学学士','2025-06-20 00:17:36',456),('120203','会计学',12,4,'管理学学士','2025-06-20 00:17:36',456),('130201','音乐表演',13,4,'艺术学学士','2025-06-20 00:17:36',354),('130301','美术学',13,4,'艺术学学士','2025-06-20 00:17:36',865);
/*!40000 ALTER TABLE `major` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `major_hotness`
--

DROP TABLE IF EXISTS `major_hotness`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `major_hotness` (
  `university_id` varchar(10) NOT NULL,
  `major_id` varchar(10) NOT NULL,
  `current_year` int NOT NULL,
  `volunteer_count` int DEFAULT '0',
  `first_choice_count` int DEFAULT '0',
  `last_updated` datetime NOT NULL,
  `university_name` varchar(100) DEFAULT NULL,
  `major_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`university_id`,`major_id`,`current_year`),
  KEY `idx_hotness` (`volunteer_count` DESC,`first_choice_count` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `major_hotness`
--

LOCK TABLES `major_hotness` WRITE;
/*!40000 ALTER TABLE `major_hotness` DISABLE KEYS */;
INSERT INTO `major_hotness` (`university_id`, `major_id`, `current_year`, `volunteer_count`, `first_choice_count`, `last_updated`, `university_name`, `major_name`) VALUES ('10001','050101',2025,1,1,'2025-12-02 16:29:53','北京大学','汉语言文学'),('10001','080901',2025,0,0,'2025-12-02 16:29:53','北方综合大学','计算机科学与技术');
/*!40000 ALTER TABLE `major_hotness` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `province`
--

DROP TABLE IF EXISTS `province`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `province` (
  `province_id` int NOT NULL AUTO_INCREMENT COMMENT '省份编号',
  `province_name` varchar(20) NOT NULL COMMENT '名称',
  `region` varchar(20) NOT NULL COMMENT '地区',
  PRIMARY KEY (`province_id`),
  UNIQUE KEY `province_name` (`province_name`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='省份表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `province`
--

LOCK TABLES `province` WRITE;
/*!40000 ALTER TABLE `province` DISABLE KEYS */;
INSERT INTO `province` (`province_id`, `province_name`, `region`) VALUES (1,'北京市','华北'),(2,'天津市','华北'),(3,'河北省','华北'),(4,'山西省','华北'),(5,'内蒙古自治区','华北'),(6,'辽宁省','东北'),(7,'吉林省','东北'),(8,'黑龙江省','东北'),(9,'上海市','华东'),(10,'江苏省','华东'),(11,'浙江省','华东'),(12,'安徽省','华东'),(13,'福建省','华东'),(14,'江西省','华东'),(15,'山东省','华东'),(16,'河南省','华中'),(17,'湖北省','华中'),(18,'湖南省','华中'),(19,'广东省','华南'),(20,'广西壮族自治区','华南'),(21,'海南省','华南'),(22,'重庆市','西南'),(23,'四川省','西南'),(24,'贵州省','西南'),(25,'云南省','西南'),(26,'西藏自治区','西南'),(27,'陕西省','西北'),(28,'甘肃省','西北'),(29,'青海省','西北'),(30,'宁夏回族自治区','西北'),(31,'新疆维吾尔自治区','西北');
/*!40000 ALTER TABLE `province` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `student_id` varchar(18) NOT NULL COMMENT '学号',
  `student_name` varchar(50) NOT NULL COMMENT '姓名',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `gender` char(1) DEFAULT NULL COMMENT '性别',
  `college_entrance_exam_score` decimal(5,1) NOT NULL COMMENT '高考成绩',
  `province_id` int NOT NULL COMMENT '省份编号',
  `ranking` int DEFAULT NULL COMMENT '排名',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`student_id`),
  UNIQUE KEY `id_card` (`id_card`),
  KEY `province_id` (`province_id`),
  CONSTRAINT `student_ibfk_1` FOREIGN KEY (`province_id`) REFERENCES `province` (`province_id`),
  CONSTRAINT `student_chk_1` CHECK ((`gender` in (_utf8mb4'男',_utf8mb4'女')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` (`student_id`, `student_name`, `id_card`, `gender`, `college_entrance_exam_score`, `province_id`, `ranking`, `create_time`) VALUES ('20230001','陈柳','110101200001011234','男',625.5,1,125,'2025-06-20 00:20:10'),('20230002','李华','110101200001021235','女',632.0,1,980,'2025-06-20 00:20:10'),('20230003','王芳','120101200001031236','女',598.5,2,3450,'2025-06-20 00:20:10'),('20230004','赵强','130101200001041237','男',487.0,3,4200,'2025-06-20 00:20:10'),('20230005','刘伟','140101200001051238','男',612.5,4,2100,'2025-06-20 00:20:10'),('20230006','陈静','150101200001061239','女',605.0,5,2850,'2025-06-20 00:20:10'),('20230007','杨光','210101200001071240','男',642.5,6,650,'2025-06-20 00:20:10'),('20230008','周雪','220101200001081241','女',635.0,7,850,'2025-06-20 00:20:10'),('20230009','吴刚','230101200001091242','男',628.5,8,1100,'2025-06-20 00:20:10'),('20230010','郑梅','310101200001101243','女',655.0,9,320,'2025-06-20 00:20:10'),('20230011','孙亮','320101200001111244','男',618.0,10,1750,'2025-06-20 00:20:10'),('20230012','朱琳','330101200001121245','女',595.5,11,3800,'2025-06-20 00:20:10'),('20230013','胡军','340101200001131246','男',602.0,12,3100,'2025-06-20 00:20:10'),('20230014','林娜','350101200001141247','女',640.5,13,700,'2025-06-20 00:20:10'),('20230015','徐阳','360101200001151248','男',630.0,14,950,'2025-06-20 00:20:10');
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_user`
--

DROP TABLE IF EXISTS `system_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_user` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `role` varchar(20) NOT NULL COMMENT '角色',
  `student_id` varchar(18) DEFAULT NULL COMMENT '学号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` int DEFAULT '0' COMMENT '用户状态',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `student_id` (`student_id`),
  CONSTRAINT `system_user_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`),
  CONSTRAINT `system_user_chk_1` CHECK ((`role` in (_utf8mb4'admin',_utf8mb4'student')))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_user`
--

LOCK TABLES `system_user` WRITE;
/*!40000 ALTER TABLE `system_user` DISABLE KEYS */;
INSERT INTO `system_user` (`user_id`, `username`, `password`, `role`, `student_id`, `create_time`, `status`) VALUES (1,'admin','123456','admin',NULL,'2025-06-20 00:20:29',1),(3,'student2','123456','student','20230002','2025-06-20 00:20:29',1),(4,'student3','123456','student','20230003','2025-06-20 00:20:29',1),(5,'student4','123456','student','20230004','2025-06-20 00:20:29',1),(7,'student5','123456','student','20230011','2025-06-21 10:51:17',1);
/*!40000 ALTER TABLE `system_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `university`
--

DROP TABLE IF EXISTS `university`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `university` (
  `university_id` varchar(10) NOT NULL COMMENT '院校编号',
  `university_name` varchar(100) NOT NULL COMMENT '名称',
  `province_id` int NOT NULL COMMENT '省份编号',
  `type_id` int NOT NULL COMMENT '类型编号',
  `level` varchar(20) NOT NULL COMMENT '层次',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `ranking` int DEFAULT NULL COMMENT '院校排名',
  PRIMARY KEY (`university_id`),
  UNIQUE KEY `i_rank` (`ranking`),
  KEY `province_id` (`province_id`),
  KEY `type_id` (`type_id`),
  CONSTRAINT `university_ibfk_1` FOREIGN KEY (`province_id`) REFERENCES `province` (`province_id`),
  CONSTRAINT `university_ibfk_2` FOREIGN KEY (`type_id`) REFERENCES `university_type` (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='院校表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `university`
--

LOCK TABLES `university` WRITE;
/*!40000 ALTER TABLE `university` DISABLE KEYS */;
INSERT INTO `university` (`university_id`, `university_name`, `province_id`, `type_id`, `level`, `create_time`, `ranking`) VALUES ('10001','北京大学',1,1,'985','2025-06-20 00:24:14',1),('10002','清华大学',1,2,'985','2025-06-20 00:24:14',2),('10003','浙江大学',11,3,'985','2025-06-20 00:24:14',12),('10004','复旦大学',9,4,'985','2025-06-20 00:24:14',3),('10005','银河财经大学',19,5,'985','2025-06-20 00:24:14',4),('10006','上海交通大学',9,6,'985','2025-06-20 00:24:14',5),('10007','南京大学',10,7,'985','2025-06-20 00:24:14',6),('10008','武汉大学',17,8,'985','2025-06-20 00:24:14',77),('10009','南方科技大学',19,2,'本科一批','2025-06-20 00:24:14',7),('10010','西北工业大学',27,2,'本科一批','2025-06-20 00:24:14',56),('10011','中原大学',16,1,'本科一批','2025-06-20 00:24:14',9),('10012','滨海大学',2,1,'本科二批','2025-06-20 00:24:14',10),('10013','长江大学',17,1,'本科二批','2025-06-20 00:24:14',11),('10014','天山大学',31,1,'本科二批','2025-06-20 00:24:14',24),('10015','高原大学',26,1,'本科二批','2025-06-20 00:24:14',76);
/*!40000 ALTER TABLE `university` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `university_type`
--

DROP TABLE IF EXISTS `university_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `university_type` (
  `type_id` int NOT NULL AUTO_INCREMENT COMMENT '类型编号',
  `type_name` varchar(20) NOT NULL COMMENT '名称',
  `description` varchar(100) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`type_id`),
  UNIQUE KEY `type_name` (`type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='院校类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `university_type`
--

LOCK TABLES `university_type` WRITE;
/*!40000 ALTER TABLE `university_type` DISABLE KEYS */;
INSERT INTO `university_type` (`type_id`, `type_name`, `description`) VALUES (1,'综合类','学科门类齐全、科研实力强的综合性大学'),(2,'理工类','以理工科为主的大学，工程和科学领域突出'),(3,'师范类','以培养教师和教育工作者为主的大学'),(4,'医药类','以医学和药学为主的大学'),(5,'财经类','以经济学、金融学为主的大学'),(6,'农林类','以农业和林业为主的大学'),(7,'艺术类','以艺术类专业为主的大学'),(8,'语言类','以语言类专业为主的大学');
/*!40000 ALTER TABLE `university_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `volunteer_audit`
--

DROP TABLE IF EXISTS `volunteer_audit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `volunteer_audit` (
  `audit_id` int NOT NULL AUTO_INCREMENT,
  `student_id` varchar(18) NOT NULL COMMENT '学生ID',
  `operation_type` enum('INSERT','UPDATE','DELETE') NOT NULL COMMENT '操作类型',
  `university_id_old` varchar(10) DEFAULT NULL COMMENT '原院校ID',
  `university_name_old` varchar(100) DEFAULT NULL COMMENT '原院校名称',
  `major_id_old` varchar(10) DEFAULT NULL COMMENT '原专业ID',
  `volunteer_order_old` tinyint DEFAULT NULL COMMENT '原志愿顺序',
  `university_id_new` varchar(10) DEFAULT NULL COMMENT '新院校ID',
  `university_name_new` varchar(100) DEFAULT NULL COMMENT '新院校名称',
  `major_id_new` varchar(10) DEFAULT NULL COMMENT '新专业ID',
  `volunteer_order_new` tinyint DEFAULT NULL COMMENT '新志愿顺序',
  `change_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
  `changed_by` varchar(50) NOT NULL COMMENT '操作用户',
  PRIMARY KEY (`audit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `volunteer_audit`
--

LOCK TABLES `volunteer_audit` WRITE;
/*!40000 ALTER TABLE `volunteer_audit` DISABLE KEYS */;
INSERT INTO `volunteer_audit` (`audit_id`, `student_id`, `operation_type`, `university_id_old`, `university_name_old`, `major_id_old`, `volunteer_order_old`, `university_id_new`, `university_name_new`, `major_id_new`, `volunteer_order_new`, `change_time`, `changed_by`) VALUES (1,'20230011','DELETE','10001','北方综合大学','080901',1,NULL,NULL,NULL,NULL,'2025-06-21 15:27:20','root@localhost'),(2,'20230004','UPDATE','10002','东方理工大学','081001',1,'10001','北方综合大学','080901',1,'2025-06-21 15:58:57','root@localhost'),(3,'20230002','UPDATE','10001','北京大学','080901',1,'10001','北京大学','050101',1,'2025-12-02 16:29:53','root@localhost');
/*!40000 ALTER TABLE `volunteer_audit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `volunteer_relation`
--

DROP TABLE IF EXISTS `volunteer_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `volunteer_relation` (
  `relation_id` int NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `student_id` varchar(18) NOT NULL COMMENT '学号',
  `university_id` varchar(10) NOT NULL COMMENT '院校编号',
  `major_id` varchar(10) NOT NULL COMMENT '专业编号',
  `volunteer_order` tinyint NOT NULL COMMENT '志愿顺序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`relation_id`),
  UNIQUE KEY `student_id` (`student_id`,`volunteer_order`),
  KEY `university_id` (`university_id`),
  KEY `major_id` (`major_id`),
  CONSTRAINT `volunteer_relation_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`),
  CONSTRAINT `volunteer_relation_ibfk_2` FOREIGN KEY (`university_id`) REFERENCES `university` (`university_id`),
  CONSTRAINT `volunteer_relation_ibfk_3` FOREIGN KEY (`major_id`) REFERENCES `major` (`major_id`),
  CONSTRAINT `volunteer_relation_chk_1` CHECK ((`volunteer_order` between 1 and 30))
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考生志愿关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `volunteer_relation`
--

LOCK TABLES `volunteer_relation` WRITE;
/*!40000 ALTER TABLE `volunteer_relation` DISABLE KEYS */;
INSERT INTO `volunteer_relation` (`relation_id`, `student_id`, `university_id`, `major_id`, `volunteer_order`, `create_time`) VALUES (1,'20230001','10001','080901',1,'2025-06-20 00:21:04'),(2,'20230001','10002','080902',2,'2025-06-20 00:21:04'),(3,'20230001','10009','080901',3,'2025-06-20 00:21:04'),(4,'20230002','10001','050101',1,'2025-12-02 16:29:53'),(5,'20230002','10001','100701',2,'2025-06-20 00:21:04'),(6,'20230002','10003','040101',3,'2025-06-20 00:21:04'),(7,'20230003','10005','120203',1,'2025-06-20 00:21:04'),(8,'20230003','10001','020101',2,'2025-06-20 00:21:04'),(9,'20230003','10008','050201',3,'2025-06-20 00:21:04'),(10,'20230004','10001','080901',1,'2025-06-21 15:58:57'),(11,'20230004','10010','081001',2,'2025-06-20 00:21:04'),(12,'20230004','10006','090101',3,'2025-06-20 00:21:04'),(13,'20230005','10007','130201',1,'2025-06-20 00:21:04'),(14,'20230005','10007','130301',2,'2025-06-20 00:21:04'),(15,'20230005','10001','050101',3,'2025-06-20 00:21:04'),(16,'20230006','10003','040101',1,'2025-06-20 00:21:04'),(17,'20230006','10001','050101',2,'2025-06-20 00:21:04'),(18,'20230006','10008','050201',3,'2025-06-20 00:21:04'),(19,'20230007','10002','080901',1,'2025-06-20 00:21:04'),(20,'20230007','10009','080902',2,'2025-06-20 00:21:04'),(21,'20230007','10010','080901',3,'2025-06-20 00:21:04'),(22,'20230008','10001','070101',1,'2025-06-20 00:21:04'),(23,'20230008','10002','080901',2,'2025-06-20 00:21:04'),(24,'20230008','10003','040101',3,'2025-06-20 00:21:04'),(25,'20230009','10004','100101',1,'2025-06-20 00:21:04'),(26,'20230009','10004','100701',2,'2025-06-20 00:21:04'),(27,'20230009','10001','070101',3,'2025-06-20 00:21:04'),(28,'20230010','10008','050201',1,'2025-06-20 00:21:04'),(29,'20230010','10001','050101',2,'2025-06-20 00:21:04'),(30,'20230010','10003','040101',3,'2025-06-20 00:21:04');
/*!40000 ALTER TABLE `volunteer_relation` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
/*!50032 DROP TRIGGER IF EXISTS trg_validate_volunteer_order */;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_validate_volunteer_order` BEFORE INSERT ON `volunteer_relation` FOR EACH ROW BEGIN
    DECLARE student_score DECIMAL(5,1);
    DECLARE target_score INT;
    DECLARE max_existing_score INT DEFAULT 0;
    DECLARE min_existing_score INT DEFAULT 999;
    DECLARE student_exists INT DEFAULT 0;
    DECLARE major_exists INT DEFAULT 0;
    DECLARE existing_volunteer_count INT DEFAULT 0;

    -- 检查学生是否存在
    SELECT COUNT(*) INTO student_exists
    FROM student WHERE student_id = NEW.student_id;

    IF student_exists = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '学生ID不存在，请先添加学生信息';
    END IF;

    -- 检查专业是否存在
    SELECT COUNT(*) INTO major_exists
    FROM major WHERE major_id = NEW.major_id;

    IF major_exists = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '专业ID不存在，请检查专业信息';
    END IF;

    -- 获取学生成绩
    SELECT college_entrance_exam_score INTO student_score
    FROM student WHERE student_id = NEW.student_id;

    -- 获取目标专业分数线
    SELECT IFNULL(line_score, 0) INTO target_score
    FROM major WHERE major_id = NEW.major_id;

    -- 检查是否已有志愿
    SELECT COUNT(*) INTO existing_volunteer_count
    FROM volunteer_relation
    WHERE student_id = NEW.student_id;

    -- 获取已有志愿的最高分和最低分
    IF existing_volunteer_count > 0 THEN
        SELECT
            IFNULL(MAX(m.line_score), 0),
            IFNULL(MIN(m.line_score), 999)
        INTO max_existing_score, min_existing_score
        FROM volunteer_relation vr
                 JOIN major m ON vr.major_id = m.major_id
        WHERE vr.student_id = NEW.student_id;
    END IF;

    -- 检查志愿顺序是否已存在
    IF EXISTS (
        SELECT 1 FROM volunteer_relation
        WHERE student_id = NEW.student_id
          AND volunteer_order = NEW.volunteer_order
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '该志愿顺序已存在，请选择其他顺序';
    END IF;

    -- 检查志愿顺序合理性
    IF NEW.volunteer_order = 1 AND target_score < student_score - 30 AND target_score > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '第一志愿分数线过低，建议选择更具挑战性的目标';
    END IF;

    IF NEW.volunteer_order > 1 AND target_score > max_existing_score AND max_existing_score > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '志愿顺序不合理，高分数线专业应该放在更靠前的志愿';
    END IF;

    IF NEW.volunteer_order < 6 AND target_score > student_score + 50 AND target_score > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '前五志愿分数线过高，成功概率较低';
    END IF;

    -- 检查志愿总数是否超过限制
    IF existing_volunteer_count >= 30 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '志愿数量已达上限(30个)，不能继续添加';
    END IF;

    -- 检查是否重复填报同一院校专业
    IF EXISTS (
        SELECT 1 FROM volunteer_relation
        WHERE student_id = NEW.student_id
          AND university_id = NEW.university_id
          AND major_id = NEW.major_id
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '不能重复填报同一院校专业';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
/*!50032 DROP TRIGGER IF EXISTS trg_volunteer_audit_insert */;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_volunteer_audit_insert` AFTER INSERT ON `volunteer_relation` FOR EACH ROW BEGIN
    DECLARE uni_name_new VARCHAR(100) DEFAULT '未知大学';

    -- 尝试获取大学名称
    SELECT university_name INTO uni_name_new
    FROM university WHERE university_id = NEW.university_id;

    -- 插入审计记录
    INSERT INTO volunteer_audit
    (student_id, operation_type,
     university_id_new, university_name_new,
     major_id_new, volunteer_order_new,
     change_time, changed_by)
    VALUES
        (NEW.student_id, 'INSERT',
         NEW.university_id, IFNULL(uni_name_new, '未知大学'),
         NEW.major_id, NEW.volunteer_order,
         NOW(), CURRENT_USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
/*!50032 DROP TRIGGER IF EXISTS trg_update_major_hotness */;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_update_major_hotness` AFTER INSERT ON `volunteer_relation` FOR EACH ROW BEGIN
    DECLARE uni_name VARCHAR(100);
    DECLARE maj_name VARCHAR(50);

    SELECT university_name INTO uni_name FROM university WHERE university_id = NEW.university_id;
    SELECT major_name INTO maj_name FROM major WHERE major_id = NEW.major_id;

    INSERT INTO major_hotness (
        university_id, major_id, current_year, volunteer_count, first_choice_count,
        last_updated, university_name, major_name
    )
    VALUES (
               NEW.university_id, NEW.major_id, YEAR(CURDATE()), 1,
               IF(NEW.volunteer_order = 1, 1, 0), NOW(),
               IFNULL(uni_name, '未知大学'), IFNULL(maj_name, '未知专业')
           )
    ON DUPLICATE KEY UPDATE
                         volunteer_count = volunteer_count + 1,
                         first_choice_count = first_choice_count + IF(NEW.volunteer_order = 1, 1, 0),
                         last_updated = NOW();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
/*!50032 DROP TRIGGER IF EXISTS trg_volunteer_audit_update */;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_volunteer_audit_update` AFTER UPDATE ON `volunteer_relation` FOR EACH ROW BEGIN
    DECLARE uni_name_old VARCHAR(100) DEFAULT '未知大学';
    DECLARE uni_name_new VARCHAR(100) DEFAULT '未知大学';

    -- 尝试获取大学名称
    SELECT university_name INTO uni_name_old
    FROM university WHERE university_id = OLD.university_id;

    SELECT university_name INTO uni_name_new
    FROM university WHERE university_id = NEW.university_id;

    -- 插入审计记录
    INSERT INTO volunteer_audit
    (student_id, operation_type,
     university_id_old, university_name_old,
     major_id_old, volunteer_order_old,
     university_id_new, university_name_new,
     major_id_new, volunteer_order_new,
     change_time, changed_by)
    VALUES
        (NEW.student_id, 'UPDATE',
         OLD.university_id, IFNULL(uni_name_old, '未知大学'),
         OLD.major_id, OLD.volunteer_order,
         NEW.university_id, IFNULL(uni_name_new, '未知大学'),
         NEW.major_id, NEW.volunteer_order,
         NOW(), CURRENT_USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
/*!50032 DROP TRIGGER IF EXISTS trg_update_major_hotness_on_update */;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_update_major_hotness_on_update` AFTER UPDATE ON `volunteer_relation` FOR EACH ROW BEGIN
    DECLARE uni_name VARCHAR(100);
    DECLARE maj_name VARCHAR(50);

    -- 处理志愿变更
    IF OLD.university_id != NEW.university_id OR OLD.major_id != NEW.major_id THEN
        -- 减少原院校专业热度
        UPDATE major_hotness
        SET volunteer_count = GREATEST(0, volunteer_count - 1),
            first_choice_count = GREATEST(0, first_choice_count - IF(OLD.volunteer_order = 1, 1, 0)),
            last_updated = NOW()
        WHERE university_id = OLD.university_id AND major_id = OLD.major_id
          AND current_year = YEAR(CURDATE());

        -- 增加新院校专业热度
        SELECT university_name INTO uni_name FROM university WHERE university_id = NEW.university_id;
        SELECT major_name INTO maj_name FROM major WHERE major_id = NEW.major_id;

        INSERT INTO major_hotness (university_id, major_id, current_year, volunteer_count,
                                   first_choice_count, last_updated, university_name, major_name)
        VALUES (NEW.university_id, NEW.major_id, YEAR(CURDATE()), 1,
                IF(NEW.volunteer_order = 1, 1, 0), NOW(),
                IFNULL(uni_name, '未知大学'), IFNULL(maj_name, '未知专业'))
        ON DUPLICATE KEY UPDATE
                             volunteer_count = volunteer_count + 1,
                             first_choice_count = first_choice_count + IF(NEW.volunteer_order = 1, 1, 0),
                             last_updated = NOW();
        -- 处理志愿顺序变更
    ELSEIF OLD.volunteer_order != NEW.volunteer_order AND
           ((OLD.volunteer_order = 1) OR (NEW.volunteer_order = 1)) THEN
        UPDATE major_hotness
        SET first_choice_count = first_choice_count + IF(NEW.volunteer_order = 1, 1, -1),
            last_updated = NOW()
        WHERE university_id = NEW.university_id AND major_id = NEW.major_id
          AND current_year = YEAR(CURDATE());
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
/*!50032 DROP TRIGGER IF EXISTS trg_volunteer_audit_delete */;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_volunteer_audit_delete` AFTER DELETE ON `volunteer_relation` FOR EACH ROW BEGIN
    DECLARE uni_name_old VARCHAR(100) DEFAULT '未知大学';

    -- 尝试获取大学名称
    SELECT university_name INTO uni_name_old
    FROM university WHERE university_id = OLD.university_id;

    -- 插入审计记录
    INSERT INTO volunteer_audit
    (student_id, operation_type,
     university_id_old, university_name_old,
     major_id_old, volunteer_order_old,
     change_time, changed_by)
    VALUES
        (OLD.student_id, 'DELETE',
         OLD.university_id, IFNULL(uni_name_old, '未知大学'),
         OLD.major_id, OLD.volunteer_order,
         NOW(), CURRENT_USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
/*!50032 DROP TRIGGER IF EXISTS trg_decrease_major_hotness */;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_decrease_major_hotness` AFTER DELETE ON `volunteer_relation` FOR EACH ROW BEGIN
    UPDATE major_hotness
    SET volunteer_count = GREATEST(0, volunteer_count - 1),
        first_choice_count = GREATEST(0, first_choice_count - IF(OLD.volunteer_order = 1, 1, 0)),
        last_updated = NOW()
    WHERE university_id = OLD.university_id
      AND major_id = OLD.major_id
      AND current_year = YEAR(CURDATE());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Dumping routines for database 'gaokao_adviser'
--
/*!50003 DROP PROCEDURE IF EXISTS `analyze_major_employment_prospects` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `analyze_major_employment_prospects`(
    IN maj_id VARCHAR(10)
)
proc_label: BEGIN
    DECLARE major_exists INT;
    DECLARE major_name_var VARCHAR(50);
    DECLARE category_name_var VARCHAR(30);
    DECLARE degree_type_var VARCHAR(20);
    DECLARE schooling_length_var TINYINT;
    DECLARE avg_employment_rate DECIMAL(5,2);
    DECLARE avg_salary DECIMAL(8,2);

    -- 检查专业是否存在
    SELECT COUNT(*) INTO major_exists FROM major WHERE major_id = maj_id;

    -- 如果专业不存在，显示可用专业并退出
    IF major_exists = 0 THEN
        SELECT 'ERROR: 专业ID不存在，以下是可能的专业:' AS message;
        SELECT major_id, major_name FROM major ORDER BY major_id LIMIT 10;
        LEAVE proc_label;
    END IF;

    -- 获取专业基本信息
    SELECT
        m.major_name,
        dc.category_name,
        m.degree_type,
        m.schooling_length
    INTO
        major_name_var,
        category_name_var,
        degree_type_var,
        schooling_length_var
    FROM major m
             JOIN discipline_category dc ON m.discipline_category_id = dc.category_id
    WHERE m.major_id = maj_id;

    -- 生成模拟数据
    SET avg_employment_rate = ROUND(70 + RAND() * 20, 2);
    SET avg_salary = ROUND(7000 + RAND() * 3000, 2);

    -- 整合所有信息到一个表中
    SELECT
        maj_id AS major_id,
        major_name_var AS major_name,
        category_name_var AS discipline_category,
        degree_type_var AS degree_type,
        schooling_length_var AS schooling_length,
        avg_employment_rate AS average_employment_rate,
        avg_salary AS average_salary,
        CASE
            WHEN avg_employment_rate > 85 THEN '就业率高'
            WHEN avg_employment_rate > 75 THEN '就业率良好'
            ELSE '就业率一般'
            END AS employment_status,
        CASE
            WHEN major_name_var LIKE '%计算机%' OR major_name_var LIKE '%软件%' OR major_name_var LIKE '%人工智能%' THEN
                '信息技术行业持续增长，就业前景广阔。'
            WHEN major_name_var LIKE '%电子%' OR major_name_var LIKE '%通信%' THEN
                '电子信息行业发展稳定，5G技术应用带来新机遇。'
            WHEN major_name_var LIKE '%医%' OR major_name_var LIKE '%药%' OR major_name_var LIKE '%护理%' THEN
                '医疗健康行业需求稳定，老龄化趋势增加医疗服务需求。'
            WHEN major_name_var LIKE '%金融%' OR major_name_var LIKE '%经济%' OR major_name_var LIKE '%会计%' THEN
                '金融行业竞争激烈，但高端人才需求旺盛。'
            WHEN major_name_var LIKE '%管理%' THEN
                '管理类人才需求广泛，但需要复合型能力。'
            WHEN category_name_var LIKE '%工学%' THEN
                '工程技术领域就业稳定，制造业升级带来新需求。'
            WHEN category_name_var LIKE '%理学%' THEN
                '基础研究领域需深入专业知识，交叉学科应用前景广。'
            WHEN category_name_var LIKE '%文学%' THEN
                '人文领域就业面广，新媒体和文化创意产业提供新机会。'
            ELSE '该专业领域就业情况受市场影响，建议关注行业动态。'
            END AS industry_outlook,
        CASE
            WHEN RAND() > 0.7 THEN '高需求行业'
            WHEN RAND() > 0.4 THEN '稳定需求行业'
            ELSE '竞争激烈行业'
            END AS demand_level,
        CASE
            WHEN RAND() > 0.6 THEN '建议积极参与实习实践，提前积累行业经验'
            WHEN RAND() > 0.3 THEN '建议关注行业前沿技术，提升专业核心竞争力'
            ELSE '建议培养跨学科能力，拓展就业选择面'
            END AS career_advice,
        (SELECT GROUP_CONCAT(m2.major_name SEPARATOR '、')
         FROM major m2
         WHERE m2.discipline_category_id = (SELECT discipline_category_id FROM major WHERE major_id = maj_id)
           AND m2.major_id != maj_id
         LIMIT 5) AS related_majors;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `analyze_province_score_trends` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `analyze_province_score_trends`(
    IN prov_id INT,
    IN disc_category_id INT
)
proc_label: BEGIN
    DECLARE province_exists INT DEFAULT 0;
    DECLARE category_exists INT DEFAULT 0;
    DECLARE province_name_var VARCHAR(20);
    DECLARE region_var VARCHAR(20);
    DECLARE category_name_var VARCHAR(30);

    -- 检查省份是否存在
    SELECT COUNT(*) INTO province_exists FROM province WHERE province_id = prov_id;

    IF province_exists > 0 THEN
        SELECT province_name, region INTO province_name_var, region_var
        FROM province WHERE province_id = prov_id LIMIT 1;
    END IF;

    -- 检查学科门类是否存在
    SELECT COUNT(*) INTO category_exists FROM discipline_category WHERE category_id = disc_category_id;

    IF category_exists > 0 THEN
        SELECT category_name INTO category_name_var
        FROM discipline_category WHERE category_id = disc_category_id LIMIT 1;
    END IF;

    -- 如果数据不存在，显示错误
    IF province_exists = 0 OR category_exists = 0 THEN
        SELECT
            CONCAT('错误: ',
                   IF(province_exists = 0, '省份ID不存在', ''),
                   IF(category_exists = 0, '学科门类ID不存在', '')
            ) AS message;
        LEAVE proc_label;
    END IF;

    -- 返回单一结果集，包含所有信息
    SELECT
        prov_id AS province_id,
        province_name_var AS province_name,
        region_var AS region,
        disc_category_id AS category_id,
        category_name_var AS category_name,

        -- 最低分数线趋势（模拟数据）
        ROUND(400 + RAND() * 50) AS min_score_current_year,
        ROUND(390 + RAND() * 50) AS min_score_last_year,
        ROUND(380 + RAND() * 50) AS min_score_two_years_ago,

        -- 平均分数线趋势（模拟数据）
        ROUND(500 + RAND() * 50) AS avg_score_current_year,
        ROUND(490 + RAND() * 50) AS avg_score_last_year,
        ROUND(480 + RAND() * 50) AS avg_score_two_years_ago,

        -- 最高分数线趋势（模拟数据）
        ROUND(550 + RAND() * 50) AS max_score_current_year,
        ROUND(540 + RAND() * 50) AS max_score_last_year,
        ROUND(530 + RAND() * 50) AS max_score_two_years_ago,

        -- 分数线趋势分析
        CASE
            WHEN RAND() > 0.6 THEN '分数线呈上升趋势，竞争日益激烈'
            WHEN RAND() > 0.3 THEN '分数线基本稳定，竞争强度适中'
            ELSE '分数线略有波动，关注年度变化'
            END AS score_trend_analysis,

        -- 热门大学（简化处理）
        '该省份热门高校' AS popular_universities,

        -- 录取难度评估
        CASE
            WHEN RAND() > 0.7 THEN '高'
            WHEN RAND() > 0.4 THEN '中'
            ELSE '适中'
            END AS admission_difficulty,

        -- 建议
        CASE
            WHEN RAND() > 0.5 THEN '建议关注一流大学和特色学科建设高校'
            ELSE '建议考虑该省份优势学科和重点专业'
            END AS recommendation;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `analyze_student_volunteer_strategy` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `analyze_student_volunteer_strategy`(
    IN stu_id VARCHAR(18)
)
proc_label: BEGIN
    DECLARE stu_score DECIMAL(5,1);
    DECLARE stu_province_id INT;
    DECLARE stu_exists INT DEFAULT 0;
    DECLARE stu_name VARCHAR(50);

    -- 检查学生是否存在
    SELECT COUNT(*) INTO stu_exists FROM student WHERE student_id = stu_id;

    IF stu_exists = 0 THEN
        SELECT '错误: 学生ID不存在' AS message;
        LEAVE proc_label;
    END IF;

    -- 获取学生基本信息
    SELECT
        student_name,
        college_entrance_exam_score,
        province_id
    INTO
        stu_name,
        stu_score,
        stu_province_id
    FROM student
    WHERE student_id = stu_id;

    -- 显示学生基本信息（调试用）
    SELECT
        stu_id AS student_id,
        stu_name AS student_name,
        stu_score AS college_entrance_exam_score,
        stu_province_id AS province_id;

    -- 显示该学生的志愿与历史录取分数对比（调试用）
    SELECT
        vr.volunteer_order,
        u.university_name,
        m.major_name,
        ha.admission_number AS original_admission_score,
        IFNULL(ha.admission_number, 0) AS processed_admission_score,
        stu_score AS student_score,
        stu_score - 20 AS lower_bound,
        stu_score + 20 AS upper_bound,
        CASE
            WHEN ha.admission_number IS NULL THEN '无历史数据'
            WHEN ha.admission_number > stu_score + 20 THEN '冲刺志愿'
            WHEN ha.admission_number BETWEEN stu_score - 20 AND stu_score + 20 THEN '稳妥志愿'
            ELSE '保底志愿'
            END AS volunteer_type_with_null,
        CASE
            WHEN IFNULL(ha.admission_number, 0) > stu_score + 20 THEN '冲刺志愿'
            WHEN IFNULL(ha.admission_number, 0) BETWEEN stu_score - 20 AND stu_score + 20 THEN '稳妥志愿'
            ELSE '保底志愿'
            END AS volunteer_type_with_zero
    FROM volunteer_relation vr
             JOIN university u ON vr.university_id = u.university_id
             JOIN major m ON vr.major_id = m.major_id
             LEFT JOIN historical_admission ha ON vr.university_id = ha.university_id
        AND vr.major_id = ha.major_id
    WHERE vr.student_id = stu_id
    ORDER BY vr.volunteer_order;

    -- 检查历史录取数据是否存在
    SELECT
        COUNT(*) AS total_volunteers,
        SUM(CASE WHEN ha.admission_number IS NOT NULL THEN 1 ELSE 0 END) AS volunteers_with_admission_data,
        SUM(CASE WHEN ha.admission_number IS NULL THEN 1 ELSE 0 END) AS volunteers_without_admission_data
    FROM volunteer_relation vr
             LEFT JOIN historical_admission ha ON vr.university_id = ha.university_id
        AND vr.major_id = ha.major_id
    WHERE vr.student_id = stu_id;

    -- 修改后的志愿类型判断（使用专业分数线作为备选）
    SELECT
        vr.volunteer_order,
        u.university_name,
        m.major_name,
        IFNULL(ha.admission_number, m.line_score) AS reference_score,
        CASE
            WHEN ha.admission_number IS NULL THEN '使用专业分数线'
            ELSE '使用历史录取分数'
            END AS score_source,
        CASE
            WHEN IFNULL(ha.admission_number, m.line_score) > stu_score + 20 THEN '冲刺志愿'
            WHEN IFNULL(ha.admission_number, m.line_score) BETWEEN stu_score - 20 AND stu_score + 20 THEN '稳妥志愿'
            ELSE '保底志愿'
            END AS improved_volunteer_type
    FROM volunteer_relation vr
             JOIN university u ON vr.university_id = u.university_id
             JOIN major m ON vr.major_id = m.major_id
             LEFT JOIN historical_admission ha ON vr.university_id = ha.university_id
        AND vr.major_id = ha.major_id
    WHERE vr.student_id = stu_id
    ORDER BY vr.volunteer_order;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `analyze_university_major_match` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `analyze_university_major_match`(
    IN uni_id VARCHAR(10),
    IN maj_id VARCHAR(10)
)
proc_label: BEGIN
    DECLARE university_exists INT DEFAULT 0;
    DECLARE major_exists INT DEFAULT 0;
    DECLARE match_exists INT DEFAULT 0;

    -- 检查大学和专业是否存在
    SELECT COUNT(*) INTO university_exists FROM university WHERE university_id = uni_id;
    SELECT COUNT(*) INTO major_exists FROM major WHERE major_id = maj_id;

    -- 如果大学或专业不存在，显示错误信息
    IF university_exists = 0 OR major_exists = 0 THEN
        SELECT
            CONCAT('错误: ',
                   IF(university_exists = 0, '大学ID不存在', ''),
                   IF(major_exists = 0, ' 专业ID不存在', '')
            ) AS message,
            (SELECT GROUP_CONCAT(university_id, ': ', university_name)
             FROM university LIMIT 5) AS available_universities,
            (SELECT GROUP_CONCAT(major_id, ': ', major_name)
             FROM major LIMIT 5) AS available_majors;
        LEAVE proc_label;
    END IF;

    -- 检查该大学是否有该专业
    SELECT COUNT(*) INTO match_exists
    FROM enrollment_plan
    WHERE university_id = uni_id AND major_id = maj_id;

    -- 如果没有匹配记录，给出提示但继续执行
    IF match_exists = 0 THEN
        SELECT '注意: 该大学可能没有开设此专业，以下分析基于假设该专业存在' AS warning;
    END IF;

    -- 获取大学和专业的基本信息
    SELECT
        u.university_name,
        u.level,
        u.ranking,
        m.major_name,
        dc.category_name,
        m.line_score
    INTO
        @uni_name,
        @uni_level,
        @uni_rank,
        @major_name,
        @category_name,
        @line_score
    FROM university u
             CROSS JOIN major m
             JOIN discipline_category dc ON m.discipline_category_id = dc.category_id
    WHERE u.university_id = uni_id AND m.major_id = maj_id;

    -- 获取招生计划和历史录取数据
    SELECT
        IFNULL(MAX(ep.enrollment_number), 0),
        IFNULL(MAX(ha.admission_number), 0)
    INTO
        @enrollment_number,
        @admission_number
    FROM university u
             CROSS JOIN major m
             LEFT JOIN enrollment_plan ep ON u.university_id = ep.university_id
        AND m.major_id = ep.major_id
             LEFT JOIN historical_admission ha ON u.university_id = ha.university_id
        AND m.major_id = ha.major_id
    WHERE u.university_id = uni_id AND m.major_id = maj_id;

    -- 计算志愿人数
    SELECT COUNT(*) INTO @popularity_count
    FROM volunteer_relation
    WHERE university_id = uni_id AND major_id = maj_id;

    -- 返回单一结果集，包含所有信息
    SELECT
        uni_id AS university_id,
        @uni_name AS university_name,
        @uni_level AS level,
        @uni_rank AS university_ranking,
        maj_id AS major_id,
        @major_name AS major_name,
        @category_name AS discipline_category,
        @line_score AS major_line_score,
        @enrollment_number AS planned_enrollment,
        @admission_number AS last_year_admission_score,
        @popularity_count AS popularity_count,

        -- 大学层次评价
        CASE
            WHEN @uni_level = '985工程' THEN '顶尖'
            WHEN @uni_level = '211工程' THEN '一流'
            WHEN @uni_rank <= 100 THEN '优秀'
            WHEN @uni_rank <= 300 THEN '良好'
            ELSE '一般'
            END AS university_tier,

        -- 专业热度评价
        CASE
            WHEN @line_score >= 600 THEN '热门专业'
            WHEN @line_score >= 550 THEN '较热门专业'
            ELSE '普通专业'
            END AS major_popularity,

        -- 匹配度评价
        CASE
            WHEN @uni_level IN ('985工程', '211工程') AND @line_score >= 600 THEN '优质匹配：名校热门专业'
            WHEN @uni_level IN ('985工程', '211工程') THEN '良好匹配：名校专业'
            WHEN @uni_rank <= 100 AND @line_score >= 550 THEN '良好匹配：优质大学热门专业'
            WHEN @line_score >= 600 THEN '中等匹配：热门专业'
            ELSE '一般匹配'
            END AS match_evaluation,

        -- 就业前景评价
        CASE
            WHEN @uni_level IN ('985工程', '211工程') AND @line_score >= 600 THEN '就业前景优秀'
            WHEN @uni_level IN ('985工程', '211工程') OR @line_score >= 600 THEN '就业前景良好'
            WHEN @uni_rank <= 200 OR @line_score >= 550 THEN '就业前景中上'
            ELSE '就业前景一般'
            END AS employment_prospect,

        -- 录取难度评价
        CASE
            WHEN @admission_number > 650 THEN '录取难度高'
            WHEN @admission_number > 600 THEN '录取难度中等'
            WHEN @admission_number > 550 THEN '录取难度适中'
            ELSE '录取难度较低'
            END AS admission_difficulty,

        -- 相似专业推荐
        (SELECT GROUP_CONCAT(major_name SEPARATOR '、')
         FROM (
                  SELECT m2.major_name
                  FROM major m
                           JOIN major m2 ON m.discipline_category_id = m2.discipline_category_id AND m.major_id != m2.major_id
                  WHERE m.major_id = maj_id
                  ORDER BY ABS(m.line_score - m2.line_score)
                  LIMIT 3
              ) AS similar_majors
        ) AS similar_majors_at_university,

        -- 推荐指数
        CASE
            WHEN @uni_level IN ('985工程', '211工程') AND @line_score >= 600 THEN 5
            WHEN @uni_level IN ('985工程', '211工程') OR @line_score >= 600 THEN 4
            WHEN @uni_rank <= 200 OR @line_score >= 550 THEN 3
            ELSE 2
            END AS recommendation_score,

        -- 备注
        CASE
            WHEN match_exists = 0 THEN '注意：该大学可能未开设此专业，请确认'
            WHEN @enrollment_number = 0 THEN '注意：未找到招生计划数据'
            WHEN @admission_number = 0 THEN '注意：未找到历史录取数据'
            ELSE '数据完整'
            END AS remarks;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `check_volunteer_conflict` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `check_volunteer_conflict`(IN stu_id VARCHAR(18))
BEGIN
    -- 首先检查学生是否存在
    SELECT COUNT(*) INTO @student_exists FROM student WHERE student_id = stu_id;
    SELECT CONCAT('学生存在: ', @student_exists) AS debug_info;

    -- 检查学生志愿数量
    SELECT COUNT(*) INTO @volunteer_count FROM volunteer_relation WHERE student_id = stu_id;
    SELECT CONCAT('志愿数量: ', @volunteer_count) AS debug_info;

    -- 检查历史录取数据
    SELECT
        COUNT(*) INTO @admission_count
    FROM volunteer_relation vr
             JOIN historical_admission ha ON vr.university_id = ha.university_id
        AND vr.major_id = ha.major_id
    WHERE vr.student_id = stu_id;
    SELECT CONCAT('匹配的历史录取数据: ', @admission_count) AS debug_info;

    -- 检查年份匹配情况
    SELECT
        COUNT(*) INTO @year_match_count
    FROM volunteer_relation vr
             JOIN historical_admission ha ON vr.university_id = ha.university_id
        AND vr.major_id = ha.major_id
        AND ha.year = YEAR(CURDATE()) - 1
    WHERE vr.student_id = stu_id;
    SELECT CONCAT('去年录取数据匹配: ', @year_match_count) AS debug_info;

    -- 显示所有志愿的分数线情况(不筛选年份)
    SELECT
        vr.volunteer_order,
        u.university_name,
        m.major_name,
        ha.year,
        ha.admission_number
    FROM volunteer_relation vr
             JOIN university u ON vr.university_id = u.university_id
             JOIN major m ON vr.major_id = m.major_id
             LEFT JOIN historical_admission ha ON vr.university_id = ha.university_id
        AND vr.major_id = ha.major_id
    WHERE vr.student_id = stu_id
    ORDER BY vr.volunteer_order;

    -- 修改后的志愿检测查询(放宽年份限制)
    SELECT
        vr1.volunteer_order AS order1,
        u1.university_name AS uni1,
        m1.major_name AS major1,
        ha1.admission_number AS score1,
        vr2.volunteer_order AS order2,
        u2.university_name AS uni2,
        m2.major_name AS major2,
        ha2.admission_number AS score2,
        CASE
            WHEN ha1.admission_number < ha2.admission_number THEN '风险：志愿倒挂'
            WHEN ha1.admission_number - ha2.admission_number < 10 THEN '提示：梯度不足'
            ELSE '正常'
            END AS advice
    FROM volunteer_relation vr1
             JOIN volunteer_relation vr2 ON vr1.student_id = vr2.student_id
        AND vr1.volunteer_order < vr2.volunteer_order
             JOIN university u1 ON vr1.university_id = u1.university_id
             JOIN major m1 ON vr1.major_id = m1.major_id
             JOIN university u2 ON vr2.university_id = u2.university_id
             JOIN major m2 ON vr2.major_id = m2.major_id
             JOIN historical_admission ha1 ON vr1.university_id = ha1.university_id
        AND vr1.major_id = ha1.major_id
             JOIN historical_admission ha2 ON vr2.university_id = ha2.university_id
        AND vr2.major_id = ha2.major_id
    WHERE vr1.student_id = stu_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `flexible_recommendation` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `flexible_recommendation`(IN stu_id varchar(18))
BEGIN
    DECLARE stu_score DECIMAL(5,1);  -- 声明变量存储学生成绩

    -- 获取学生成绩，使用IFNULL函数处理NULL值
    -- 如果成绩为NULL，则默认设为600分
    SELECT IFNULL(college_entrance_exam_score, 600) INTO stu_score
    FROM student WHERE student_id = stu_id;

    -- 双重保险：如果没有找到学生记录，stu_score会是NULL
    -- 此时再次设置默认分数
    IF stu_score IS NULL THEN
        SET stu_score = 600;  -- 设置默认分数为600
    END IF;

    -- 输出调试信息，显示当前使用的学生分数
    -- 这有助于确认是使用了实际分数还是默认分数
    SELECT stu_score AS student_score;

    -- 核心查询：推荐匹配的大学和专业
    SELECT
        u.university_name,              -- 大学名称
        m.major_name,                   -- 专业名称
        m.line_score AS last_year_admission,  -- 去年录取分数
        ABS(stu_score - ha.admission_number) AS score_difference  -- 计算分数差距
    FROM university u
             JOIN historical_admission ha ON u.university_id = ha.university_id  -- 关联历史录取数据
             JOIN major m ON ha.major_id = m.major_id  -- 关联专业信息
    ORDER BY score_difference  -- 按分数差距排序，差距越小越靠前
    LIMIT 20;  -- 限制返回20条记录
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_student_volunteers` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_student_volunteers`(IN stu_id VARCHAR(18))
BEGIN
    SELECT vr.volunteer_order, u.university_name, m.major_name
    FROM volunteer_relation vr
    JOIN university u ON vr.university_id = u.university_id
    JOIN major m ON vr.major_id = m.major_id
    WHERE vr.student_id = stu_id
    ORDER BY vr.volunteer_order;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `predict_admission_probability` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `predict_admission_probability`(
    IN stu_id VARCHAR(18),
    IN uni_id VARCHAR(10),
    IN maj_id VARCHAR(10)
)
BEGIN
    DECLARE stu_score DECIMAL(5,1);
    DECLARE avg_admit DECIMAL(5,1);
    DECLARE std_dev DECIMAL(5,1);
    DECLARE z_score DECIMAL(10,6);
    DECLARE prob DECIMAL(5,2);
    DECLARE record_count INT;

    -- 获取学生成绩
    SELECT IFNULL(college_entrance_exam_score, 0) INTO stu_score
    FROM student WHERE student_id = stu_id;

    -- 检查是否有历史录取数据
    SELECT COUNT(*) INTO record_count
    FROM historical_admission
    WHERE university_id = uni_id AND major_id = maj_id;

    -- 如果没有历史数据，使用专业分数线
    IF record_count = 0 THEN
        SELECT line_score INTO avg_admit FROM major WHERE major_id = maj_id;
        SET std_dev = 10; -- 默认标准差
    ELSE
        -- 计算历年平均录取线和标准差
        SELECT
            AVG(admission_number),
            IFNULL(STDDEV(admission_number), 10)
        INTO avg_admit, std_dev
        FROM historical_admission
        WHERE university_id = uni_id AND major_id = maj_id;
    END IF;

    -- 确保平均分和标准差不为NULL
    SET avg_admit = IFNULL(avg_admit, 600);
    SET std_dev = IFNULL(std_dev, 10);

    -- 使用正态分布计算概率
    IF std_dev > 0 THEN
        -- 计算z-score
        SET z_score = (stu_score - avg_admit) / std_dev;

        -- 使用更准确的正态分布累积函数近似
        IF z_score < 0 THEN
            SET prob = 50 - 50 * (1 - EXP(-0.717 * z_score - 0.416 * z_score * z_score));
        ELSE
            SET prob = 50 + 50 * (1 - EXP(-0.717 * z_score - 0.416 * z_score * z_score));
        END IF;
    ELSE
        SET prob = IF(stu_score >= avg_admit, 100, 0);
    END IF;

    -- 返回结果
    SELECT
        u.university_name,
        m.major_name,
        stu_score AS your_score,
        avg_admit AS avg_admission_score,
        std_dev AS score_std_dev,
        record_count AS historical_data_count,
        ROUND(GREATEST(0, LEAST(100, prob)), 2) AS admission_probability,
        CASE
            WHEN prob > 80 THEN '高概率'
            WHEN prob > 50 THEN '中等概率'
            WHEN prob > 20 THEN '低概率'
            ELSE '极小概率'
            END AS probability_level
    FROM university u
             JOIN major m ON m.major_id = maj_id
    WHERE u.university_id = uni_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-02 17:01:13
