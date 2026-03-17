package com.apiiungo.controller;

import com.apiiungo.entity.User;
import com.apiiungo.mapper.UserMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        Long v = jdbcTemplate.queryForObject(sql, Long.class);
        return v == null ? 0L : v;
    }
}

