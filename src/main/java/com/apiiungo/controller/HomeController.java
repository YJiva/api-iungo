package com.apiiungo.controller;

import com.apiiungo.entity.User;
import com.apiiungo.mapper.UserMapper;
import com.apiiungo.service.BlogService;
import com.apiiungo.vo.PublicBlogItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BlogService blogService;

    /**
     * 返回所有用户名列表，供首页测试区域使用。
     */
    @GetMapping("/usernames")
    public Map<String, Object> usernames() {
        Map<String, Object> result = new HashMap<>();
        List<User> users = userMapper.selectAll();
        List<String> names = users.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        result.put("code", 200);
        result.put("data", names);
        return result;
    }

    /**
     * 根据用户名返回用户详情，供首页测试区域使用。
     */
    @GetMapping("/user")
    public Map<String, Object> user(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            result.put("code", 404);
            result.put("msg", "用户不存在");
        } else {
            result.put("code", 200);
            result.put("data", user);
        }
        return result;
    }

    /**
     * 首页统计信息：总用户、总帖子、总博客、总评论、总收藏。
     */
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        Long totalUsers = queryForLong("SELECT COUNT(*) FROM user");
        Long totalPosts = queryForLong("SELECT COUNT(*) FROM post");
        Long totalBlogs = queryForLong("SELECT COUNT(*) FROM blog");
        Long totalComments = queryForLong("SELECT COUNT(*) FROM comment");
        Long totalFavorites = queryForLong("SELECT COUNT(*) FROM favorite");

        data.put("totalUsers", totalUsers);
        data.put("totalPosts", totalPosts);
        data.put("totalBlogs", totalBlogs);
        data.put("totalComments", totalComments);
        data.put("totalFavorites", totalFavorites);

        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    private Long queryForLong(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return 0L;
        }
        Long v = jdbcTemplate.queryForObject(sql, Long.class);
        return v == null ? 0L : v;
    }

    /**
     * 首页搜索（分页）：
     * type: post | category | blog | user
     */
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String type,
                                      @RequestParam(defaultValue = "") String keyword,
                                      @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        String t = type == null ? "" : type.trim().toLowerCase();
        String kw = keyword == null ? "" : keyword.trim();
        int p = page == null || page < 1 ? 1 : page;
        int size = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 30);
        int offset = (p - 1) * size;
        String like = "%" + kw + "%";

        List<Map<String, Object>> list = new ArrayList<>();
        Long total = 0L;

        if ("post".equals(t)) {
            total = queryForLong(
                    "SELECT COUNT(*) " +
                            "FROM post p " +
                            "LEFT JOIN user u ON p.author_id = u.id " +
                            "LEFT JOIN post_category pc ON p.category_id = pc.id " +
                            "WHERE p.title LIKE ? " +
                            "   OR p.content LIKE ? " +
                            "   OR u.nickname LIKE ? " +
                            "   OR u.username LIKE ? " +
                            "   OR pc.name LIKE ? " +
                            "   OR pc.description LIKE ?",
                    like, like, like, like, like, like
            );
            list = jdbcTemplate.queryForList(
                    "SELECT p.id, p.category_id, p.title, p.content, p.author_id, p.create_time, " +
                            "u.nickname AS author_nickname, u.username AS author_username, " +
                            "pc.name AS category_name " +
                            "FROM post p " +
                            "LEFT JOIN user u ON p.author_id = u.id " +
                            "LEFT JOIN post_category pc ON p.category_id = pc.id " +
                            "WHERE p.title LIKE ? " +
                            "   OR p.content LIKE ? " +
                            "   OR u.nickname LIKE ? " +
                            "   OR u.username LIKE ? " +
                            "   OR pc.name LIKE ? " +
                            "   OR pc.description LIKE ? " +
                            "ORDER BY p.create_time DESC LIMIT ? OFFSET ?",
                    like, like, like, like, like, like, size, offset
            );
        } else if ("category".equals(t)) {
            total = queryForLong(
                    "SELECT COUNT(*) FROM post_category WHERE name LIKE ? OR description LIKE ?",
                    like, like
            );
            list = jdbcTemplate.queryForList(
                    "SELECT id, name, description, icon, member_count, post_count " +
                            "FROM post_category WHERE name LIKE ? OR description LIKE ? " +
                            "ORDER BY id DESC LIMIT ? OFFSET ?",
                    like, like, size, offset
            );
        } else if ("blog".equals(t)) {
            Map<String, Object> blogPage = blogService.pagePublicBlogs(kw, p, size);
            total = ((Number) blogPage.get("total")).longValue();
            @SuppressWarnings("unchecked")
            List<PublicBlogItem> items = (List<PublicBlogItem>) blogPage.get("list");
            list = new ArrayList<>();
            if (items != null) {
                for (PublicBlogItem b : items) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", b.getId());
                    row.put("title", b.getTitle());
                    row.put("content", b.getContent());
                    row.put("user_id", b.getUserId());
                    row.put("create_time", b.getCreateTime());
                    row.put("read", b.getRead());
                    row.put("tags", b.getTags());
                    row.put("author_nickname", b.getAuthorNickname());
                    row.put("author_username", b.getAuthorUsername());
                    list.add(row);
                }
            }
        } else if ("user".equals(t)) {
            total = queryForLong(
                    "SELECT COUNT(*) FROM user WHERE username LIKE ? OR nickname LIKE ? OR bio LIKE ?",
                    like, like, like
            );
            list = jdbcTemplate.queryForList(
                    "SELECT id, username, nickname, avatar, bio, create_time " +
                            "FROM user WHERE username LIKE ? OR nickname LIKE ? OR bio LIKE ? " +
                            "ORDER BY id DESC LIMIT ? OFFSET ?",
                    like, like, like, size, offset
            );
        } else {
            result.put("code", 400);
            result.put("msg", "不支持的搜索类型");
            return result;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        data.put("page", p);
        data.put("pageSize", size);
        data.put("hasMore", (long) p * size < total);
        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    private Long queryForLong(String sql, Object... args) {
        if (sql == null || sql.trim().isEmpty()) {
            return 0L;
        }
        Long v = jdbcTemplate.queryForObject(sql, Long.class, args);
        return v == null ? 0L : v;
    }
}

