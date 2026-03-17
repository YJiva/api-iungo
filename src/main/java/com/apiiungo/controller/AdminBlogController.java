package com.apiiungo.controller;

import com.apiiungo.entity.Blog;
import com.apiiungo.entity.BlogType;
import com.apiiungo.entity.User;
import com.apiiungo.mapper.BlogMapper;
import com.apiiungo.mapper.BlogTypeMapper;
import com.apiiungo.service.UserService;
import com.apiiungo.utils.Md5Util;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin/blog")
public class AdminBlogController {

    @Autowired
    private BlogMapper blogMapper;
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
    public Map<String, Object> list(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User admin = getCurrentUser(request);
        if (!isAdmin(admin)) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        List<Blog> blogs = blogMapper.selectAll();
        result.put("code", 200);
        result.put("data", blogs);
        return result;
    }

    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User admin = getCurrentUser(request);
        if (!isAdmin(admin)) {
        result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }

        LocalDateTime now = LocalDateTime.now();

        Blog blog = new Blog();
        Object idObj = body.get("id");
        if (idObj != null) {
            blog.setId(Long.valueOf(idObj.toString()));
        }
        Object userIdObj = body.get("userId");
        if (userIdObj != null) {
            blog.setUserId(Long.valueOf(userIdObj.toString()));
        }
        blog.setTitle(Objects.toString(body.get("title"), ""));
        blog.setContent(Objects.toString(body.get("content"), ""));

        Object statusObj = body.get("status");
        blog.setStatus(statusObj == null ? 1 : Integer.valueOf(statusObj.toString()));
        Object topObj = body.get("top");
        blog.setTop(topObj == null ? 0 : Integer.valueOf(topObj.toString()));
        Object scopeObj = body.get("openScope");
        blog.setOpenScope(scopeObj == null ? 2 : Integer.valueOf(scopeObj.toString()));

        // 处理标签名 -> blog_type，同步并生成 tags 中的 id 列表
        List<?> tagNameList = (List<?>) body.get("tagNames");
        List<Integer> typeIds = new ArrayList<>();
        if (tagNameList != null) {
            for (Object o : tagNameList) {
                if (o == null) continue;
                String name = o.toString().trim();
                if (name.isEmpty()) continue;
                BlogType type = findOrCreateBlogTypeByName(name);
                if (type != null && type.getId() != null) {
                    typeIds.add(type.getId());
                }
            }
        }
        if (!typeIds.isEmpty()) {
            String idsStr = joinIds(typeIds);
            blog.setTags(idsStr);
        } else {
            blog.setTags(null);
        }

        int n;
        if (blog.getId() == null) {
            blog.setCreateTime(now);
            blog.setUpdateTime(now);
            n = blogMapper.insertBlog(blog);
        } else {
            blog.setUpdateTime(now);
            n = blogMapper.updateBlog(blog);
        }

        result.put("code", n > 0 ? 200 : 400);
        result.put("msg", n > 0 ? "保存成功" : "保存失败");
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
        int n = blogMapper.deleteById(id);
        result.put("code", n > 0 ? 200 : 400);
        result.put("msg", n > 0 ? "删除成功" : "删除失败");
        return result;
    }

    private BlogType findOrCreateBlogTypeByName(String name) {
        QueryWrapper<BlogType> qw = new QueryWrapper<>();
        qw.eq("name", name).last("LIMIT 1");
        BlogType existing = blogTypeMapper.selectOne(qw);
        if (existing != null) {
            return existing;
        }
        BlogType type = new BlogType();
        type.setName(name);
        type.setShow(1);
        type.setDescription("");
        blogTypeMapper.insert(type);
        return type;
    }

    private String joinIds(List<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(ids.get(i));
        }
        return sb.toString();
    }
}

