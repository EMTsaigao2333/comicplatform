-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: comicplatformdb
-- ------------------------------------------------------
-- Server version	8.0.32

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
-- Table structure for table `t_bookshelf`
--

DROP TABLE IF EXISTS `t_bookshelf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_bookshelf` (
  `bookshelfid` int NOT NULL AUTO_INCREMENT COMMENT '书架ID',
  `userid` int NOT NULL COMMENT '用户ID',
  `comicid` int NOT NULL COMMENT '漫画ID',
  `collecttime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`bookshelfid`) USING BTREE,
  KEY `fk_bookshelf_userid` (`userid`) USING BTREE,
  KEY `fk_bookshelf_comicid` (`comicid`) USING BTREE,
  CONSTRAINT `fk_bookshelf_comic` FOREIGN KEY (`comicid`) REFERENCES `t_comic` (`comicid`) ON DELETE CASCADE,
  CONSTRAINT `fk_bookshelf_user` FOREIGN KEY (`userid`) REFERENCES `t_user` (`userid`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_bookshelf`
--

LOCK TABLES `t_bookshelf` WRITE;
/*!40000 ALTER TABLE `t_bookshelf` DISABLE KEYS */;
INSERT INTO `t_bookshelf` VALUES (6,4,18,'2026-07-07 23:32:37'),(7,3,13,'2026-07-09 00:11:40'),(8,4,19,'2026-07-09 00:14:05'),(9,5,19,'2026-11-01 22:54:27'),(10,6,19,'2026-11-01 23:03:11'),(11,6,18,'2026-11-01 23:03:15');
/*!40000 ALTER TABLE `t_bookshelf` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_category`
--

DROP TABLE IF EXISTS `t_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_category` (
  `categoryid` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `categoryname` varchar(50) NOT NULL COMMENT '分类名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `sortorder` int NOT NULL DEFAULT '1' COMMENT '排序号',
  `status` varchar(20) DEFAULT '启用' COMMENT '分类状态',
  `createtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`categoryid`) USING BTREE,
  UNIQUE KEY `uk_categoryname` (`categoryname`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='漫画分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_category`
--

LOCK TABLES `t_category` WRITE;
/*!40000 ALTER TABLE `t_category` DISABLE KEYS */;
INSERT INTO `t_category` VALUES (1,'热血','热血战斗类漫画，强调成长与拼搏',1,'启用','2025-05-16 10:00:00'),(2,'冒险','航海、探险等冒险题材漫画',2,'启用','2025-05-16 10:00:00'),(3,'恋爱','校园恋爱与情感题材漫画',3,'启用','2025-05-16 10:00:00'),(4,'奇幻','魔法与异世界幻想题材漫画',4,'启用','2025-05-16 10:00:00'),(5,'悬疑','推理与悬疑惊悚题材漫画',5,'启用','2025-05-16 10:00:00'),(6,'其他','暂未归类的漫画题材',6,'停用','2025-05-16 10:00:00');
/*!40000 ALTER TABLE `t_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_chapter`
--

DROP TABLE IF EXISTS `t_chapter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_chapter` (
  `chapterid` int NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `comicid` int NOT NULL COMMENT '漫画ID',
  `chapterno` int NOT NULL COMMENT '章节编号',
  `chaptertitle` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '章节标题',
  `releasedate` datetime DEFAULT NULL COMMENT '发布日期',
  `status` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT '草稿' COMMENT '发布状态',
  `content` text COMMENT '章节内容图片URL数组，JSON格式',
  PRIMARY KEY (`chapterid`) USING BTREE,
  KEY `fk_chapter_comicid` (`comicid`) USING BTREE,
  CONSTRAINT `fk_chapter_comic` FOREIGN KEY (`comicid`) REFERENCES `t_comic` (`comicid`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_chapter`
--

LOCK TABLES `t_chapter` WRITE;
/*!40000 ALTER TABLE `t_chapter` DISABLE KEYS */;
INSERT INTO `t_chapter` VALUES (3,2,1,'第1话 路飞出海','2025-05-16 10:00:00','发布','[\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\"]'),(14,2,2,'第2话 草帽海贼团','2026-07-07 15:26:10','发布','[\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\"]'),(15,2,3,'第3话 索隆','2026-07-07 15:26:10','发布','[\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\"]'),(16,3,1,'第1话 悟空出世','2026-07-07 15:26:10','发布','[\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\"]'),(17,3,2,'第2话 遇见布尔玛','2026-07-07 15:26:10','发布','[\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\"]'),(29,13,1,'第1话 启程','2026-07-07 16:04:53','发布','[\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\"]'),(30,14,1,'第1话 启程','2026-07-07 16:04:53','发布','[\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\"]'),(31,15,1,'第1话 启程','2026-07-07 16:04:53','发布','[\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\"]'),(32,16,1,'第1话 启程','2026-07-07 16:04:53','发布','[\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\"]'),(33,17,1,'第1话 启程','2026-07-07 16:04:53','发布','[\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\"]'),(34,18,1,'第1话 启程','2026-07-07 16:04:53','发布','[\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\"]'),(35,19,1,'第1话 启程','2026-07-07 16:04:53','发布','[\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\"]'),(36,23,3,'uh','2026-11-01 22:54:00','草稿','[\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\"]'),(37,24,1,'22','2026-11-01 23:04:06','发布','[\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\",\"/images/content-placeholder.svg\"]');
/*!40000 ALTER TABLE `t_chapter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_chaptercontent`
--

DROP TABLE IF EXISTS `t_chaptercontent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_chaptercontent` (
  `contentid` int NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `chapterid` int NOT NULL COMMENT '章节ID',
  `imgurl` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '漫画图片URL',
  `displayorder` int NOT NULL COMMENT '显示顺序',
  `status` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT '草稿' COMMENT '显示状态',
  `releasedate` datetime DEFAULT NULL COMMENT '发布日期',
  PRIMARY KEY (`contentid`) USING BTREE,
  KEY `fk_content_chapterid` (`chapterid`) USING BTREE,
  CONSTRAINT `fk_chapter_content` FOREIGN KEY (`chapterid`) REFERENCES `t_chapter` (`chapterid`) ON DELETE CASCADE,
  CONSTRAINT `fk_content_chapter` FOREIGN KEY (`chapterid`) REFERENCES `t_chapter` (`chapterid`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_chaptercontent`
--

LOCK TABLES `t_chaptercontent` WRITE;
/*!40000 ALTER TABLE `t_chaptercontent` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_chaptercontent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_comic`
--

DROP TABLE IF EXISTS `t_comic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_comic` (
  `comicid` int NOT NULL AUTO_INCREMENT COMMENT '漫画ID',
  `comictitle` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '漫画标题',
  `author` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '作者',
  `category` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '类别',
  `intro` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '漫画简介',
  `country` varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '国家',
  `coverimg` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '漫画封面图片',
  `keywords` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '内容关键字',
  `status` varchar(20) DEFAULT '已发布' COMMENT '漫画状态：已发布/已下架',
  `isviolation` tinyint DEFAULT '0' COMMENT '是否违规：0正常/1违规',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`comicid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_comic`
--

LOCK TABLES `t_comic` WRITE;
/*!40000 ALTER TABLE `t_comic` DISABLE KEYS */;
INSERT INTO `t_comic` VALUES (2,'海贼王','尾田荣一郎','冒险','航海冒险类经典漫画','日本','/images/onepiece.jpg','路飞;索隆;娜美','已发布',0,'2026-07-07 16:03:24'),(3,'龙珠','鸟山明','热血','经典热血漫画','日本','/images/dragonball.jpg','布尔玛;小悟空;龙珠','已发布',0,'2026-07-07 16:03:24'),(4,'测试漫画','测试作者','热血','这是一个测试漫画简介','日本','/images/cover-placeholder.svg','','已下架',1,'2026-07-07 16:03:24'),(5,'123','ae','热血','嗡嗡嗡嗡嗡嗡嗡嗡嗡','大陆','/images/cover-placeholder.svg','dwa','已发布',0,'2026-07-07 16:03:24'),(13,'幻彩少年','漫阅原创','奇幻','在霓虹与梦境交织的城市中，少年追逐着属于自己的色彩与命运。','大陆','/images/colorful_hero.jpg','奇幻;青春;色彩;冒险','已发布',0,'2026-07-07 16:04:53'),(14,'火影忍者','岸本齐史','热血','漩涡鸣人成为火影的忍者成长之路','日本','/images/naruto.jpg','忍者;鸣人;佐助;热血','已发布',0,'2026-07-07 16:04:53'),(15,'光明之刃','林一','奇幻','少年持剑觉醒，与龙族缔结契约拯救世界','大陆','/images/light_blade.jpg','奇幻;龙族;剑与魔法','已发布',0,'2026-07-07 16:04:53'),(16,'樱花下的约定','苏浅','恋爱','校园里的青涩恋爱与樱花树下的约定','大陆','/images/sakura_promise.jpg','校园;恋爱;青春','已发布',0,'2026-07-07 16:04:53'),(17,'暗月','墨染','悬疑','红月之夜接连发生的离奇失踪事件','大陆','/images/dark_moon.jpg','悬疑;推理;黑暗','已发布',0,'2026-07-07 16:04:53'),(18,'剑仙传说','青云','武侠仙侠','凡人少年踏入修仙之路，一剑开天门','大陆','/images/sword_immortal.jpg','仙侠;修仙;武侠','已发布',0,'2026-07-07 16:04:53'),(19,'霓虹都市','夜航','都市','在繁华都市中追寻真相的年轻侦探','韩国','/images/neon_city.jpg','都市;侦探;悬疑','已发布',0,'2026-07-07 16:04:53'),(20,'依旧测试','黑炎','历史','黑手2','大陆','/images/cover-placeholder.svg','飞','已下架',0,'2026-07-07 17:12:41'),(22,'007','黑炎','其他','006','其他','/images/cover-placeholder.svg','007','已下架',1,'2026-07-09 00:14:31'),(23,'ww','EMT','奇幻','的','日本','/images/cover-placeholder.svg','我ddd','已发布',0,'2026-11-01 22:53:18'),(24,'wihid','wwww','冒险','dbiabdiuabidbi','大陆','/images/cover-placeholder.svg','www','已发布',0,'2026-11-01 23:03:45');
/*!40000 ALTER TABLE `t_comic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_comment`
--

DROP TABLE IF EXISTS `t_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_comment` (
  `commentid` int NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `comicid` int NOT NULL COMMENT '漫画ID',
  `userid` int NOT NULL COMMENT '评论用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '评论用户名',
  `content` varchar(500) NOT NULL COMMENT '评论内容',
  `rating` int DEFAULT '5' COMMENT '评分1-5分',
  `createtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  `status` varchar(20) DEFAULT '正常' COMMENT '评论状态',
  PRIMARY KEY (`commentid`) USING BTREE,
  KEY `fk_comment_comicid` (`comicid`) USING BTREE,
  KEY `fk_comment_userid` (`userid`) USING BTREE,
  CONSTRAINT `fk_comment_comic` FOREIGN KEY (`comicid`) REFERENCES `t_comic` (`comicid`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`userid`) REFERENCES `t_user` (`userid`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='漫画评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_comment`
--

LOCK TABLES `t_comment` WRITE;
/*!40000 ALTER TABLE `t_comment` DISABLE KEYS */;
INSERT INTO `t_comment` VALUES (7,2,2,'李四','海贼王的冒险剧情很热血，路飞的成长线很打动人',5,'2026-11-02 10:17:12','正常'),(8,13,2,'李四','幻彩少年的画风很独特，剧情节奏也不错',5,'2026-11-02 10:17:13','正常'),(9,2,3,'admin','画面有点糊，希望能优化一下封面清晰度',3,'2026-11-02 10:17:13','正常'),(10,2,3,'admin','这条是广告内容，用于演示管理员屏蔽功能',1,'2026-11-02 10:17:13','已屏蔽'),(11,13,3,'admin','期待后续章节更新，节奏再快一点就更好了',4,'2026-11-02 10:17:13','正常'),(12,19,3,'admin','88',5,'2026-11-02 10:56:56','正常');
/*!40000 ALTER TABLE `t_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_notice`
--

DROP TABLE IF EXISTS `t_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_notice` (
  `noticeid` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(150) NOT NULL COMMENT '公告标题',
  `content` varchar(1000) DEFAULT NULL COMMENT '公告正文',
  `noticetype` varchar(20) DEFAULT '公告' COMMENT '公告类型',
  `publisher` varchar(50) DEFAULT NULL COMMENT '发布人',
  `publishdate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `status` varchar(20) DEFAULT '已发布' COMMENT '公告状态',
  PRIMARY KEY (`noticeid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='平台公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_notice`
--

LOCK TABLES `t_notice` WRITE;
/*!40000 ALTER TABLE `t_notice` DISABLE KEYS */;
INSERT INTO `t_notice` VALUES (1,'未选择的梦 | 3.4版本内容说明','3.4版本已完成开发，新增分类筛选、评论互动与公告栏功能，欢迎体验','公告','管理员','2025-07-04 10:00:00','已发布'),(2,'暑期漫画狂欢节开启','暑期期间平台将上线专题推荐位，优质原创作品可获得首页曝光机会','活动','管理员','2025-07-03 09:30:00','已发布'),(3,'新番连载《星际回响》正式上线','全新科幻题材连载作品上线，每周五更新一章','更新','管理员','2025-07-02 15:20:00','已发布'),(4,'作者招募计划：圆你的漫画梦','平台长期招募原创漫画作者，入驻即享作品发布与数据反馈服务','招募','管理员','2025-07-01 11:00:00','已发布'),(5,'平台维护公告（已下线）','平台已于7月6日凌晨完成数据库升级维护','公告','管理员','2025-07-06 02:00:00','已下线');
/*!40000 ALTER TABLE `t_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user` (
  `userid` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
  `usertype` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户类型',
  `phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '电话',
  `email` varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '邮箱',
  `createdate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  PRIMARY KEY (`userid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user`
--

LOCK TABLES `t_user` WRITE;
/*!40000 ALTER TABLE `t_user` DISABLE KEYS */;
INSERT INTO `t_user` VALUES (2,'李四','123456','作者','13900139000','lisi@qq.com','2025-05-16 10:00:00'),(3,'admin','123456','admin','13800138000','admin@example.com','2026-07-06 21:48:47'),(4,'黑炎','794613258','作者','128478938295','','2026-07-07 17:03:07'),(5,'EMT','123456','作者','12846782319y832','','2026-07-09 00:16:18'),(6,'wwww','123456','作者','12423634212','','2026-11-01 23:02:34');
/*!40000 ALTER TABLE `t_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_bookshelf`
--

DROP TABLE IF EXISTS `v_bookshelf`;
/*!50001 DROP VIEW IF EXISTS `v_bookshelf`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_bookshelf` AS SELECT 
 1 AS `bookshelfid`,
 1 AS `userid`,
 1 AS `comicid`,
 1 AS `collecttime`,
 1 AS `username`,
 1 AS `comictitle`,
 1 AS `coverimg`,
 1 AS `intro`,
 1 AS `category`,
 1 AS `author`,
 1 AS `lastUpdateTime`,
 1 AS `chapterCount`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_comic`
--

DROP TABLE IF EXISTS `v_comic`;
/*!50001 DROP VIEW IF EXISTS `v_comic`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_comic` AS SELECT 
 1 AS `comicid`,
 1 AS `comictitle`,
 1 AS `author`,
 1 AS `category`,
 1 AS `intro`,
 1 AS `country`,
 1 AS `coverimg`,
 1 AS `keywords`,
 1 AS `chapterid`,
 1 AS `chapterno`,
 1 AS `chaptertitle`,
 1 AS `releasedate`,
 1 AS `status`,
 1 AS `contentid`,
 1 AS `imgurl`,
 1 AS `displayorder`,
 1 AS `contentStatus`,
 1 AS `authorId`,
 1 AS `authorName`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `v_bookshelf`
--

/*!50001 DROP VIEW IF EXISTS `v_bookshelf`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_bookshelf` AS select `b`.`bookshelfid` AS `bookshelfid`,`b`.`userid` AS `userid`,`b`.`comicid` AS `comicid`,`b`.`collecttime` AS `collecttime`,`u`.`username` AS `username`,`c`.`comictitle` AS `comictitle`,`c`.`coverimg` AS `coverimg`,`c`.`intro` AS `intro`,`c`.`category` AS `category`,`c`.`author` AS `author`,max(`ch`.`releasedate`) AS `lastUpdateTime`,count(`ch`.`chapterid`) AS `chapterCount` from (((`t_bookshelf` `b` left join `t_user` `u` on((`b`.`userid` = `u`.`userid`))) left join `t_comic` `c` on((`b`.`comicid` = `c`.`comicid`))) left join `t_chapter` `ch` on(((`c`.`comicid` = `ch`.`comicid`) and (`ch`.`status` = '发布')))) group by `b`.`bookshelfid` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_comic`
--

/*!50001 DROP VIEW IF EXISTS `v_comic`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_comic` AS select `c`.`comicid` AS `comicid`,`c`.`comictitle` AS `comictitle`,`c`.`author` AS `author`,`c`.`category` AS `category`,`c`.`intro` AS `intro`,`c`.`country` AS `country`,`c`.`coverimg` AS `coverimg`,`c`.`keywords` AS `keywords`,`ch`.`chapterid` AS `chapterid`,`ch`.`chapterno` AS `chapterno`,`ch`.`chaptertitle` AS `chaptertitle`,`ch`.`releasedate` AS `releasedate`,`ch`.`status` AS `status`,`cc`.`contentid` AS `contentid`,`cc`.`imgurl` AS `imgurl`,`cc`.`displayorder` AS `displayorder`,`cc`.`status` AS `contentStatus`,`u`.`userid` AS `authorId`,`u`.`username` AS `authorName` from (((`t_chaptercontent` `cc` left join `t_chapter` `ch` on((`cc`.`chapterid` = `ch`.`chapterid`))) left join `t_comic` `c` on((`ch`.`comicid` = `c`.`comicid`))) left join `t_user` `u` on((`c`.`author` = `u`.`username`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-18 11:29:45
