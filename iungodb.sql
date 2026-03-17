/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80012 (8.0.12)
 Source Host           : localhost:3306
 Source Schema         : iungodb

 Target Server Type    : MySQL
 Target Server Version : 80012 (8.0.12)
 File Encoding         : 65001

 Date: 17/03/2026 05:18:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for blog
-- ----------------------------
DROP TABLE IF EXISTS `blog`;
CREATE TABLE `blog`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` tinyint(4) NULL DEFAULT 0,
  `top` tinyint(4) NULL DEFAULT 0,
  `open_scope` tinyint(4) NULL DEFAULT 2,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `read` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog
-- ----------------------------
INSERT INTO `blog` VALUES (1, 3, '博客管理模块开发笔记', '<p>博客模块核心功能：<br>1. 富文本编辑器集成<br>2. 草稿保存与版本恢复<br>3. 开放权限控制</p>', '1,2,3', 1, 1, 2, '2026-02-28 04:30:16', '2026-03-17 04:14:37', NULL);
INSERT INTO `blog` VALUES (2, 4, '邀请树功能实现思路（草稿）', '<p>1. 递归查询邀请关系<br>2. 前端树形组件渲染<br>3. 性能优化：懒加载</p>', '1', 0, 0, 2, '2026-02-28 04:30:16', '2026-03-17 03:53:20', NULL);

-- ----------------------------
-- Table structure for blog_type
-- ----------------------------
DROP TABLE IF EXISTS `blog_type`;
CREATE TABLE `blog_type`  (
  `id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `show` tinyint(4) NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of blog_type
-- ----------------------------
INSERT INTO `blog_type` VALUES (1, '博客开发', 1, '就是博客开发');
INSERT INTO `blog_type` VALUES (2, '22', 0, '22');
INSERT INTO `blog_type` VALUES (3, '33', 1, '33');

-- ----------------------------
-- Table structure for collection
-- ----------------------------
DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `target_id` int(4) NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collection
-- ----------------------------

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `target_id` int(10) NULL DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `target_type` int(4) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `target_id` int(10) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `target_type` int(4) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of favorite
-- ----------------------------

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `befllow_id` bigint(20) NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of follow
-- ----------------------------
INSERT INTO `follow` VALUES (1, 5, 3, '2026-02-28 04:30:16');
INSERT INTO `follow` VALUES (2, 4, 3, '2026-02-28 04:30:16');

-- ----------------------------
-- Table structure for invite_code
-- ----------------------------
DROP TABLE IF EXISTS `invite_code`;
CREATE TABLE `invite_code`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `userid` bigint(20) NULL DEFAULT NULL,
  `expire_time` datetime NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of invite_code
-- ----------------------------
INSERT INTO `invite_code` VALUES (1, 'ADMIN123', 1, NULL, '2026-03-17 02:01:13');
INSERT INTO `invite_code` VALUES (2, 'ALICE888', 2, NULL, '2026-03-17 02:01:13');
INSERT INTO `invite_code` VALUES (3, 'BOB9999', 3, NULL, '2026-03-17 02:01:13');
INSERT INTO `invite_code` VALUES (4, 'CHARLIE7', 4, NULL, '2026-03-17 02:01:13');
INSERT INTO `invite_code` VALUES (5, '01AGA1K4', 9, NULL, '2026-03-17 02:06:14');

