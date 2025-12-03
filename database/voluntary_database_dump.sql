-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: voluntary_database
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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学科门类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discipline_category`
--

LOCK TABLES `discipline_category` WRITE;
/*!40000 ALTER TABLE `discipline_category` DISABLE KEYS */;
INSERT INTO `discipline_category` (`category_id`, `category_name`, `code`) VALUES (1,'哲学','01'),(2,'经济学','02'),(3,'法学','03'),(4,'教育学','04'),(5,'文学','05'),(6,'历史学','06'),(7,'理学','07'),(8,'工学','08'),(9,'农学','09'),(10,'医学','10'),(11,'管理学','12'),(12,'艺术学','13');
/*!40000 ALTER TABLE `discipline_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enrollment_plan`
--

DROP TABLE IF EXISTS `enrollment_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enrollment_plan` (
  `plan_id` int NOT NULL AUTO_INCREMENT,
  `university_id` varchar(10) NOT NULL,
  `major_id` varchar(10) NOT NULL,
  `enrollment_year` year NOT NULL,
  `enrollment_number` int NOT NULL,
  PRIMARY KEY (`plan_id`),
  UNIQUE KEY `uniq_plan` (`university_id`,`major_id`,`enrollment_year`),
  KEY `fk_plan_major` (`major_id`),
  CONSTRAINT `fk_plan_major` FOREIGN KEY (`major_id`) REFERENCES `major` (`major_id`),
  CONSTRAINT `fk_plan_uni` FOREIGN KEY (`university_id`) REFERENCES `university` (`university_id`),
  CONSTRAINT `enrollment_plan_chk_1` CHECK ((`enrollment_number` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='招生计划表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollment_plan`
--

LOCK TABLES `enrollment_plan` WRITE;
/*!40000 ALTER TABLE `enrollment_plan` DISABLE KEYS */;
INSERT INTO `enrollment_plan` (`plan_id`, `university_id`, `major_id`, `enrollment_year`, `enrollment_number`) VALUES (1,'10001','080901',2024,180),(2,'10001','050101',2024,80),(3,'10001','120204',2024,90),(4,'10002','080901',2024,200),(5,'10002','080902',2024,120),(6,'10002','080903',2024,110),(7,'10003','080901',2024,150),(8,'10003','050201',2024,70),(9,'10003','120203',2024,60),(10,'10004','100101',2024,220),(11,'10004','100202',2024,150),(12,'10005','080904',2024,160),(13,'10005','040101',2024,90),(14,'10006','071001',2024,140),(15,'10006','080901',2024,120),(16,'10007','040101',2024,130),(17,'10007','050101',2024,110),(18,'10008','120203',2024,100),(19,'10008','120204',2024,120),(20,'10009','100101',2024,180),(21,'10009','100202',2024,130),(22,'10010','040101',2024,160),(23,'10010','050101',2024,90),(24,'10011','120203',2024,110),(25,'10011','120204',2024,100),(26,'10012','100101',2024,200),(27,'10012','100202',2024,150);
/*!40000 ALTER TABLE `enrollment_plan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historical_admission`
--

DROP TABLE IF EXISTS `historical_admission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historical_admission` (
  `data_id` int NOT NULL AUTO_INCREMENT,
  `university_id` varchar(10) NOT NULL,
  `major_id` varchar(10) NOT NULL,
  `year` year NOT NULL,
  `admission_number` int NOT NULL COMMENT '录取分数线',
  PRIMARY KEY (`data_id`),
  UNIQUE KEY `uniq_hist` (`university_id`,`major_id`,`year`),
  KEY `fk_hist_major` (`major_id`),
  CONSTRAINT `fk_hist_major` FOREIGN KEY (`major_id`) REFERENCES `major` (`major_id`),
  CONSTRAINT `fk_hist_uni` FOREIGN KEY (`university_id`) REFERENCES `university` (`university_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='历年录取数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historical_admission`
--

LOCK TABLES `historical_admission` WRITE;
/*!40000 ALTER TABLE `historical_admission` DISABLE KEYS */;
INSERT INTO `historical_admission` (`data_id`, `university_id`, `major_id`, `year`, `admission_number`) VALUES (1,'10001','080901',2023,674),(2,'10001','080901',2022,672),(3,'10001','080901',2021,669),(4,'10001','050101',2023,640),(5,'10001','050101',2022,638),(6,'10001','120204',2023,650),(7,'10002','080901',2023,680),(8,'10002','080901',2022,678),(9,'10002','080902',2023,668),(10,'10002','080903',2023,669),(11,'10003','080901',2023,663),(12,'10003','050201',2023,623),(13,'10003','120203',2023,631),(14,'10004','100101',2023,693),(15,'10004','100202',2023,678),(16,'10005','080904',2023,655),(17,'10005','040101',2023,608),(18,'10006','071001',2023,630),(19,'10006','080901',2023,648),(20,'10007','040101',2023,603),(21,'10007','050101',2023,618),(22,'10008','120203',2023,610),(23,'10008','120204',2023,622),(24,'10009','100101',2023,670),(25,'10009','100202',2023,655),(26,'10010','040101',2023,595),(27,'10010','050101',2023,602),(28,'10011','120203',2023,628),(29,'10011','120204',2023,636),(30,'10012','100101',2023,663),(31,'10012','100202',2023,651);
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
  `discipline_category_id` int NOT NULL,
  `schooling_length` tinyint NOT NULL,
  `degree_type` varchar(20) NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `line_score` int DEFAULT NULL,
  PRIMARY KEY (`major_id`),
  KEY `fk_major_cat` (`discipline_category_id`),
  CONSTRAINT `fk_major_cat` FOREIGN KEY (`discipline_category_id`) REFERENCES `discipline_category` (`category_id`),
  CONSTRAINT `major_chk_1` CHECK ((`schooling_length` between 2 and 8))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='专业表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `major`
