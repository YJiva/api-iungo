package com.apiiungo.controller;

import com.apiiungo.entity.PostAdmin;
import com.apiiungo.entity.PostCategory;
import com.apiiungo.entity.User;
import com.apiiungo.mapper.PostAdminMapper;
import com.apiiungo.mapper.PostCategoryFollowMapper;
import com.apiiungo.mapper.PostCategoryMapper;
import com.apiiungo.mapper.PostMapper;
import com.apiiungo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/post/category")
public class PostCategoryController {

    @Autowired
    private PostCategoryMapper postCategoryMapper;
    @Autowired
    private PostAdminMapper postAdminMapper;
    @Autowired
    private PostCategoryFollowMapper postCategoryFollowMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private PostMapper postMapper;

    @GetMapping("/list")
    public Map<String, Object> list(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        List<PostCategory> list = postCategoryMapper.selectList(null);
        Long uid = getCurrentUserId(request);
        List<Map<String, Object>> data = new java.util.ArrayList<>();
        for (PostCategory c : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", c.getId());
            item.put("name", c.getName());
            item.put("description", c.getDescription());
            item.put("icon", c.getIcon());
            item.put("coverUrl", c.getCoverUrl());
            int memberCount = postCategoryFollowMapper.countByCategory(c.getId().longValue());
            int postCount = postMapper.countByCategory(c.getId().longValue());
            item.put("memberCount", memberCount);
            item.put("postCount", postCount);
            if (uid != null) {
                item.put("followed", postCategoryFollowMapper.exists(c.getId().longValue(), uid) > 0);
            }
            data.add(item);
        }
        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    @GetMapping("/detail")
    public Map<String, Object> detail(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        PostCategory c = postCategoryMapper.selectById(id);
        if (c == null) {
            result.put("code", 404);
            result.put("msg", "板块不存在");
            return result;
        }
        List<PostAdmin> admins = postAdminMapper.selectByCategory(id);
        Map<String, Object> data = new HashMap<>();
        data.put("category", c);
        data.put("admins", admins);
        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody PostCategory input, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User u = getCurrentUser(request);
        if (u == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        if (input == null || input.getName() == null || input.getName().trim().isEmpty()) {
            result.put("code", 400);
            result.put("msg", "板块名称不能为空");
            return result;
        }
        input.setName(input.getName().trim());
        if (input.getShow() == null) {
            input.setShow(1);
        }
        postCategoryMapper.insert(input);

        PostAdmin owner = new PostAdmin();
        owner.setCategoryId(input.getId().longValue());
        owner.setUserId(u.getId());
        owner.setRole("OWNER");
        owner.setStatus(1);
        owner.setCreateTime(LocalDateTime.now());
        owner.setUpdateTime(LocalDateTime.now());
        postAdminMapper.insert(owner);

        result.put("code", 200);
        result.put("msg", "创建成功");
        result.put("data", input);
        return result;
    }

    @PostMapping("/admin/add")
    public Map<String, Object> addAdmin(@RequestParam Long categoryId,
                                        @RequestParam Long userId,
                                        @RequestParam(defaultValue = "MODERATOR") String role,
                                        HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User op = getCurrentUser(request);
        if (op == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        if (!canManageCategory(op, categoryId)) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        if (postAdminMapper.existsActive(categoryId, userId) > 0) {
            result.put("code", 400);
            result.put("msg", "该用户已是吧务");
            return result;
        }
        PostAdmin pa = new PostAdmin();
        pa.setCategoryId(categoryId);
        pa.setUserId(userId);
        pa.setRole(role == null || role.trim().isEmpty() ? "MODERATOR" : role.trim().toUpperCase());
        pa.setStatus(1);
        pa.setCreateTime(LocalDateTime.now());
        pa.setUpdateTime(LocalDateTime.now());
        postAdminMapper.insert(pa);

        result.put("code", 200);
        result.put("msg", "添加成功");
        return result;
    }

    @PostMapping("/admin/remove")
    public Map<String, Object> removeAdmin(@RequestParam Long categoryId,
                                           @RequestParam Long userId,
                                           HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User op = getCurrentUser(request);
        if (op == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        if (!canManageCategory(op, categoryId)) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        int n = postAdminMapper.disable(categoryId, userId);
        result.put("code", n > 0 ? 200 : 400);
        result.put("msg", n > 0 ? "移除成功" : "移除失败");
        return result;
    }


    @PostMapping("/owner/transfer")
    public Map<String, Object> transferOwner(@RequestParam Long categoryId,
                                             @RequestParam Long toUserId,
                                             HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User op = getCurrentUser(request);
        if (op == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        if (!canManageCategory(op, categoryId)) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }

        PostCategory c = postCategoryMapper.selectById(categoryId);
        if (c == null) {
            result.put("code", 404);
            result.put("msg", "板块不存在");
            return result;
        }

        // 先移除当前 OWNER（若有）
        List<PostAdmin> admins = postAdminMapper.selectByCategory(categoryId);
        for (PostAdmin a : admins) {
            if ("OWNER".equalsIgnoreCase(a.getRole())) {
                postAdminMapper.disable(categoryId, a.getUserId());
            }
        }

        // 给新用户设置 OWNER
        if (postAdminMapper.existsActive(categoryId, toUserId) > 0) {
            postAdminMapper.disable(categoryId, toUserId);
        }
        PostAdmin owner = new PostAdmin();
        owner.setCategoryId(categoryId);
        owner.setUserId(toUserId);
        owner.setRole("OWNER");
        owner.setStatus(1);
        owner.setCreateTime(LocalDateTime.now());
        owner.setUpdateTime(LocalDateTime.now());
        postAdminMapper.insert(owner);

        result.put("code", 200);
        result.put("msg", "转让成功");
        return result;
    }

    private boolean canManageCategory(User op, Long categoryId) {
        if (op.getRoleId() != null && op.getRoleId() == 2L) {
            return true;
        }
        List<PostAdmin> admins = postAdminMapper.selectByCategory(categoryId);
        for (PostAdmin a : admins) {
            if (a.getUserId() != null && a.getUserId().equals(op.getId()) && "OWNER".equalsIgnoreCase(a.getRole())) {
                return true;
            }
        }
        return false;
    }

    @PostMapping("/follow/toggle")
    public Map<String, Object> toggleFollow(@RequestParam Long categoryId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long uid = getCurrentUserId(request);
        if (uid == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        PostCategory c = postCategoryMapper.selectById(categoryId);
        if (c == null) {
            result.put("code", 404);
            result.put("msg", "板块不存在");
            return result;
        }
        boolean followed;
        if (postCategoryFollowMapper.exists(categoryId, uid) > 0) {
            postCategoryFollowMapper.delete(categoryId, uid);
            followed = false;
        } else {
            postCategoryFollowMapper.insert(categoryId, uid);
            followed = true;
        }
        int memberCount = postCategoryFollowMapper.countByCategory(categoryId);
        c.setMemberCount(memberCount);
        postCategoryMapper.updateById(c);

        result.put("code", 200);
        result.put("followed", followed);
        result.put("memberCount", memberCount);
        return result;
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        User u = getCurrentUser(request);
        return u == null ? null : u.getId();
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
