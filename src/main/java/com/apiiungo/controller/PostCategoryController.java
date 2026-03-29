package com.apiiungo.controller;

import com.apiiungo.entity.PostAdmin;
import com.apiiungo.entity.PostAdminView;
import com.apiiungo.entity.PostCategory;
import com.apiiungo.entity.PostRole;
import com.apiiungo.entity.User;
import com.apiiungo.mapper.PostAdminMapper;
import com.apiiungo.mapper.PostCategoryFollowMapper;
import com.apiiungo.mapper.PostCategoryMapper;
import com.apiiungo.mapper.PostMapper;
import com.apiiungo.mapper.PostRoleMapper;
import com.apiiungo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

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
    private PostRoleMapper postRoleMapper;
    @Autowired
    private PostMapper postMapper;

    @GetMapping("/list")
    public Map<String, Object> list(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        List<PostCategory> list = postCategoryMapper.selectList(null);
        Long uid = getCurrentUserId(request);
        List<Map<String, Object>> data = new ArrayList<>();
        for (PostCategory c : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", c.getId());
            item.put("name", c.getName());
            item.put("description", c.getDescription());
            item.put("icon", c.getIcon());
            item.put("coverUrl", c.getCoverUrl());
            item.put("status", c.getStatus());
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
        List<PostAdminView> admins = postAdminMapper.selectViewByCategory(id);
        Map<String, Object> data = new HashMap<>();
        data.put("category", c);
        data.put("admins", admins);
        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    @GetMapping("/candidate-users")
    public Map<String, Object> candidateUsers(@RequestParam(defaultValue = "") String keyword,
                                               @RequestParam(defaultValue = "20") Integer limit,
                                               HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User op = getCurrentUser(request);
        if (op == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        int l = Math.max(1, Math.min(limit == null ? 20 : limit, 50));
        result.put("code", 200);
        result.put("data", postAdminMapper.selectCandidateUsers(keyword == null ? "" : keyword.trim(), l));
        return result;
    }

    @GetMapping("/role/list")
    public Map<String, Object> roleList() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", postRoleMapper.selectAllActive());
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
        if (input.getShow() == null) input.setShow(1);
        input.setCreateTime(LocalDateTime.now());
        input.setUpdateTime(LocalDateTime.now());
        postCategoryMapper.insert(input);

        PostRole ownerRole = getRoleByCode("OWNER");
        PostAdmin owner = new PostAdmin();
        owner.setCategoryId(input.getId().longValue());
        owner.setUserId(u.getId());
        owner.setRole("OWNER");
        owner.setRoleId(ownerRole == null ? null : ownerRole.getId());
        owner.setStatus(1);
        owner.setCreateTime(LocalDateTime.now());
        owner.setUpdateTime(LocalDateTime.now());
        postAdminMapper.insert(owner);

        result.put("code", 200);
        result.put("msg", "创建成功");
        result.put("data", input);
        return result;
    }

    @PostMapping("/update")
    public Map<String, Object> updateCategory(@RequestBody PostCategory input, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User op = getCurrentUser(request);
        if (op == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        if (input == null || input.getId() == null) {
            result.put("code", 400);
            result.put("msg", "id 不能为空");
            return result;
        }
        if (!canManageCategory(op, input.getId().longValue())) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        PostCategory exists = postCategoryMapper.selectById(input.getId());
        if (exists == null) {
            result.put("code", 404);
            result.put("msg", "板块不存在");
            return result;
        }

        if (input.getName() != null) exists.setName(input.getName().trim());
        if (input.getDescription() != null) exists.setDescription(input.getDescription().trim());
        if (input.getIcon() != null) exists.setIcon(input.getIcon());
        if (input.getCoverUrl() != null) exists.setCoverUrl(input.getCoverUrl());
        if (input.getStatus() != null) exists.setStatus(input.getStatus());
        exists.setUpdateTime(LocalDateTime.now());
        postCategoryMapper.updateById(exists);

        result.put("code", 200);
        result.put("msg", "更新成功");
        result.put("data", exists);
        return result;
    }

    @PostMapping("/admin/add")
    public Map<String, Object> addAdmin(@RequestParam Long categoryId,
                                        @RequestParam Long userId,
                                        @RequestParam(required = false) Long roleId,
                                        @RequestParam(required = false) String role,
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
        User targetUser = userService.findById(userId);
        if (targetUser == null) {
            result.put("code", 404);
            result.put("msg", "目标用户不存在");
            return result;
        }

        PostRole roleObj = resolveRole(roleId, role);
        if (roleObj == null) {
            result.put("code", 400);
            result.put("msg", "角色无效");
            return result;
        }

        List<PostAdminView> admins = postAdminMapper.selectViewByCategory(categoryId);
        for (PostAdminView a : admins) {
            if (a.getUserId() != null && a.getUserId().equals(userId)) {
                if (Objects.equals(a.getRoleId(), roleObj.getId())) {
                    result.put("code", 400);
                    result.put("msg", "该用户已是该职务");
                    return result;
                }
                break;
            }
        }

        if ("OWNER".equalsIgnoreCase(roleObj.getCode())) {
            disableAllOwners(categoryId);
        }

        int updated = postAdminMapper.updateRole(categoryId, userId, roleObj.getCode(), roleObj.getId());
        if (updated == 0) {
            int reactivated = postAdminMapper.reactivate(categoryId, userId, roleObj.getCode(), roleObj.getId());
            if (reactivated == 0) {
                PostAdmin pa = new PostAdmin();
                pa.setCategoryId(categoryId);
                pa.setUserId(userId);
                pa.setRole(roleObj.getCode());
                pa.setRoleId(roleObj.getId());
                pa.setStatus(1);
                pa.setCreateTime(LocalDateTime.now());
                pa.setUpdateTime(LocalDateTime.now());
                postAdminMapper.insert(pa);
            }
        }

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
        List<PostAdminView> admins = postAdminMapper.selectViewByCategory(categoryId);
        for (PostAdminView a : admins) {
            if (a.getUserId() != null && a.getUserId().equals(userId) && "OWNER".equalsIgnoreCase(a.getRoleCode())) {
                result.put("code", 400);
                result.put("msg", "不能直接移除吧主，请先转让");
                return result;
            }
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

        User target = userService.findById(toUserId);
        if (target == null) {
            result.put("code", 404);
            result.put("msg", "目标用户不存在");
            return result;
        }

        PostRole ownerRole = getRoleByCode("OWNER");
        if (ownerRole == null) {
            result.put("code", 500);
            result.put("msg", "OWNER 角色未配置");
            return result;
        }

        disableAllOwners(categoryId);

        int updated = postAdminMapper.updateRole(categoryId, toUserId, ownerRole.getCode(), ownerRole.getId());
        if (updated == 0) {
            int reactivated = postAdminMapper.reactivate(categoryId, toUserId, ownerRole.getCode(), ownerRole.getId());
            if (reactivated == 0) {
                PostAdmin owner = new PostAdmin();
                owner.setCategoryId(categoryId);
                owner.setUserId(toUserId);
                owner.setRole(ownerRole.getCode());
                owner.setRoleId(ownerRole.getId());
                owner.setStatus(1);
                owner.setCreateTime(LocalDateTime.now());
                owner.setUpdateTime(LocalDateTime.now());
                postAdminMapper.insert(owner);
            }
        }

        result.put("code", 200);
        result.put("msg", "转让成功");
        return result;
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

    private PostRole resolveRole(Long roleId, String roleCode) {
        if (roleId != null) {
            PostRole byId = postRoleMapper.selectById(roleId);
            if (byId != null && byId.getStatus() != null && byId.getStatus() == 1) return byId;
        }
        String code = (roleCode == null || roleCode.trim().isEmpty()) ? "MODERATOR" : roleCode.trim().toUpperCase();
        PostRole byCode = postRoleMapper.selectByCode(code);
        if (byCode != null && byCode.getStatus() != null && byCode.getStatus() == 1) return byCode;
        return null;
    }

    private PostRole getRoleByCode(String code) {
        PostRole role = postRoleMapper.selectByCode(code);
        if (role != null && role.getStatus() != null && role.getStatus() == 1) return role;
        return null;
    }

    private void disableAllOwners(Long categoryId) {
        List<PostAdminView> admins = postAdminMapper.selectViewByCategory(categoryId);
        for (PostAdminView a : admins) {
            if ("OWNER".equalsIgnoreCase(a.getRoleCode())) {
                postAdminMapper.disable(categoryId, a.getUserId());
            }
        }
    }

    private boolean canManageCategory(User op, Long categoryId) {
        if (op.getRoleId() != null && op.getRoleId() == 2L) {
            return true;
        }
        List<PostAdminView> admins = postAdminMapper.selectViewByCategory(categoryId);
        for (PostAdminView a : admins) {
            if (a.getUserId() != null && a.getUserId().equals(op.getId()) && "OWNER".equalsIgnoreCase(a.getRoleCode())) {
                return true;
            }
        }
        return false;
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        User u = getCurrentUser(request);
        return u == null ? null : u.getId();
    }

    private User getCurrentUser(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null) return null;
        String token = auth.trim();
        if (token.isEmpty()) return null;
        String lower = token.toLowerCase();
        if (lower.startsWith("bearer ")) token = token.substring(7).trim();
        if (!token.startsWith("dev-token:")) return null;
        String username = token.substring("dev-token:".length());
        return userService.findByUsername(username);
    }
}
