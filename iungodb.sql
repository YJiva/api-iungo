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

 Date: 29/03/2026 17:24:34
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
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog
-- ----------------------------
INSERT INTO `blog` VALUES (1, 3, '博客管理模块开发笔记', '<p>博客模块核心功能：<br>1. 富文本编辑器集成<br>2. 草稿保存与版本恢复<br>3. 开放权限控制</p>', '1,3', 1, 1, 2, '2026-02-28 04:30:16', '2026-03-17 05:28:31', 7);
INSERT INTO `blog` VALUES (3, 1, 'Spring Boot 与 Vue3 整合实践', '<p>本篇介绍如何使用 <strong>Spring Boot</strong> + <strong>Vue3</strong> 搭建前后端分离项目。</p>\r\n  <ul>\r\n    <li>后端提供 RESTful API</li>\r\n    <li>前端使用 Composition API</li>\r\n    <li>统一异常与登录校验</li>\r\n  </ul>\r\n  <p><img src=\"https://via.placeholder.com/640x260.png?text=SpringBoot+Vue3\" /></p>', '1,3', 1, 0, 2, '2026-03-17 05:57:52', '2026-03-17 20:11:02', 1);
INSERT INTO `blog` VALUES (4, 2, '富文本编辑器上传图片配置', '<p>使用 <code>@vueup/vue-quill</code> 集成富文本编辑器，并支持图片上传。</p>\r\n  <ol>\r\n    <li>前端拦截 toolbar 的 image 事件</li>\r\n    <li>选择本地图片并通过 <code>FormData</code> 上传</li>\r\n    <li>后端返回图片 URL，并插入到编辑器中</li>\r\n  </ol>', '1', 1, 0, 2, '2026-03-17 05:57:52', '2026-03-17 20:25:24', 4);
INSERT INTO `blog` VALUES (8, 3, '前端无限滚动与骨架屏实现', '<p>通过 <code>v-infinite-scroll</code> 实现列表的懒加载，并用 <code>el-skeleton</code> 作为骨架屏。</p>\r\n  <p>这样可以保证布局稳定，不会因为异步数据抖动。</p>', '1,3', 1, 0, 2, '2026-03-17 05:57:52', '2026-03-17 05:57:52', 25);
INSERT INTO `blog` VALUES (12, 1, '前台首页精选博客区重构记录', '<p>记录这次首页精选区的重构过程：</p>\r\n  <ul>\r\n    <li>从 <code>post</code> 切换到 <code>blog</code> 数据源</li>\r\n    <li>增加标签小卡片展示</li>\r\n    <li>保留邀请统计与圈层信息</li>\r\n  </ul>', '1,3', 1, 1, 2, '2026-03-17 05:57:52', '2026-03-17 05:57:52', 1);
INSERT INTO `blog` VALUES (13, 1, '你好', '<p><img src=\"/upload/editor/20260317/1773729166743.jpeg\" alt=\"tos-cn-i-0813c001_o0MtGeNzBIzEQzIftANALED2Ie7iAAj2GBNAZi~c5_300x300.jpeg\" data-href=\"/upload/editor/20260317/1773729166743.jpeg\" style=\"\"/>的</p>', '1,3', 1, 0, 2, '2026-03-17 14:32:53', '2026-03-17 19:58:24', 10);
INSERT INTO `blog` VALUES (14, 1, 'asd', '<p style=\"text-align: center;\"><br></p><p style=\"text-align: center;\"><br></p><p style=\"text-align: center;\">sd </p><p>大飒飒的</p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p>', '1', 1, 0, 2, '2026-03-17 14:51:46', '2026-03-29 00:38:35', NULL);
INSERT INTO `blog` VALUES (16, 1, '看看', '<p>dasd asd测试一下这个</p>', '', 1, 0, 2, '2026-03-17 20:30:02', '2026-03-17 20:30:02', 11);
INSERT INTO `blog` VALUES (17, 1, '是的a', '<p>打赏</p>', '', 1, 0, 2, '2026-03-17 20:35:21', '2026-03-17 20:35:21', 45);
INSERT INTO `blog` VALUES (18, 1, 'test', '<div data-w-e-type=\"video\" data-w-e-is-void>\n<video poster=\"\" controls=\"true\" width=\"auto\" height=\"auto\"><source src=\"/upload/editor-video/20260327/1774625928227.mp4\" type=\"video/mp4\"/></video>\n</div><p style=\"text-align: center;\">测试一下<img src=\"/upload/editor/20260327/1774625584169.jpeg\" alt=\"tos-cn-i-0813c001_o0MtGeNzBIzEQzIftANALED2Ie7iAAj2GBNAZi~c5_300x300.jpeg\" data-href=\"/upload/editor/20260327/1774625584169.jpeg\" style=\"width: 217.00px;height: 217.00px;\"></p>', NULL, 1, 0, 2, '2026-03-27 23:39:09', '2026-03-28 14:33:09', 55);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of blog_type
-- ----------------------------
INSERT INTO `blog_type` VALUES (1, '博客开发', 1, '就是博客开发');
INSERT INTO `blog_type` VALUES (2, '22', 1, '22');
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
  `like_count` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 87 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (7, 14, 1, 'nihao', '2026-03-28 04:23:04', 1, 0);
