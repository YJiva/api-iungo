package com.apiiungo.controller;

import com.apiiungo.entity.Sub;
import com.apiiungo.entity.User;
import com.apiiungo.service.SubService;
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
@RequestMapping("/api/sub")
public class SubController {

    @Autowired
    private SubService subService;
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Map<String, Object> list(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        List<Sub> list = subService.listByUser(userId);
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    @PostMapping("/read")
    public Map<String, Object> markRead(@RequestParam Long id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        boolean ok = subService.markRead(userId, id);
        result.put("code", ok ? 200 : 400);
        result.put("msg", ok ? "已标记为已读" : "标记失败");
        return result;
    }

    @PostMapping("/read-all")
    public Map<String, Object> markAllRead(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        int count = subService.markAllRead(userId);
        result.put("code", 200);
        result.put("msg", "已全部标记为已读");
        result.put("count", count);
        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> deleteOne(@RequestParam Long id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        boolean ok = subService.deleteById(userId, id);
        result.put("code", ok ? 200 : 400);
        result.put("msg", ok ? "删除成功" : "删除失败");
        return result;
    }

    @PostMapping("/delete-all")
    public Map<String, Object> deleteAll(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        int count = subService.deleteAll(userId);
        result.put("code", 200);
        result.put("msg", "删除成功");
        result.put("count", count);
        return result;
    }

    @PostMapping("/delete-read")
    public Map<String, Object> deleteRead(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        int count = subService.deleteRead(userId);
        result.put("code", 200);
        result.put("msg", "已删除全部已读通知");
        result.put("count", count);
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

