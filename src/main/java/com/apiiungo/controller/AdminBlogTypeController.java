package com.apiiungo.controller;

import com.apiiungo.entity.BlogType;
import com.apiiungo.entity.User;
import com.apiiungo.mapper.BlogTypeMapper;
import com.apiiungo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/blog-type")
public class AdminBlogTypeController {

    @Autowired
    private BlogTypeMapper blogTypeMapper;
    @Autowired
    private UserService userService;

    private User getCurrentUser(HttpServletRequest request) {
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
        return userService.findByUsername(username);
    }

    private boolean isAdmin(User u) {
        return u != null && u.getRoleId() != null && u.getRoleId() == 2L;
    }

    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "5") Integer size,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) Integer id,
                                    HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User admin = getCurrentUser(request);
        if (!isAdmin(admin)) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        List<BlogType> all = blogTypeMapper.selectList(null);
        List<BlogType> filtered = new ArrayList<>();
        String kw = keyword == null ? null : keyword.trim().toLowerCase();
        for (BlogType t : all) {
            if (t == null) continue;
            if (id != null) {
                if (t.getId() != null && t.getId().equals(id)) {
                    filtered.add(t);
                }
                continue;
            }
            if (kw == null || kw.isEmpty()) {
                filtered.add(t);
                continue;
            }
            String hay = (String.valueOf(t.getId()) + " "
                    + String.valueOf(t.getName()) + " "
                    + String.valueOf(t.getDescription())).toLowerCase();
            if (hay.contains(kw)) {
                filtered.add(t);
            }
        }

        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 5 : Math.min(size, 100);
        int total = filtered.size();
        int from = Math.min((safePage - 1) * safeSize, total);
        int to = Math.min(from + safeSize, total);
        List<BlogType> types = filtered.subList(from, to);
        result.put("code", 200);
        result.put("data", types);
        result.put("total", total);
        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody BlogType type, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User admin = getCurrentUser(request);
        if (!isAdmin(admin)) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        type.setId(null);
        if (type.getShow() == null) {
            type.setShow(1);
        }
        int n = blogTypeMapper.insert(type);
        result.put("code", n > 0 ? 200 : 400);
        result.put("msg", n > 0 ? "创建成功" : "创建失败");
        return result;
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody BlogType type, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User admin = getCurrentUser(request);
        if (!isAdmin(admin)) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        if (type.getId() == null) {
            result.put("code", 400);
            result.put("msg", "缺少ID");
            return result;
        }
        int n = blogTypeMapper.updateById(type);
        result.put("code", n > 0 ? 200 : 400);
        result.put("msg", n > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestParam Integer id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User admin = getCurrentUser(request);
        if (!isAdmin(admin)) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        int n = blogTypeMapper.deleteById(id);
        result.put("code", n > 0 ? 200 : 400);
        result.put("msg", n > 0 ? "删除成功" : "删除失败");
        return result;
    }
}