INSERT INTO `comment` VALUES (8, 14, 1, '我去', '2026-03-28 04:23:28', 1, 1);
INSERT INTO `comment` VALUES (9, 8, 1, 'woc', '2026-03-28 04:24:00', 3, 1);
INSERT INTO `comment` VALUES (10, 8, 1, '123', '2026-03-28 04:31:01', 3, 1);
INSERT INTO `comment` VALUES (11, 10, 1, '123', '2026-03-28 04:31:33', 3, 1);
INSERT INTO `comment` VALUES (12, 11, 1, '真的假的', '2026-03-28 14:44:24', 3, 1);
INSERT INTO `comment` VALUES (26, 14, 9, '可以可以', '2026-03-28 15:10:46', 1, 1);
INSERT INTO `comment` VALUES (27, 14, 9, '真的', '2026-03-28 15:11:08', 1, 1);
INSERT INTO `comment` VALUES (35, 18, 9, '是的', '2026-03-28 17:22:43', 1, 1);
INSERT INTO `comment` VALUES (36, 18, 9, 'da', '2026-03-28 17:22:49', 1, 0);
INSERT INTO `comment` VALUES (37, 14, 9, 'hi', '2026-03-28 17:28:40', 1, 0);
INSERT INTO `comment` VALUES (38, 16, 9, '擦拭', '2026-03-28 17:58:17', 1, 0);
INSERT INTO `comment` VALUES (39, 35, 9, '可以可以', '2026-03-28 17:58:38', 3, 0);
INSERT INTO `comment` VALUES (40, 17, 9, '是', '2026-03-28 22:11:33', 1, 0);
INSERT INTO `comment` VALUES (41, 18, 9, '十大', '2026-03-28 22:35:01', 1, 0);
INSERT INTO `comment` VALUES (42, 35, 9, '阿松大', '2026-03-28 22:35:06', 3, 0);
INSERT INTO `comment` VALUES (43, 39, 9, '大苏打', '2026-03-28 22:35:11', 3, 0);
INSERT INTO `comment` VALUES (44, 43, 9, '打撒大', '2026-03-28 22:35:46', 3, 0);
INSERT INTO `comment` VALUES (45, 35, 1, 'asd', '2026-03-28 22:36:28', 3, 0);
INSERT INTO `comment` VALUES (46, 8, 1, 'das', '2026-03-28 22:36:54', 1, 0);
INSERT INTO `comment` VALUES (47, 8, 2, 'sad', '2026-03-28 22:37:13', 1, 0);
INSERT INTO `comment` VALUES (48, 46, 2, 'ads', '2026-03-28 22:37:19', 3, 0);
INSERT INTO `comment` VALUES (49, 48, 2, 'dasdsa', '2026-03-28 22:37:25', 3, 0);
INSERT INTO `comment` VALUES (50, 8, 1, 'dasdsa', '2026-03-28 22:37:44', 1, 0);
INSERT INTO `comment` VALUES (51, 46, 1, 'ads', '2026-03-28 22:37:56', 3, 0);
INSERT INTO `comment` VALUES (52, 46, 1, 'jghhgjgh', '2026-03-28 22:38:01', 3, 0);
INSERT INTO `comment` VALUES (53, 8, 1, 'dsadsa', '2026-03-28 22:38:49', 1, 0);
INSERT INTO `comment` VALUES (54, 8, 1, 'asdsad', '2026-03-28 22:38:51', 1, 0);
INSERT INTO `comment` VALUES (55, 48, 1, '大撒大撒', '2026-03-28 22:39:18', 3, 0);
INSERT INTO `comment` VALUES (56, 9, 1, 'woqu', '2026-03-29 00:31:11', 3, 0);
INSERT INTO `comment` VALUES (58, 3, 1, '<p>大苏打啊</p>', '2026-03-29 01:40:48', 2, 0);
INSERT INTO `comment` VALUES (59, 1, 1, '<p>阿达</p>', '2026-03-29 01:41:03', 2, 0);
INSERT INTO `comment` VALUES (60, 1, 1, '<p>大撒大撒</p>', '2026-03-29 01:41:08', 2, 0);
INSERT INTO `comment` VALUES (61, 8, 1, '<p>我勒个</p><p><br></p>', '2026-03-29 02:04:43', 2, 0);
INSERT INTO `comment` VALUES (62, 61, 1, '<p>可以可以</p>', '2026-03-29 02:04:53', 3, 0);
INSERT INTO `comment` VALUES (63, 62, 1, '<p>额</p>', '2026-03-29 02:05:04', 3, 0);
INSERT INTO `comment` VALUES (64, 8, 1, '<p><br></p>', '2026-03-29 02:05:09', 2, 0);
INSERT INTO `comment` VALUES (65, 64, 1, '<p>达瓦</p>', '2026-03-29 02:05:14', 3, 0);
INSERT INTO `comment` VALUES (66, 63, 9, '<p>我去</p>', '2026-03-29 04:53:49', 3, 0);
INSERT INTO `comment` VALUES (67, 63, 9, '<p>就</p>', '2026-03-29 04:54:22', 3, 0);
INSERT INTO `comment` VALUES (68, 63, 9, '<p>阿松大</p>', '2026-03-29 04:55:43', 3, 0);
INSERT INTO `comment` VALUES (69, 67, 9, '<p>可以可以</p>', '2026-03-29 04:55:51', 3, 0);
INSERT INTO `comment` VALUES (70, 9, 9, '<p>确实还可以</p>', '2026-03-29 04:57:54', 2, 0);
INSERT INTO `comment` VALUES (71, 9, 9, '<p>看着不错</p>', '2026-03-29 04:58:00', 2, 0);
INSERT INTO `comment` VALUES (72, 71, 9, '<p>真的假的</p>', '2026-03-29 04:58:18', 3, 0);
INSERT INTO `comment` VALUES (73, 70, 9, '<p>当然是真的啊</p>', '2026-03-29 04:58:25', 3, 0);
INSERT INTO `comment` VALUES (74, 9, 9, '<p><img src=\"/upload/editor/20260329/1774731684622.jpg\" style=\"max-width:100%;\" /></p>', '2026-03-29 05:01:34', 2, 0);
INSERT INTO `comment` VALUES (75, 74, 9, '<p>这是啥</p>', '2026-03-29 05:01:41', 3, 0);
INSERT INTO `comment` VALUES (76, 75, 9, '<p>二逼</p>', '2026-03-29 05:01:54', 3, 0);
INSERT INTO `comment` VALUES (79, 58, 1, '<p>dsa</p>', '2026-03-29 14:27:15', 3, 0);
INSERT INTO `comment` VALUES (86, 58, 1, '<p>大苏打撒旦</p>', '2026-03-29 15:19:53', 3, 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` VALUES (11, 18, 2, '2026-03-28 04:21:38', 1);
INSERT INTO `favorite` VALUES (12, 14, 2, '2026-03-28 04:22:19', 1);
INSERT INTO `favorite` VALUES (13, 14, 1, '2026-03-28 04:23:01', 1);
INSERT INTO `favorite` VALUES (14, 18, 1, '2026-03-28 14:33:38', 1);
INSERT INTO `favorite` VALUES (15, 14, 9, '2026-03-28 15:06:09', 1);
INSERT INTO `favorite` VALUES (16, 13, 9, '2026-03-28 17:09:24', 1);

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `target_id` bigint(20) NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_follow_user_target`(`user_id` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_follow_target_id`(`target_id` ASC) USING BTREE,
  INDEX `idx_follow_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of follow
-- ----------------------------
INSERT INTO `follow` VALUES (1, 5, 3, '2026-02-28 04:30:16');
INSERT INTO `follow` VALUES (2, 4, 3, '2026-02-28 04:30:16');
INSERT INTO `follow` VALUES (3, 1, 2, '2026-03-27 17:22:08');
INSERT INTO `follow` VALUES (4, 1, 9, '2026-03-27 17:25:01');
INSERT INTO `follow` VALUES (8, 9, 1, '2026-03-28 17:59:07');
INSERT INTO `follow` VALUES (9, 1, 4, '2026-03-29 05:13:21');

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
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of invite_code
-- ----------------------------
INSERT INTO `invite_code` VALUES (1, 'ADMIN123', 1, NULL, '2026-03-17 02:01:13');
INSERT INTO `invite_code` VALUES (2, 'ALICE888', 2, NULL, '2026-03-17 02:01:13');
INSERT INTO `invite_code` VALUES (3, 'BOB9999', 3, NULL, '2026-03-17 02:01:13');
INSERT INTO `invite_code` VALUES (4, 'CHARLIE7', 4, NULL, '2026-03-17 02:01:13');
INSERT INTO `invite_code` VALUES (5, '01AGA1K4', 9, NULL, '2026-03-17 02:06:14');
INSERT INTO `invite_code` VALUES (6, 'LH2K47LU', 10, NULL, '2026-03-27 16:30:00');

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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of invite_relation
-- ----------------------------
INSERT INTO `invite_relation` VALUES (1, 1, 2, 1, '2026-03-17 02:01:25');
INSERT INTO `invite_relation` VALUES (2, 1, 3, 1, '2026-03-17 02:01:25');
INSERT INTO `invite_relation` VALUES (3, 2, 4, 1, '2026-03-17 02:01:25');
INSERT INTO `invite_relation` VALUES (4, 2, 9, 1, '2026-03-17 02:06:14');
INSERT INTO `invite_relation` VALUES (5, 9, 10, 1, '2026-03-27 16:30:00');

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
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of like
-- ----------------------------
INSERT INTO `like` VALUES (4, 1, 1, 18, 'LIKE', '2026-03-28 04:20:56');
INSERT INTO `like` VALUES (6, 2, 1, 18, 'LIKE', '2026-03-28 04:21:58');
INSERT INTO `like` VALUES (7, 1, 1, 14, 'LIKE', '2026-03-28 04:39:42');
INSERT INTO `like` VALUES (9, 9, 3, 27, 'LIKE', '2026-03-28 15:34:09');
INSERT INTO `like` VALUES (13, 9, 3, 10, 'LIKE', '2026-03-28 15:34:35');
INSERT INTO `like` VALUES (14, 9, 3, 11, 'LIKE', '2026-03-28 15:34:36');
INSERT INTO `like` VALUES (16, 9, 1, 14, 'LIKE', '2026-03-28 15:41:20');
INSERT INTO `like` VALUES (22, 9, 3, 26, 'LIKE', '2026-03-28 15:46:23');
INSERT INTO `like` VALUES (26, 9, 3, 9, 'LIKE', '2026-03-28 15:48:30');
INSERT INTO `like` VALUES (27, 9, 3, 8, 'LIKE', '2026-03-28 15:48:49');
INSERT INTO `like` VALUES (29, 9, 3, 12, 'LIKE', '2026-03-28 15:51:59');
INSERT INTO `like` VALUES (30, 9, 1, 13, 'LIKE', '2026-03-28 17:09:23');
INSERT INTO `like` VALUES (32, 9, 1, 18, 'LIKE', '2026-03-28 17:58:28');
INSERT INTO `like` VALUES (33, 9, 3, 35, 'LIKE', '2026-03-28 21:47:17');
INSERT INTO `like` VALUES (34, 9, 1, 17, 'LIKE', '2026-03-28 22:11:29');
INSERT INTO `like` VALUES (35, 1, 1, 8, 'LIKE', '2026-03-28 22:39:25');
INSERT INTO `like` VALUES (38, 1, 2, 3, 'LIKE', '2026-03-29 15:01:45');

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
  `views` bigint(20) NOT NULL DEFAULT 0,
  `is_top` tinyint(4) NOT NULL DEFAULT 0,
  `is_essence` tinyint(4) NOT NULL DEFAULT 0,
  `last_reply_time` datetime NULL DEFAULT NULL,
  `status` tinyint(4) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_post_category_create`(`category_id` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_post_category_reply`(`category_id` ASC, `last_reply_time` ASC) USING BTREE,
  INDEX `idx_post_author`(`author_id` ASC) USING BTREE,
  INDEX `idx_post_category_likes`(`category_id` ASC, `likes` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES (1, 1, 1, '欢迎帖', '这是第一篇帖子', 'welcome', 0, 0, 0, 0, 0, 0, '2026-02-28 04:19:00', 0, '2026-02-28 04:19:00', '2026-02-28 04:19:00');
INSERT INTO `post` VALUES (2, 3, 1, 'Vue3+SpringBoot全栈开发实战', '<p>这是一篇测试帖子，包含<strong>富文本内容</strong>，演示Vue3和SpringBoot的整合使用。</p><p>核心要点：</p><ul><li>前端使用Vue3组合式API</li><li>后端使用SpringBoot+MyBatis</li><li>数据库使用MySQL</li></ul>', 'Vue3,SpringBoot,MySQL', 10, 5, 3, 0, 0, 0, '2026-02-28 04:30:16', 0, '2026-02-28 04:30:16', '2026-03-29 15:20:51');
INSERT INTO `post` VALUES (3, 3, 1, '2026年学习计划', '<p>1. 深入学习SpringCloud微服务<br>2. 掌握Vue3生态（Pinia、VueRouter）<br>3. 学习MySQL优化技巧</p>', '学习计划,2026', 5, 2, 1, 0, 0, 0, '2026-02-28 04:30:16', 0, '2026-02-28 04:30:16', '2026-03-29 15:37:31');
INSERT INTO `post` VALUES (4, 4, 1, 'MySQL索引优化实战', '<p>1. 避免使用SELECT *<br>2. 联合索引遵循最左前缀原则<br>3. 避免在索引列上做函数操作</p>', 'MySQL,索引优化,性能', 8, 4, 2, 0, 0, 0, '2026-02-28 04:30:16', 1, '2026-02-28 04:30:16', '2026-02-28 04:30:16');
INSERT INTO `post` VALUES (7, 1, 2, '实打实', '大苏打', NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2026-03-29 01:45:28', '2026-03-29 01:45:28');
INSERT INTO `post` VALUES (8, 1, 2, '图片帖', '<p><img src=\"/upload/editor/20260329/1774720851701.jpg\" style=\"max-width:100%;\" /></p><p><img src=\"/upload/editor/20260329/1774720844488.jpg\" style=\"max-width:100%;\" /></p>', NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2026-03-29 02:01:00', '2026-03-29 15:37:29');
INSERT INTO `post` VALUES (9, 9, 3, '我勒个豆', '<p>有点东西啊这个人</p><p><img src=\"/upload/editor/20260329/1774731457342.jpg\" style=\"max-width:100%;\" /></p><div><video controls style=\"max-width:100%;\"><source src=\"/upload/editor-video/20260329/1774731462270.mp4\" type=\"video/mp4\" /></video></div>', NULL, 0, 0, 0, 0, 0, 0, NULL, 0, '2026-03-29 04:57:45', '2026-03-29 15:37:30');

-- ----------------------------
-- Table structure for post_admin
-- ----------------------------
DROP TABLE IF EXISTS `post_admin`;
CREATE TABLE `post_admin`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NULL DEFAULT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'OWNER',
  `status` tinyint(4) NOT NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_post_admin_category_user`(`category_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_post_admin_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_post_admin_category`(`category_id` ASC) USING BTREE,
  INDEX `idx_post_admin_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_admin
-- ----------------------------
INSERT INTO `post_admin` VALUES (1, 1, 1, 1, 'OWNER', 0, '2026-03-29 00:48:45', '2026-03-29 15:39:06');
INSERT INTO `post_admin` VALUES (2, 2, 1, 1, 'OWNER', 0, '2026-03-29 00:48:45', '2026-03-29 15:37:21');
INSERT INTO `post_admin` VALUES (3, 3, 1, 1, 'OWNER', 0, '2026-03-29 00:48:45', '2026-03-29 05:12:50');
INSERT INTO `post_admin` VALUES (4, 4, 1, 1, 'OWNER', 0, '2026-03-29 00:48:45', '2026-03-29 05:11:06');
INSERT INTO `post_admin` VALUES (36, 1, 2, 1, 'OWNER', 0, '2026-03-29 15:39:06', '2026-03-29 15:39:08');
INSERT INTO `post_admin` VALUES (40, 1, 3, 1, 'OWNER', 1, '2026-03-29 15:39:13', '2026-03-29 15:39:13');

-- ----------------------------
-- Table structure for post_category
-- ----------------------------
DROP TABLE IF EXISTS `post_category`;
CREATE TABLE `post_category`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '',
  `creator_id` bigint(20) NULL DEFAULT NULL,
  `show` int(4) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT 1,
  `member_count` int(11) NOT NULL DEFAULT 0,
  `post_count` int(11) NOT NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_post_category_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_category
-- ----------------------------
INSERT INTO `post_category` VALUES (1, 'General', '通用板块', '', '', NULL, 0, 1, 0, 3, NULL, NULL);
INSERT INTO `post_category` VALUES (2, '技术分享', '编程技术相关内容', '', '', NULL, 0, 1, 0, 0, NULL, NULL);
INSERT INTO `post_category` VALUES (3, '日常记录', '生活日常分享', '', '', NULL, 0, 1, 1, 0, NULL, NULL);
INSERT INTO `post_category` VALUES (4, '经验总结', '学习/工作经验总结', '', '', NULL, 0, 1, 0, 0, NULL, NULL);

-- ----------------------------
-- Table structure for post_category_follow
-- ----------------------------
DROP TABLE IF EXISTS `post_category_follow`;
CREATE TABLE `post_category_follow`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_category_user`(`category_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_category_follow
-- ----------------------------
INSERT INTO `post_category_follow` VALUES (2, 3, 9, '2026-03-29 04:57:15');

-- ----------------------------
-- Table structure for post_role
-- ----------------------------
DROP TABLE IF EXISTS `post_role`;
CREATE TABLE `post_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `level` int(11) NOT NULL DEFAULT 0,
  `status` tinyint(4) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_post_role_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_role
-- ----------------------------
INSERT INTO `post_role` VALUES (1, 'OWNER', '吧主', 100, 1);
INSERT INTO `post_role` VALUES (2, 'MODERATOR', '吧务', 50, 1);

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
-- Table structure for site_carousel
-- ----------------------------
DROP TABLE IF EXISTS `site_carousel`;
CREATE TABLE `site_carousel`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '',
  `sort` int(11) NOT NULL DEFAULT 0,
  `status` tinyint(4) NOT NULL DEFAULT 1,
  `created_at` datetime NULL DEFAULT NULL,
  `updated_at` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of site_carousel
-- ----------------------------
INSERT INTO `site_carousel` VALUES (7, '欢迎来到 Iungo', '/upload/editor/20260327/1774601546526.jpg', '/home', 1, 1, '2026-03-27 22:03:52', '2026-03-27 22:03:52');
INSERT INTO `site_carousel` VALUES (8, '邀请制创作社区', '/upload/editor/20260327/1774601584846.jpg', '/blog/list', 2, 1, '2026-03-27 22:03:52', '2026-03-27 22:03:52');

-- ----------------------------
-- Table structure for site_config
-- ----------------------------
DROP TABLE IF EXISTS `site_config`;
CREATE TABLE `site_config`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'Iungo',
  `subtitle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '邀请制深度创作社区',
  `logo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '',
  `favicon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '',
  `icp_no` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '',
  `nav_home_text` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '首页',
  `nav_blog_text` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '圈层博客',
  `nav_invite_text` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '我的邀请',
  `nav_publish_text` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '发布博客',
  `footer_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'Iungo © 2026 邀请制深度创作社区',
  `footer_extra` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `created_at` datetime NULL DEFAULT NULL,
  `updated_at` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of site_config
-- ----------------------------
INSERT INTO `site_config` VALUES (1, 'Iungo', '邀请制深度创作者社区', '/upload/editor/20260327/1774621240386.png', '/upload/editor/20260327/1774621246510.png', '', '首页', '圈层博客', '我的邀请', '发布博客', 'Iungo © 2026 邀请制深度创作社区', 'hhhh', '2026-03-27 14:59:27', '2026-03-27 22:20:54');

-- ----------------------------
-- Table structure for sub
-- ----------------------------
DROP TABLE IF EXISTS `sub`;
CREATE TABLE `sub`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `read` tinyint(4) NOT NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sub_user_read_time`(`user_id` ASC, `read` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sub
-- ----------------------------
INSERT INTO `sub` VALUES (1, 1, '你的帖子《图片帖》收到了新评论', 1, '2026-03-29 04:55:43', NULL);
INSERT INTO `sub` VALUES (2, 1, '你的帖子《图片帖》收到了新评论', 0, '2026-03-29 04:55:51', NULL);
INSERT INTO `sub` VALUES (3, 3, '你的帖子《2026年学习计划》收到了新评论', 0, '2026-03-29 14:26:44', NULL);
INSERT INTO `sub` VALUES (4, 3, '你的帖子《2026年学习计划》收到了新评论', 0, '2026-03-29 14:27:10', NULL);
INSERT INTO `sub` VALUES (5, 3, '你的帖子《2026年学习计划》收到了新评论', 0, '2026-03-29 14:27:15', NULL);
INSERT INTO `sub` VALUES (6, 3, '你的帖子《2026年学习计划》收到了新评论', 0, '2026-03-29 14:27:17', NULL);
INSERT INTO `sub` VALUES (7, 3, '你的帖子《2026年学习计划》收到了新评论', 0, '2026-03-29 14:27:19', NULL);
INSERT INTO `sub` VALUES (8, 3, '你的帖子《2026年学习计划》收到了新评论', 0, '2026-03-29 14:27:21', NULL);
INSERT INTO `sub` VALUES (9, 3, '你的帖子《2026年学习计划》收到了新评论', 0, '2026-03-29 14:27:22', NULL);
INSERT INTO `sub` VALUES (10, 3, '你的帖子《2026年学习计划》收到了新评论', 0, '2026-03-29 15:07:14', NULL);
INSERT INTO `sub` VALUES (11, 3, '你的帖子《2026年学习计划》收到了新评论', 0, '2026-03-29 15:07:16', NULL);
INSERT INTO `sub` VALUES (12, 3, '你的帖子《2026年学习计划》收到了新评论', 0, '2026-03-29 15:19:53', NULL);

-- ----------------------------
-- Table structure for target
-- ----------------------------
DROP TABLE IF EXISTS `target`;
CREATE TABLE `target`  (
  `id` int(11) NOT NULL,
  `target_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `target_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

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
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `role_id` bigint(20) NULL DEFAULT 1,
  `status` tinyint(4) NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 2, 'e10adc3949ba59abbe56e057f20f883e', 'admin@iungo.com', '你', '/upload/avatars/20260317/1773698943353.png', '/upload/editor/20260327/1774612077158.jpg|7', '猜猜', 2, 1, '2026-03-17 02:01:07', '2026-03-27 22:38:44');
INSERT INTO `user` VALUES (2, 'alice', 2, 'e10adc3949ba59abbe56e057f20f883e', 'alice@example.com', '爱丽丝', '', '', '前端开发', 1, NULL, '2026-03-17 02:01:07', '2026-03-27 22:32:04');
INSERT INTO `user` VALUES (3, 'bob', 1, 'e10adc3949ba59abbe56e057f20f883e', 'bob@example.com', '鲍勃', NULL, '', '后端开发', 1, 1, '2026-03-17 02:01:07', '2026-03-17 02:01:07');
INSERT INTO `user` VALUES (4, 'charlie', 0, 'e10adc3949ba59abbe56e057f20f883e', 'charlie@example.com', '查理', NULL, '', '被邀请新用户', 1, 1, '2026-03-17 02:01:07', '2026-03-17 02:01:07');
INSERT INTO `user` VALUES (8, 'test', NULL, 'e10adc3949ba59abbe56e057f20f883e', 'test@example.com', 'Test User', NULL, '', NULL, 1, 1, '2026-03-17 02:04:16', '2026-03-17 02:04:16');
INSERT INTO `user` VALUES (9, 'hirasawa', 1, 'dca8650e324d99d1d0fe766927bfded9', '2464280450@qq.com', 'Jiva', '', '', '我嘞个都', 1, NULL, '2026-03-17 02:06:14', '2026-03-27 13:17:08');
INSERT INTO `user` VALUES (10, 'adad', 2, 'dca8650e324d99d1d0fe766927bfded9', '3219211968@qq.com', '', '', '', '', 1, NULL, '2026-03-27 16:30:00', '2026-03-27 16:30:44');

-- ----------------------------
-- Table structure for post_category_mute
-- ----------------------------
DROP TABLE IF EXISTS `post_category_mute`;
CREATE TABLE `post_category_mute`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `operator_id` bigint(20) NULL DEFAULT NULL,
  `mute_until` datetime NOT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '1=active,0=disabled',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pcm_category_user`(`category_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_pcm_category_until`(`category_id` ASC, `mute_until` ASC) USING BTREE,
  INDEX `idx_pcm_user_until`(`user_id` ASC, `mute_until` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for blog_user_visibility
-- ----------------------------
DROP TABLE IF EXISTS `blog_user_visibility`;
CREATE TABLE `blog_user_visibility`  (
  `user_id` bigint(20) NOT NULL,
  `visibility_scope` tinyint(4) NOT NULL DEFAULT 3 COMMENT '3=all,2=followers,1=mutual,0=self',
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- 博客/帖子/评论主键改为毫秒时间戳后，引用它们的 target_id 须为 BIGINT
-- （已有库请执行以下语句；新库若从上方整库导入可一并执行）
-- ----------------------------
ALTER TABLE `comment` MODIFY COLUMN `target_id` BIGINT NULL DEFAULT NULL;
ALTER TABLE `favorite` MODIFY COLUMN `target_id` BIGINT NOT NULL;
ALTER TABLE `like` MODIFY COLUMN `target_id` BIGINT NULL DEFAULT NULL;
ALTER TABLE `collection` MODIFY COLUMN `target_id` BIGINT NULL DEFAULT NULL;

SET FOREIGN_KEY_CHECKS = 1;
