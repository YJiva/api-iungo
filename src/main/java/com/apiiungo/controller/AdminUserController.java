package com.apiiungo.controller;

import com.apiiungo.entity.User;
import com.apiiungo.mapper.UserMapper;
import com.apiiungo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "5") Integer size,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) Long id,
                                    HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User admin = getCurrentUser(request);
        if (admin == null || admin.getRoleId() == null || admin.getRoleId() != 2L) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        List<User> all = userMapper.selectAll();
        List<User> filtered = new ArrayList<>();
        String kw = keyword == null ? null : keyword.trim().toLowerCase();
        for (User u : all) {
            if (u == null) continue;
            if (id != null) {
                if (u.getId() != null && u.getId().equals(id)) {
                    filtered.add(u);
                }
                continue;
            }
            if (kw == null || kw.isEmpty()) {
                filtered.add(u);
                continue;
            }
            String hay = (String.valueOf(u.getId()) + " "
                    + String.valueOf(u.getUsername()) + " "
                    + String.valueOf(u.getNickname()) + " "
                    + String.valueOf(u.getEmail()) + " "
                    + String.valueOf(u.getBio())).toLowerCase();
            if (hay.contains(kw)) {
                filtered.add(u);
            }
        }

        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 5 : Math.min(size, 100);
        int total = filtered.size();
        int from = Math.min((safePage - 1) * safeSize, total);
        int to = Math.min(from + safeSize, total);
        List<User> users = filtered.subList(from, to);
        result.put("code", 200);
        result.put("data", users);
        result.put("total", total);
        return result;
    }

    @PostMapping("/update-status")
    public Map<String, Object> updateStatus(@RequestParam Long userId, @RequestParam Integer status,
                                            HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User admin = getCurrentUser(request);
        if (admin == null || admin.getRoleId() == null || admin.getRoleId() != 2L) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            result.put("code", 404);
            result.put("msg", "用户不存在");
            return result;
        }
        user.setStatus(status);
        userMapper.updateUser(user);
        result.put("code", 200);
        result.put("msg", "状态已更新");
        return result;
    }

    @PostMapping("/update-role")
    public Map<String, Object> updateRole(@RequestParam Long userId, @RequestParam Long roleId,
                                          HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User admin = getCurrentUser(request);
        if (admin == null || admin.getRoleId() == null || admin.getRoleId() != 2L) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            result.put("code", 404);
            result.put("msg", "用户不存在");
            return result;
        }
        user.setRoleId(roleId);
        userMapper.updateUser(user);
        result.put("code", 200);
        result.put("msg", "角色已更新");
        return result;
    }

    /**
     * 管理员编辑用户资料（不修改密码）
     */
    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody User input, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User admin = getCurrentUser(request);
        if (admin == null || admin.getRoleId() == null || admin.getRoleId() != 2L) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        if (input == null || input.getId() == null) {
            result.put("code", 400);
            result.put("msg", "缺少用户ID");
            return result;
        }
        User db = userMapper.selectById(input.getId());
        if (db == null) {
            result.put("code", 404);
            result.put("msg", "用户不存在");
            return result;
        }

        // 合并可编辑字段：不传则保留原值
        if (input.getUsername() != null) db.setUsername(input.getUsername());
        if (input.getEmail() != null) db.setEmail(input.getEmail());
        if (input.getNickname() != null) db.setNickname(input.getNickname());
        if (input.getAvatar() != null) db.setAvatar(input.getAvatar());
        if (input.getBio() != null) db.setBio(input.getBio());
        if (input.getGender() != null) db.setGender(input.getGender());
        if (input.getStatus() != null) db.setStatus(input.getStatus());

        // 明确不允许通过该接口修改密码
        db.setPassword(null);
        db.setUpdateTime(LocalDateTime.now());

        int n = userMapper.updateUser(db);
        result.put("code", n > 0 ? 200 : 400);
        result.put("msg", n > 0 ? "更新成功" : "更新失败");
        return result;
    }

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
}

