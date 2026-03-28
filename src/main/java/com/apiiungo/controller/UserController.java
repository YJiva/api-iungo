package com.apiiungo.controller;

import com.apiiungo.entity.Role;
import com.apiiungo.entity.User;
import com.apiiungo.mapper.RoleMapper;
import com.apiiungo.service.UserService;
import com.apiiungo.service.FollowService;
import com.apiiungo.utils.EmailUtil;
import com.apiiungo.utils.EmailCodeStore;
import com.apiiungo.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private EmailCodeStore emailCodeStore;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private FollowService followService;

    // 发送邮箱验证码（注册/登录）
    @PostMapping("/send-email-code")
    public Map<String, Object> sendEmailCode(@RequestParam String email) {
        Map<String, Object> result = new HashMap<>();
        // 生成6位验证码
        String code = RandomUtil.generateCode(6);
        // 发送邮件
        boolean send = emailUtil.sendEmail(email, "iungo验证码", "你的验证码是：" + code + "，5分钟内有效");
        if (send) {
            // 存入内存验证码存储，5分钟过期
            emailCodeStore.setCode(email, code, 5);
            result.put("code", 200);
            result.put("msg", "验证码发送成功");
        } else {
            result.put("code", 500);
            result.put("msg", "验证码发送失败");
        }
        return result;
    }

    // 账号密码登录
    @PostMapping("/login/password")
    public Map<String, Object> loginByPassword(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String username = params.get("username");
        String password = params.get("password");

        // 先检查账号是否存在及状态，封禁用户禁止登录
        User base = username == null ? null : userService.findByUsername(username);
        if (base != null && Integer.valueOf(0).equals(base.getStatus())) {
            result.put("code", 403);
            result.put("msg", "该账号已被封禁，无法登录");
            return result;
        }

        User user = userService.loginByPassword(username, password);
        if (user != null) {
            // 简易开发用 token（格式 dev-token:<username>），生产请替换为 JWT
            String token = "dev-token:" + user.getUsername();
            result.put("code", 200);
            result.put("msg", "登录成功");
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            result.put("data", data);
        } else {
            result.put("code", 400);
            result.put("msg", "账号或密码错误");
        }
        return result;
    }

    // 邮箱验证码登录
    @PostMapping("/login/email")
    public Map<String, Object> loginByEmail(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.loginByEmailCode(params.get("email"), params.get("code"));
        if (user != null) {
            if (user.getStatus() != null && user.getStatus() == 0) {
                result.put("code", 403);
                result.put("msg", "该账号已被封禁，无法登录");
                return result;
            }
            String token = "dev-token:" + user.getUsername();
            result.put("code", 200);
            result.put("msg", "登录成功");
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            result.put("data", data);
        } else {
            result.put("code", 400);
            result.put("msg", "邮箱或验证码错误");
        }
        return result;
    }

    // 获取当前登录用户信息（从 Authorization: Bearer dev-token:<username> 解析）
    @GetMapping("/me")
    public Map<String, Object> me(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User u = getUserFromRequest(request);
        if (u == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
        } else if (Integer.valueOf(0).equals(u.getStatus())) {
            result.put("code", 403);
            result.put("msg", "该账号已被封禁，无法登录");
        } else {
            result.put("code", 200);
            result.put("data", u);
        }
        return result;
    }

    // 当前用户角色信息（名称 + 描述）
    @GetMapping("/role-info")
    public Map<String, Object> roleInfo(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User u = getUserFromRequest(request);
        if (u == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        if (Integer.valueOf(0).equals(u.getStatus())) {
            result.put("code", 403);
            result.put("msg", "该账号已被封禁，无法登录");
            return result;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("roleId", u.getRoleId());
        if (u.getRoleId() != null) {
            Role role = roleMapper.selectById(u.getRoleId());
            if (role != null) {
                data.put("name", role.getName());
                data.put("description", role.getDescription());
            }
        }
        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    // 更新当前用户资料（需携带 token）
    @PostMapping("/update")
    public Map<String, Object> updateProfile(@RequestBody User user, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User u = getUserFromRequest(request);
        if (u == null || !u.getId().equals(user.getId())) {
            result.put("code", 401);
            result.put("msg", "无权限");
            return result;
        }
        // 普通用户资料更新不允许修改 roleId
        user.setRoleId(null);
        boolean ok = userService.updateProfile(user);
        if (ok) {
            result.put("code", 200);
            result.put("msg", "更新成功");
        } else {
            result.put("code", 400);
            result.put("msg", "更新失败");
        }
        return result;
    }

    // 修改密码（需携带 token）
    @PostMapping("/change-password")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        User u = getUserFromRequest(request);
        if (u == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        String oldPwd = params.get("oldPassword");
        String newPwd = params.get("newPassword");
        boolean ok = userService.updatePassword(u.getId(), oldPwd, newPwd);
        if (ok) {
            result.put("code", 200);
            result.put("msg", "修改成功");
        } else {
            result.put("code", 400);
            result.put("msg", "旧密码错误或修改失败");
        }
        return result;
    }

    private User getUserFromRequest(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        String username = extractUsernameFromToken(auth);
        if (username == null || username.isEmpty()) {
            return null;
        }
        return userService.findByUsername(username);
    }

    /**
     * 从 Authorization 头或裸 token 中解析用户名。
     * 支持：
     * - Authorization: Bearer dev-token:<username>
     * - Authorization: dev-token:<username>
     * - 直接传入 dev-token:<username>
     */
    private String extractUsernameFromToken(String authHeader) {
        if (authHeader == null) {
            return null;
        }
        String auth = authHeader.trim();
        if (auth.isEmpty()) {
            return null;
        }
        String lower = auth.toLowerCase();
        if (lower.startsWith("bearer ")) {
            auth = auth.substring(7).trim();
        }
        if (!auth.startsWith("dev-token:")) {
            return null;
        }
        return auth.substring("dev-token:".length());
    }

    // 邀请注册
    @PostMapping("/register")
    public Map<String, Object> inviteRegister(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        User user = new User();
        user.setUsername(params.get("username").toString());
        user.setPassword(params.get("password").toString());
        user.setEmail(params.get("email").toString());
        user.setNickname(params.get("nickname").toString());
        // 性别（0=未知,1=男,2=女），前端可选传
        Object genderObj = params.get("gender");
        if (genderObj != null) {
            try {
                user.setGender(Integer.parseInt(genderObj.toString()));
            } catch (NumberFormatException ignored) {
                user.setGender(0);
            }
        } else {
            user.setGender(0);
        }

        Object emailCodeObj = params.get("emailCode");
        if (emailCodeObj == null) {
            result.put("code", 400);
            result.put("msg", "邮箱验证码不能为空");
            return result;
        }
        String emailCode = emailCodeObj.toString();
        // 校验邮箱验证码（从内存存储中读取）
        String realCode = emailCodeStore.getCode(user.getEmail());
        if (realCode == null || !realCode.equals(emailCode)) {
            result.put("code", 400);
            result.put("msg", "邮箱验证码错误或已过期");
            return result;
        }
        // 验证通过后删除验证码
        emailCodeStore.deleteCode(user.getEmail());
        // 校验用户名是否已存在
        if (userService.findByUsername(user.getUsername()) != null) {
            result.put("code", 400);
            result.put("msg", "用户名已存在");
            return result;
        }
        // 校验邮箱是否已被使用
        if (userService.findByEmail(user.getEmail()) != null) {
            result.put("code", 400);
            result.put("msg", "邮箱已被注册");
            return result;
        }
        String inviteCode = params.get("inviteCode").toString();
        boolean success = userService.inviteRegister(user, inviteCode);
        if (success) {
            result.put("code", 200);
            result.put("msg", "注册成功");
        } else {
            result.put("code", 400);
            result.put("msg", "邀请码无效或注册失败");
        }
        return result;
    }

    // 查询邀请树
    @GetMapping("/invite-tree")
    public Map<String, Object> getInviteTree(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> tree = userService.getInviteTree(userId);
        result.put("code", 200);
        Map<String, Object> data = new HashMap<>();
        data.put("tree", tree);
        result.put("data", data);
        return result;
    }

    // 更近关系的邀请树（只看与当前用户直接相关的关系）
    @GetMapping("/invite-tree/close")
    public Map<String, Object> getInviteTreeClose(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> tree = userService.getInviteTreeClose(userId);
        result.put("code", 200);
        Map<String, Object> data = new HashMap<>();
        data.put("tree", tree);
        result.put("data", data);
        return result;
    }

    // 生成邀请码 / 获取固定邀请码
    @PostMapping("/generate-invite-code")
    public Map<String, Object> generateInviteCode(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        String code = userService.generateInviteCode(userId);
        result.put("code", 200);
        Map<String, Object> data = new HashMap<>();
        data.put("code", code);
        result.put("data", data);
        return result;
    }

    // 公开主页资料（用于他人查看）
    @GetMapping("/public-profile")
    public Map<String, Object> publicProfile(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        User u = userService.findById(userId);
        if (u == null) {
            result.put("code", 404);
            result.put("msg", "用户不存在");
            return result;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", u.getId());
        data.put("username", u.getUsername());
        data.put("nickname", u.getNickname());
        data.put("avatar", u.getAvatar());
        data.put("coverUrl", u.getCoverUrl());
        data.put("bio", u.getBio());
        data.put("gender", u.getGender());
        data.put("roleId", u.getRoleId());
        data.put("roleName", u.getRoleName());
        data.put("createTime", u.getCreateTime());

        // 可公开查看的信息：关注数、粉丝数
        List<User> following = followService.listFollowing(userId);
        List<User> followers = followService.listFollowers(userId);
        data.put("followingCount", following == null ? 0 : following.size());
        data.put("followersCount", followers == null ? 0 : followers.size());

        result.put("code", 200);
        result.put("data", data);
        return result;
    }
}