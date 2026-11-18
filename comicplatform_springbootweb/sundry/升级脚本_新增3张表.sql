-- ============================================================
-- 漫画平台数据库升级脚本：新增 3 张业务表
-- 在 comicplatformdb 库中执行，使平台业务表总数达到 8 张：
--   t_user  t_comic  t_chapter  t_chaptercontent  t_bookshelf
--   t_category（分类） t_notice（公告） t_comment（评论）
-- ============================================================

USE `comicplatformdb`;

-- ------------------------------------------------------------
-- 1. 分类表 t_category
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category` (
  `categoryid` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `categoryname` varchar(50) NOT NULL COMMENT '分类名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `sortorder` int NOT NULL DEFAULT '1' COMMENT '排序号',
  `status` varchar(20) DEFAULT '启用' COMMENT '分类状态',
  `createtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`categoryid`) USING BTREE,
  UNIQUE KEY `uk_categoryname` (`categoryname`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='漫画分类表';

INSERT INTO `t_category` VALUES
(1,'热血','热血战斗类漫画，强调成长与拼搏',1,'启用','2025-05-16 10:00:00'),
(2,'冒险','航海、探险等冒险题材漫画',2,'启用','2025-05-16 10:00:00'),
(3,'恋爱','校园恋爱与情感题材漫画',3,'启用','2025-05-16 10:00:00'),
(4,'奇幻','魔法与异世界幻想题材漫画',4,'启用','2025-05-16 10:00:00'),
(5,'悬疑','推理与悬疑惊悚题材漫画',5,'启用','2025-05-16 10:00:00'),
(6,'其他','暂未归类的漫画题材',6,'停用','2025-05-16 10:00:00');

-- ------------------------------------------------------------
-- 2. 公告表 t_notice
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_notice`;
CREATE TABLE `t_notice` (
  `noticeid` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(150) NOT NULL COMMENT '公告标题',
  `content` varchar(1000) DEFAULT NULL COMMENT '公告正文',
  `noticetype` varchar(20) DEFAULT '公告' COMMENT '公告类型',
  `publisher` varchar(50) DEFAULT NULL COMMENT '发布人',
  `publishdate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `status` varchar(20) DEFAULT '已发布' COMMENT '公告状态',
  PRIMARY KEY (`noticeid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='平台公告表';

INSERT INTO `t_notice` VALUES
(1,'未选择的梦 | 3.4版本内容说明','3.4版本已完成开发，新增分类筛选、评论互动与公告栏功能，欢迎体验','公告','管理员','2025-07-04 10:00:00','已发布'),
(2,'暑期漫画狂欢节开启','暑期期间平台将上线专题推荐位，优质原创作品可获得首页曝光机会','活动','管理员','2025-07-03 09:30:00','已发布'),
(3,'新番连载《星际回响》正式上线','全新科幻题材连载作品上线，每周五更新一章','更新','管理员','2025-07-02 15:20:00','已发布'),
(4,'作者招募计划：圆你的漫画梦','平台长期招募原创漫画作者，入驻即享作品发布与数据反馈服务','招募','管理员','2025-07-01 11:00:00','已发布'),
(5,'平台维护公告（已下线）','平台已于7月6日凌晨完成数据库升级维护','公告','管理员','2025-07-06 02:00:00','已下线');

-- ------------------------------------------------------------
-- 3. 评论表 t_comment
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `t_comment`;
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='漫画评论表';

INSERT INTO `t_comment` VALUES
(1,2,2,'李四','海贼王的冒险剧情很热血，路飞的成长线很打动人',5,'2025-06-01 09:20:00','正常'),
(2,13,2,'李四','幻彩少年的画风很独特，剧情节奏也不错',5,'2025-06-02 14:35:00','正常'),
(3,2,3,'admin','画面有点糊，希望能优化一下封面清晰度',3,'2025-06-03 20:10:00','正常'),
(4,2,3,'admin','这条是广告内容，用于演示管理员屏蔽功能',1,'2025-06-04 11:45:00','已屏蔽'),
(5,13,3,'admin','期待后续章节更新，节奏再快一点就更好了',4,'2025-06-05 16:30:00','正常');

-- 查看结果：应列出 8 张业务表
SHOW TABLES;