--

LOCK TABLES `major` WRITE;
/*!40000 ALTER TABLE `major` DISABLE KEYS */;
INSERT INTO `major` (`major_id`, `major_name`, `discipline_category_id`, `schooling_length`, `degree_type`, `create_time`, `line_score`) VALUES ('040101','教育学',4,4,'教育学学士','2025-12-02 17:13:16',590),('050101','汉语言文学',5,4,'文学学士','2025-12-02 17:13:16',610),('050201','英语',5,4,'文学学士','2025-12-02 17:13:16',600),('071001','信息与计算科学',7,4,'理学学士','2025-12-02 17:13:16',620),('080901','计算机科学与技术',8,4,'工学学士','2025-12-02 17:13:16',650),('080902','软件工程',8,4,'工学学士','2025-12-02 17:13:16',640),('080903','人工智能',8,4,'工学学士','2025-12-02 17:13:16',645),('080904','数据科学与大数据技术',8,4,'工学学士','2025-12-02 17:13:16',638),('100101','临床医学',10,5,'医学学士','2025-12-02 17:13:16',680),('100202','口腔医学',10,5,'医学学士','2025-12-02 17:13:16',665),('120203','会计学',12,4,'管理学学士','2025-12-02 17:13:16',620),('120204','金融学',12,4,'经济学学士','2025-12-02 17:13:16',630);
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='专业热度统计';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `major_hotness`
--

LOCK TABLES `major_hotness` WRITE;
/*!40000 ALTER TABLE `major_hotness` DISABLE KEYS */;
INSERT INTO `major_hotness` (`university_id`, `major_id`, `current_year`, `volunteer_count`, `first_choice_count`, `last_updated`, `university_name`, `major_name`) VALUES ('10001','050101',2025,3,2,'2025-12-02 17:13:17','北京大学','汉语言文学'),('10002','080901',2025,5,3,'2025-12-02 17:13:17','清华大学','计算机科学与技术'),('10004','100101',2025,4,4,'2025-12-02 17:13:17','上海交通大学','临床医学');
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='省份表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `province`
--