-- ----------------------------
-- Table structure for invite_relation
-- ----------------------------
DROP TABLE IF EXISTS `invite_relation`;
CREATE TABLE `invite_relation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `inviter_id` bigint(20) NOT NULL,
  `invitee_id` bigint(20) NOT NULL,
  `rewarded` tinyint(4) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of invite_relation
-- ----------------------------
INSERT INTO `invite_relation` VALUES (1, 1, 2, 1, '2026-03-17 02:01:25');
INSERT INTO `invite_relation` VALUES (2, 1, 3, 1, '2026-03-17 02:01:25');
INSERT INTO `invite_relation` VALUES (3, 2, 4, 1, '2026-03-17 02:01:25');
INSERT INTO `invite_relation` VALUES (4, 1, 9, 1, '2026-03-17 02:06:14');

-- ----------------------------
-- Table structure for like
-- ----------------------------
DROP TABLE IF EXISTS `like`;
CREATE TABLE `like`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `target_type` int(4) NULL DEFAULT NULL,
  `target_id` int(10) NULL DEFAULT NULL,
  `action` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of like
-- ----------------------------

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `author_id` bigint(20) NOT NULL,
  `category_id` bigint(20) NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `likes` int(11) NULL DEFAULT 0,
  `favorites` int(11) NULL DEFAULT 0,
  `comments` int(11) NULL DEFAULT 0,
  `status` tinyint(4) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES (1, 1, 1, '欢迎帖', '这是第一篇帖子', 'welcome', 0, 0, 0, 0, '2026-02-28 04:19:00', '2026-02-28 04:19:00');
INSERT INTO `post` VALUES (2, 3, 1, 'Vue3+SpringBoot全栈开发实战', '<p>这是一篇测试帖子，包含<strong>富文本内容</strong>，演示Vue3和SpringBoot的整合使用。</p><p>核心要点：</p><ul><li>前端使用Vue3组合式API</li><li>后端使用SpringBoot+MyBatis</li><li>数据库使用MySQL</li></ul>', 'Vue3,SpringBoot,MySQL', 10, 5, 3, 0, '2026-02-28 04:30:16', '2026-02-28 04:30:16');
INSERT INTO `post` VALUES (3, 3, 1, '2026年学习计划', '<p>1. 深入学习SpringCloud微服务<br>2. 掌握Vue3生态（Pinia、VueRouter）<br>3. 学习MySQL优化技巧</p>', '学习计划,2026', 5, 2, 1, 0, '2026-02-28 04:30:16', '2026-02-28 04:30:16');
INSERT INTO `post` VALUES (4, 4, 1, 'MySQL索引优化实战', '<p>1. 避免使用SELECT *<br>2. 联合索引遵循最左前缀原则<br>3. 避免在索引列上做函数操作</p>', 'MySQL,索引优化,性能', 8, 4, 2, 1, '2026-02-28 04:30:16', '2026-02-28 04:30:16');
INSERT INTO `post` VALUES (5, 2, NULL, 'a\'d\'s', ' 大苏打', '是的', 0, 0, 0, 0, '2026-03-14 21:58:22', '2026-03-14 21:58:22');
INSERT INTO `post` VALUES (6, 2, NULL, 'asd', '<ol><li>ssssd</li></ol>', 'ads', 0, 0, 0, 0, '2026-03-14 22:58:44', '2026-03-14 22:58:44');

-- ----------------------------
-- Table structure for post_category
-- ----------------------------
DROP TABLE IF EXISTS `post_category`;
CREATE TABLE `post_category`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_category
-- ----------------------------
INSERT INTO `post_category` VALUES (1, 'General', '通用板块');
INSERT INTO `post_category` VALUES (2, '技术分享', '编程技术相关内容');
INSERT INTO `post_category` VALUES (3, '日常记录', '生活日常分享');
INSERT INTO `post_category` VALUES (4, '经验总结', '学习/工作经验总结');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, 'USER', '普通用户');
INSERT INTO `role` VALUES (2, 'ADMIN', '管理员');

-- ----------------------------
-- Table structure for target
-- ----------------------------
DROP TABLE IF EXISTS `target`;
CREATE TABLE `target`  (
  `id` int(11) NOT NULL,
  `targetName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `targetDescription` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of target
-- ----------------------------
INSERT INTO `target` VALUES (1, 'blog', '博客');
INSERT INTO `target` VALUES (2, 'post', '帖子');
INSERT INTO `target` VALUES (3, 'comment', '评论');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `gender` tinyint(4) NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `role_id` bigint(20) NULL DEFAULT 1,
  `status` tinyint(4) NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 1, 'e10adc3949ba59abbe56e057f20f883e', 'admin@iungo.com', '管理员', NULL, '系统管理员', 2, 1, '2026-03-17 02:01:07', '2026-03-17 02:01:07');
INSERT INTO `user` VALUES (2, 'alice', 2, 'e10adc3949ba59abbe56e057f20f883e', 'alice@example.com', '爱丽丝', NULL, '前端开发', 1, 1, '2026-03-17 02:01:07', '2026-03-17 02:01:07');
INSERT INTO `user` VALUES (3, 'bob', 1, 'e10adc3949ba59abbe56e057f20f883e', 'bob@example.com', '鲍勃', NULL, '后端开发', 1, 1, '2026-03-17 02:01:07', '2026-03-17 02:01:07');
INSERT INTO `user` VALUES (4, 'charlie', 0, 'e10adc3949ba59abbe56e057f20f883e', 'charlie@example.com', '查理', NULL, '被邀请新用户', 1, 1, '2026-03-17 02:01:07', '2026-03-17 02:01:07');
INSERT INTO `user` VALUES (8, 'test', NULL, 'e10adc3949ba59abbe56e057f20f883e', 'test@example.com', 'Test User', NULL, NULL, 1, 1, '2026-03-17 02:04:16', '2026-03-17 02:04:16');
INSERT INTO `user` VALUES (9, 'hirasawa', 0, 'dca8650e324d99d1d0fe766927bfded9', '2464280450@qq.com', 'Jiva', NULL, NULL, 1, 1, '2026-03-17 02:06:14', '2026-03-17 02:06:14');

SET FOREIGN_KEY_CHECKS = 1;
