package com.apiiungo.controller;

import com.apiiungo.entity.User;
import com.apiiungo.service.FollowService;
import com.apiiungo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;
    @Autowired
    private UserService userService;

    @PostMapping("/toggle")
    public Map<String, Object> toggle(@RequestParam Long targetId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        if (userId.equals(targetId)) {
            result.put("code", 400);
            result.put("msg", "不能关注自己");
            return result;
        }
        boolean following = followService.toggleFollow(userId, targetId);
        result.put("code", 200);
        result.put("following", following);
        result.put("msg", following ? "已关注" : "已取消关注");
        return result;
    }

    @GetMapping("/status")
    public Map<String, Object> status(@RequestParam Long targetId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        boolean following = followService.isFollowing(userId, targetId);
        result.put("code", 200);
        result.put("following", following);
        return result;
    }

    @GetMapping("/following")
    public Map<String, Object> following(@RequestParam(required = false) Long userId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long current = userId != null ? userId : getUserIdFromRequest(request);
        if (current == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        List<User> list = followService.listFollowing(current);
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    @GetMapping("/followers")
    public Map<String, Object> followers(@RequestParam(required = false) Long userId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long current = userId != null ? userId : getUserIdFromRequest(request);
        if (current == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        List<User> list = followService.listFollowers(current);
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null) {
            return null;
        }
        String token = auth.trim();
        if (token.isEmpty()) {
            return null;
        }
        String lower = token.toLowerCase();
        if (lower.startsWith("bearer ")) {
            token = token.substring(7).trim();
        }
        if (!token.startsWith("dev-token:")) {
            return null;
        }
        String username = token.substring("dev-token:".length());
        User user = userService.findByUsername(username);
        return user == null ? null : user.getId();
    }
}