LOCK TABLES `province` WRITE;
/*!40000 ALTER TABLE `province` DISABLE KEYS */;
INSERT INTO `province` (`province_id`, `province_name`, `region`) VALUES (1,'北京市','华北'),(2,'天津市','华北'),(3,'河北省','华北'),(4,'上海市','华东'),(5,'江苏省','华东'),(6,'浙江省','华东'),(7,'安徽省','华东'),(8,'湖北省','华中'),(9,'广东省','华南'),(10,'四川省','西南');
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
  `student_name` varchar(50) NOT NULL,
  `id_card` varchar(18) NOT NULL,
  `gender` char(1) DEFAULT NULL,
  `college_entrance_exam_score` decimal(5,1) NOT NULL,
  `province_id` int NOT NULL,
  `ranking` int DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`student_id`),
  UNIQUE KEY `id_card` (`id_card`),
  KEY `fk_student_province` (`province_id`),
  CONSTRAINT `fk_student_province` FOREIGN KEY (`province_id`) REFERENCES `province` (`province_id`),
  CONSTRAINT `student_chk_1` CHECK ((`gender` in (_utf8mb4'男',_utf8mb4'女')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` (`student_id`, `student_name`, `id_card`, `gender`, `college_entrance_exam_score`, `province_id`, `ranking`, `create_time`) VALUES ('20230001','洪笃胜','110101200001010011','男',652.0,1,780,'2025-12-02 17:13:17'),('20230002','侯锐霖','110101200001020022','男',642.0,1,1800,'2025-12-02 17:13:17'),('20230003','曹粮欢','120101200001030033','男',625.5,2,2600,'2025-12-02 17:13:17'),('20230004','陈凯','320101200001040044','男',603.0,5,5200,'2025-12-02 17:13:17'),('20230005','陈然','310101200001050055','女',588.0,4,7800,'2025-12-02 17:13:17'),('20230006','苏航','330101200001060066','男',612.5,6,4300,'2025-12-02 17:13:17'),('20230007','林雨','440101200001070077','女',635.0,9,2300,'2025-12-02 17:13:17'),('20230008','刘泽','510101200001080088','男',598.0,10,9100,'2025-12-02 17:13:17');
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_user`
--

DROP TABLE IF EXISTS `system_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` varchar(20) NOT NULL,
  `student_id` varchar(18) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` int DEFAULT '1',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `student_id` (`student_id`),
  CONSTRAINT `fk_user_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`),
  CONSTRAINT `system_user_chk_1` CHECK ((`role` in (_utf8mb4'admin',_utf8mb4'student')))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_user`
--

LOCK TABLES `system_user` WRITE;
/*!40000 ALTER TABLE `system_user` DISABLE KEYS */;
INSERT INTO `system_user` (`user_id`, `username`, `password`, `role`, `student_id`, `create_time`, `status`) VALUES (1,'admin','123456','admin',NULL,'2025-12-02 17:13:17',1),(2,'hongdusheng','123456','student','20230001','2025-12-02 17:13:17',1),(3,'houruilin','123456','student','20230002','2025-12-02 17:13:17',1),(4,'caolianghuan','123456','student','20230003','2025-12-02 17:13:17',1),(5,'chenkai','123456','student','20230004','2025-12-02 17:13:17',1),(6,'student5','123456','student','20230005','2025-12-02 17:13:17',1),(7,'student6','123456','student','20230006','2025-12-02 17:13:17',1),(8,'student7','123456','student','20230007','2025-12-02 17:13:17',1),(9,'student8','123456','student','20230008','2025-12-02 17:13:17',1);
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
  `province_id` int NOT NULL,
  `type_id` int NOT NULL,
  `level` varchar(20) NOT NULL COMMENT '层次',
  `ranking` int DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`university_id`),
  UNIQUE KEY `uniq_rank` (`ranking`),
  KEY `fk_uni_province` (`province_id`),
  KEY `fk_uni_type` (`type_id`),
  CONSTRAINT `fk_uni_province` FOREIGN KEY (`province_id`) REFERENCES `province` (`province_id`),
  CONSTRAINT `fk_uni_type` FOREIGN KEY (`type_id`) REFERENCES `university_type` (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='院校表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `university`
--

LOCK TABLES `university` WRITE;
/*!40000 ALTER TABLE `university` DISABLE KEYS */;
INSERT INTO `university` (`university_id`, `university_name`, `province_id`, `type_id`, `level`, `ranking`, `create_time`) VALUES ('10001','北京大学',1,1,'双一流A',1,'2025-12-02 17:13:16'),('10002','清华大学',1,2,'双一流A',2,'2025-12-02 17:13:16'),('10003','复旦大学',4,1,'双一流A',3,'2025-12-02 17:13:16'),('10004','上海交通大学',4,2,'双一流A',4,'2025-12-02 17:13:16'),('10005','浙江大学',6,1,'双一流A',5,'2025-12-02 17:13:16'),('10006','南京大学',5,1,'双一流A',7,'2025-12-02 17:13:16'),('10007','武汉大学',8,1,'双一流A',9,'2025-12-02 17:13:16'),('10008','中山大学',9,1,'双一流A',11,'2025-12-02 17:13:16'),('10009','四川大学',10,1,'双一流A',15,'2025-12-02 17:13:16'),('10010','华中师范大学',8,4,'双一流B',35,'2025-12-02 17:13:16'),('10011','上海财经大学',4,5,'双一流B',40,'2025-12-02 17:13:16'),('10012','南方医科大学',9,3,'双一流B',65,'2025-12-02 17:13:16');
/*!40000 ALTER TABLE `university` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `university_major_line`
--

DROP TABLE IF EXISTS `university_major_line`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `university_major_line` (
  `id` int NOT NULL AUTO_INCREMENT,
  `university_id` varchar(10) NOT NULL,
  `major_id` varchar(10) NOT NULL,
  `year` year NOT NULL,
  `line_score` int NOT NULL,
  `remark` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_uml` (`university_id`,`major_id`,`year`),
  KEY `fk_uml_major` (`major_id`),
  CONSTRAINT `fk_uml_major` FOREIGN KEY (`major_id`) REFERENCES `major` (`major_id`),
  CONSTRAINT `fk_uml_uni` FOREIGN KEY (`university_id`) REFERENCES `university` (`university_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='院校-专业年度分数线';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `university_major_line`
--

LOCK TABLES `university_major_line` WRITE;
/*!40000 ALTER TABLE `university_major_line` DISABLE KEYS */;
INSERT INTO `university_major_line` (`id`, `university_id`, `major_id`, `year`, `line_score`, `remark`) VALUES (1,'10001','080901',2024,672,NULL),(2,'10001','050101',2024,641,NULL),(3,'10002','080901',2024,679,NULL),(4,'10002','080902',2024,667,NULL),(5,'10004','100101',2024,692,NULL),(6,'10005','080904',2024,654,NULL),(7,'10009','100101',2024,668,NULL),(8,'10012','100202',2024,650,NULL);
/*!40000 ALTER TABLE `university_major_line` ENABLE KEYS */;
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
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`type_id`),
  UNIQUE KEY `type_name` (`type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='院校类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `university_type`
--

LOCK TABLES `university_type` WRITE;
/*!40000 ALTER TABLE `university_type` DISABLE KEYS */;
INSERT INTO `university_type` (`type_id`, `type_name`, `description`) VALUES (1,'综合类','学科齐全的综合性大学'),(2,'理工类','以工科/理科为主'),(3,'医药类','医学、药学特色'),(4,'师范类','教师教育特色'),(5,'财经类','经济管理见长');
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
  `student_id` varchar(18) NOT NULL,
  `operation_type` enum('INSERT','UPDATE','DELETE') NOT NULL,
  `university_id_old` varchar(10) DEFAULT NULL,
  `university_name_old` varchar(100) DEFAULT NULL,
  `major_id_old` varchar(10) DEFAULT NULL,
  `volunteer_order_old` tinyint DEFAULT NULL,
  `university_id_new` varchar(10) DEFAULT NULL,
  `university_name_new` varchar(100) DEFAULT NULL,
  `major_id_new` varchar(10) DEFAULT NULL,
  `volunteer_order_new` tinyint DEFAULT NULL,
  `change_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `changed_by` varchar(50) NOT NULL,
  PRIMARY KEY (`audit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='志愿审计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `volunteer_audit`
--

LOCK TABLES `volunteer_audit` WRITE;
/*!40000 ALTER TABLE `volunteer_audit` DISABLE KEYS */;
INSERT INTO `volunteer_audit` (`audit_id`, `student_id`, `operation_type`, `university_id_old`, `university_name_old`, `major_id_old`, `volunteer_order_old`, `university_id_new`, `university_name_new`, `major_id_new`, `volunteer_order_new`, `change_time`, `changed_by`) VALUES (1,'20230001','UPDATE','10002','清华大学','080901',1,'10009','四川大学','100101',1,'2025-12-02 17:32:35','root@localhost'),(2,'20230001','UPDATE','10009','四川大学','100101',1,'10002','清华大学','080901',1,'2025-12-02 17:33:31','root@localhost'),(3,'20230001','UPDATE','10002','清华大学','080901',1,'10004','上海交通大学','100202',1,'2025-12-02 17:33:32','root@localhost'),(4,'20230002','UPDATE','10001','北京大学','050101',1,'10005','浙江大学','080904',1,'2025-12-02 17:50:17','root@localhost'),(5,'20230002','UPDATE','10005','浙江大学','080904',1,'10001','北京大学','080901',1,'2025-12-02 17:53:23','root@localhost');
/*!40000 ALTER TABLE `volunteer_audit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `volunteer_relation`
--

DROP TABLE IF EXISTS `volunteer_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `volunteer_relation` (
  `relation_id` int NOT NULL AUTO_INCREMENT,
  `student_id` varchar(18) NOT NULL,
  `university_id` varchar(10) NOT NULL,
  `major_id` varchar(10) NOT NULL,
  `volunteer_order` tinyint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`relation_id`),
  UNIQUE KEY `uniq_student_order` (`student_id`,`volunteer_order`),
  KEY `fk_vr_uni` (`university_id`),
  KEY `fk_vr_major` (`major_id`),
  CONSTRAINT `fk_vr_major` FOREIGN KEY (`major_id`) REFERENCES `major` (`major_id`),
  CONSTRAINT `fk_vr_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`),
  CONSTRAINT `fk_vr_uni` FOREIGN KEY (`university_id`) REFERENCES `university` (`university_id`),
  CONSTRAINT `volunteer_relation_chk_1` CHECK ((`volunteer_order` between 1 and 30))
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考生志愿关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `volunteer_relation`
--

LOCK TABLES `volunteer_relation` WRITE;
/*!40000 ALTER TABLE `volunteer_relation` DISABLE KEYS */;
INSERT INTO `volunteer_relation` (`relation_id`, `student_id`, `university_id`, `major_id`, `volunteer_order`, `create_time`) VALUES (1,'20230001','10004','100202',1,'2025-12-02 17:33:32'),(2,'20230001','10001','080901',2,'2025-12-02 17:13:17'),(3,'20230001','10003','080901',3,'2025-12-02 17:13:17'),(4,'20230002','10001','080901',1,'2025-12-02 17:53:23'),(5,'20230002','10005','080904',2,'2025-12-02 17:13:17'),(6,'20230003','10005','080904',1,'2025-12-02 17:13:17'),(7,'20230003','10001','120204',2,'2025-12-02 17:13:17'),(8,'20230003','10004','100202',3,'2025-12-02 17:13:17'),(9,'20230004','10009','100101',1,'2025-12-02 17:13:17'),(10,'20230004','10007','040101',2,'2025-12-02 17:13:17'),(11,'20230005','10010','040101',1,'2025-12-02 17:13:17'),(12,'20230005','10011','120203',2,'2025-12-02 17:13:17'),(13,'20230006','10002','080902',1,'2025-12-02 17:13:17'),(14,'20230006','10001','080901',2,'2025-12-02 17:13:17'),(15,'20230007','10004','100101',1,'2025-12-02 17:13:17'),(16,'20230007','10008','120204',2,'2025-12-02 17:13:17'),(17,'20230008','10012','100101',1,'2025-12-02 17:13:17'),(18,'20230008','10005','040101',2,'2025-12-02 17:13:17');
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
/*!50032 DROP TRIGGER IF EXISTS trg_volunteer_audit_insert */;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_volunteer_audit_insert` AFTER INSERT ON `volunteer_relation` FOR EACH ROW BEGIN
    DECLARE uni_name VARCHAR(100);
    SELECT university_name INTO uni_name FROM university WHERE university_id = NEW.university_id;
    INSERT INTO volunteer_audit (student_id, operation_type,
                                 university_id_new, university_name_new,
                                 major_id_new, volunteer_order_new,
                                 changed_by)
    VALUES (NEW.student_id, 'INSERT', NEW.university_id, IFNULL(uni_name,'未知大学'),
            NEW.major_id, NEW.volunteer_order, CURRENT_USER());
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
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Dumping routines for database 'voluntary_database'
--
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
CREATE DEFINER=`root`@`localhost` PROCEDURE `flexible_recommendation`(IN stu_id VARCHAR(18))
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

-- Dump completed on 2025-12-02 19:59:22
