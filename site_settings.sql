-- 站点配置与轮播图
-- 执行前请确认数据库为 iungoDB

CREATE TABLE IF NOT EXISTS `site_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(120) NOT NULL DEFAULT 'Iungo',
  `logo_url` varchar(255) DEFAULT '',
  `favicon_url` varchar(255) DEFAULT '',
  `icp_no` varchar(120) DEFAULT '',
  `banner_image_url` varchar(255) DEFAULT '',
  `banner_link` varchar(255) DEFAULT '',
  `banner_text` varchar(255) DEFAULT '',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `site_carousel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(120) DEFAULT '',
  `image_url` varchar(255) NOT NULL,
  `link_url` varchar(255) DEFAULT '',
  `sort` int(11) NOT NULL DEFAULT 0,
  `status` tinyint(4) NOT NULL DEFAULT 1,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始化一条站点配置（仅当不存在时）
INSERT INTO `site_config` (`title`, `logo_url`, `favicon_url`, `icp_no`, `banner_image_url`, `banner_link`, `banner_text`, `created_at`, `updated_at`)
SELECT 'Iungo', '', '', '', '', '', '', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `site_config`);

-- 示例轮播图（仅当 site_carousel 为空时插入，可按需删除）
INSERT INTO `site_carousel` (`title`, `image_url`, `link_url`, `sort`, `status`, `created_at`, `updated_at`)
SELECT '欢迎来到 Iungo', 'https://via.placeholder.com/1200x360?text=Iungo+Slide+1', '/home', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `site_carousel`);

INSERT INTO `site_carousel` (`title`, `image_url`, `link_url`, `sort`, `status`, `created_at`, `updated_at`)
SELECT '邀请制创作社区', 'https://via.placeholder.com/1200x360?text=Iungo+Slide+2', '/blog/list', 2, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `site_carousel` WHERE `sort` = 2);

