package com.apiiungo.controller;

import com.apiiungo.entity.User;
import com.apiiungo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SiteController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @GetMapping("/site/config")
    public Map<String, Object> getSiteConfig() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> row = getOrInitSiteConfig();
        result.put("code", 200);
        result.put("data", row);
        return result;
    }

    @GetMapping("/site/carousels")
    public Map<String, Object> getCarousels() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, title, image_url, link_url, sort, status FROM site_carousel WHERE status = 1 ORDER BY sort ASC, id ASC");
        result.put("code", 200);
        result.put("data", rows);
        return result;
    }

    @GetMapping("/admin/site/detail")
    public Map<String, Object> adminDetail(HttpServletRequest request) {
        Map<String, Object> auth = requireAdmin(request);
        if (auth != null) {
            return auth;
        }
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("config", getOrInitSiteConfig());
        List<Map<String, Object>> carousels = jdbcTemplate.queryForList(
                "SELECT id, title, image_url, link_url, sort, status FROM site_carousel ORDER BY sort ASC, id ASC");
        data.put("carousels", carousels);
        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    @PostMapping("/admin/site/config/update")
    public Map<String, Object> updateConfig(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Map<String, Object> auth = requireAdmin(request);
        if (auth != null) {
            return auth;
        }
        Map<String, Object> cfg = getOrInitSiteConfig();
        Long id = toLong(cfg.get("id"));
        String title = toString(payload.get("title"));
        String logoUrl = toString(payload.get("logoUrl"));
        String faviconUrl = toString(payload.get("faviconUrl"));
        String icpNo = toString(payload.get("icpNo"));
        String subtitle = toString(payload.get("subtitle"));
        String navHomeText = toString(payload.get("navHomeText"));
        String navBlogText = toString(payload.get("navBlogText"));
        String navInviteText = toString(payload.get("navInviteText"));
        String navPublishText = toString(payload.get("navPublishText"));
        String footerText = toString(payload.get("footerText"));
        String footerExtra = toString(payload.get("footerExtra"));

        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update(
                "UPDATE site_config SET title=?, logo_url=?, favicon_url=?, icp_no=?, subtitle=?, nav_home_text=?, nav_blog_text=?, nav_invite_text=?, nav_publish_text=?, footer_text=?, footer_extra=?, updated_at=? WHERE id=?",
                title, logoUrl, faviconUrl, icpNo, subtitle, navHomeText, navBlogText, navInviteText, navPublishText, footerText, footerExtra, Timestamp.valueOf(now), id
        );
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "站点配置更新成功");
        return result;
    }

    @PostMapping("/admin/site/carousels/replace")
    public Map<String, Object> replaceCarousels(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Map<String, Object> auth = requireAdmin(request);
        if (auth != null) {
            return auth;
        }
        Object listObj = payload.get("list");
        List<?> list = listObj instanceof List ? (List<?>) listObj : new ArrayList<>();
        jdbcTemplate.update("DELETE FROM site_carousel");

        LocalDateTime now = LocalDateTime.now();
        for (Object itemObj : list) {
            if (!(itemObj instanceof Map)) {
                continue;
            }
            Map<?, ?> item = (Map<?, ?>) itemObj;
            String title = toString(item.get("title"));
            String imageUrl = toString(item.get("imageUrl"));
            if (imageUrl.isEmpty()) {
                continue;
            }
            String linkUrl = toString(item.get("linkUrl"));
            Integer sort = toInt(item.get("sort"), 0);
            Integer status = toInt(item.get("status"), 1);
            jdbcTemplate.update(
                    "INSERT INTO site_carousel(title, image_url, link_url, sort, status, created_at, updated_at) VALUES(?,?,?,?,?,?,?)",
                    title, imageUrl, linkUrl, sort, status, Timestamp.valueOf(now), Timestamp.valueOf(now)
            );
        }
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "轮播图保存成功");
        return result;
    }

    private Map<String, Object> getOrInitSiteConfig() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, title, logo_url, favicon_url, icp_no, subtitle, nav_home_text, nav_blog_text, nav_invite_text, nav_publish_text, footer_text, footer_extra FROM site_config ORDER BY id ASC LIMIT 1");
        if (!rows.isEmpty()) {
            return rows.get(0);
        }
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update(
                "INSERT INTO site_config(title, logo_url, favicon_url, icp_no, subtitle, nav_home_text, nav_blog_text, nav_invite_text, nav_publish_text, footer_text, footer_extra, created_at, updated_at) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)",
                "Iungo", "", "", "", "邀请制深度创作社区", "首页", "圈层博客", "我的邀请", "发布博客", "Iungo © 2026 邀请制深度创作社区", "", Timestamp.valueOf(now), Timestamp.valueOf(now)
        );
        return jdbcTemplate.queryForMap(
                "SELECT id, title, logo_url, favicon_url, icp_no, subtitle, nav_home_text, nav_blog_text, nav_invite_text, nav_publish_text, footer_text, footer_extra FROM site_config ORDER BY id ASC LIMIT 1");
    }

    private Map<String, Object> requireAdmin(HttpServletRequest request) {
        User current = getCurrentUser(request);
        if (current == null || current.getRoleId() == null || current.getRoleId() != 2L) {
            Map<String, Object> denied = new HashMap<>();
            denied.put("code", 403);
            denied.put("msg", "无权限");
            return denied;
        }
        return null;
    }

    private User getCurrentUser(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null) {
            return null;
        }
        String token = auth.trim();
        if (token.toLowerCase().startsWith("bearer ")) {
            token = token.substring(7).trim();
        }
        if (!token.startsWith("dev-token:")) {
            return null;
        }
        String username = token.substring("dev-token:".length());
        return userService.findByUsername(username);
    }

    private String toString(Object v) {
        return v == null ? "" : String.valueOf(v).trim();
    }

    private Integer toInt(Object v, int fallback) {
        if (v == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(String.valueOf(v));
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private Long toLong(Object v) {
        if (v == null) {
            return null;
        }
        try {
            return Long.parseLong(String.valueOf(v));
        } catch (Exception ignored) {
            return null;
        }
    }
}

