package com.apiiungo.controller;

import com.apiiungo.entity.Role;
import com.apiiungo.entity.User;
import com.apiiungo.mapper.RoleMapper;
import com.apiiungo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/role")
public class AdminRoleController {

    @Autowired
    private RoleMapper roleMapper;
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
    public Map<String, Object> list(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User admin = getCurrentUser(request);
        if (!isAdmin(admin)) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        List<Role> roles = roleMapper.selectList(null);
        result.put("code", 200);
        result.put("data", roles);
        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody Role role, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User admin = getCurrentUser(request);
        if (!isAdmin(admin)) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        role.setId(null);
        int n = roleMapper.insert(role);
        result.put("code", n > 0 ? 200 : 400);
        result.put("msg", n > 0 ? "创建成功" : "创建失败");
        return result;
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody Role role, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User admin = getCurrentUser(request);
        if (!isAdmin(admin)) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        if (role.getId() == null) {
            result.put("code", 400);
            result.put("msg", "缺少角色ID");
            return result;
        }
        int n = roleMapper.updateById(role);
        result.put("code", n > 0 ? 200 : 400);
        result.put("msg", n > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestParam Long id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User admin = getCurrentUser(request);
        if (!isAdmin(admin)) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        int n = roleMapper.deleteById(id);
        result.put("code", n > 0 ? 200 : 400);
        result.put("msg", n > 0 ? "删除成功" : "删除失败");
        return result;
    }
}

