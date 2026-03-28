package com.apiiungo.service.impl;

import com.apiiungo.entity.InviteCode;
import com.apiiungo.entity.InviteRelation;
import com.apiiungo.entity.User;
import com.apiiungo.mapper.InviteCodeMapper;
import com.apiiungo.mapper.InviteRelationMapper;
import com.apiiungo.mapper.UserMapper;
import com.apiiungo.service.UserService;
import com.apiiungo.utils.Md5Util;
import com.apiiungo.utils.EmailCodeStore;
import com.apiiungo.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private InviteCodeMapper inviteCodeMapper;
    @Autowired
    private EmailCodeStore emailCodeStore;
    @Autowired
    private InviteRelationMapper inviteRelationMapper;

    // 账号密码登录
    @Override
    public User loginByPassword(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null || !Md5Util.md5(password).equals(user.getPassword())) {
            return null;
        }
        // status=0 表示封禁，禁止登录
        if (user.getStatus() != null && user.getStatus() == 0) {
            return null;
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    // 邮箱验证码登录
    @Override
    public User loginByEmailCode(String email, String code) {
        // 校验内存中的验证码
        String realCode = emailCodeStore.getCode(email);
        if (realCode == null || !realCode.equals(code)) {
            return null;
        }
        // 验证通过，删除验证码
        emailCodeStore.deleteCode(email);
        User user = userMapper.selectByEmail(email);
        // status=0 表示封禁，禁止登录
        if (user != null && user.getStatus() != null && user.getStatus() == 0) {
            return null;
        }
        return user;
    }

    // 邀请注册（事务保证原子性）
    @Override
    @Transactional
    public boolean inviteRegister(User user, String inviteCode) {
        LocalDateTime now = LocalDateTime.now();
        // 1. 校验邀请码：在 invite_code 表中存在且未过期（每个用户固定一个邀请码）
        InviteCode inviterCode = inviteCodeMapper.selectByCode(inviteCode);
        if (inviterCode == null) {
            return false;
        }
        if (inviterCode.getExpireTime() != null && inviterCode.getExpireTime().isBefore(now)) {
            return false;
        }
        Long inviterId = inviterCode.getUserId();
        if (inviterId == null) {
            return false;
        }
        // 2. 邀请人必须存在
        User inviter = userMapper.selectById(inviterId);
        if (inviter == null) {
            return false;
        }
        // 3. 加密密码 + 为新用户生成固定邀请码（同时写入 user 表和 invite_code 表，保持一致）
        user.setPassword(Md5Util.md5(user.getPassword()));
        String newUserCode = RandomUtil.generateInviteCode(8);
        user.setInviteCode(newUserCode);
        user.setRoleId(1L);
        user.setStatus(1);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        // 4. 插入新用户
        int insert = userMapper.insertUser(user);
        if (insert <= 0) {
            return false;
        }

        // 5. 为新用户写入固定邀请码记录 invite_code（userid -> code）
        InviteCode newInviteCode = new InviteCode();
        newInviteCode.setUserId(user.getId());
        newInviteCode.setCode(newUserCode);
        newInviteCode.setCreateTime(now);
        // 固定邀请码默认不过期（expire_time 置空）
        newInviteCode.setExpireTime(null);
        inviteCodeMapper.insert(newInviteCode);

        // 6. 写入邀请关系 invite_relation
        InviteRelation relation = new InviteRelation();
        relation.setInviterId(inviterId);
        relation.setInviteeId(user.getId());
        relation.setRewarded(1); // 按需求默认插入 1
        relation.setCreateTime(now); // create_time 自动生成：这里直接使用 now
        inviteRelationMapper.insert(relation);

        return true;
    }

    // 生成邀请码（每个老用户可生成多个）
    @Override
    public String generateInviteCode(Long userId) {
        // 每个用户固定一个邀请码：已存在则直接返回，不存在则创建
        InviteCode existing = inviteCodeMapper.selectByUserId(userId);
        if (existing != null && existing.getCode() != null && !existing.getCode().isEmpty()) {
            return existing.getCode();
        }
        String code = RandomUtil.generateInviteCode(8);
        InviteCode inviteCode = new InviteCode();
        inviteCode.setUserId(userId);
        inviteCode.setCode(code);
        inviteCode.setCreateTime(LocalDateTime.now());
        inviteCode.setExpireTime(null);
        inviteCodeMapper.insert(inviteCode);
        return code;
    }

    // 查询邀请树
    @Override
    public List<Map<String, Object>> getInviteTree(Long userId) {
        // 全量树：按照 inviter 分组，每个 inviter 只出现一次，children 为其邀请的用户
        List<Map<String, Object>> rows = inviteRelationMapper.selectAllWithUsers();
        return buildTreeFromRows(rows);
    }

    @Override
    public List<Map<String, Object>> getInviteTreeClose(Long userId) {
        // 仅与当前用户直接相关的邀请关系（作为邀请人或被邀请人）
        List<Map<String, Object>> rows = inviteRelationMapper.selectCloseWithUsers(userId);
        return buildTreeFromRows(rows);
    }

    private List<Map<String, Object>> buildTreeFromRows(List<Map<String, Object>> rows) {
        Map<Long, Map<String, Object>> inviterMap = new LinkedHashMap<>();
        for (Map<String, Object> r : rows) {
            Long inviterId = ((Number) r.get("inviter_id")).longValue();
            Long inviteeId = ((Number) r.get("invitee_id")).longValue();
            String inviterName = Objects.toString(r.get("inviter_username"), "");
            String inviteeName = Objects.toString(r.get("invitee_username"), "");
            String inviterNickName=  Objects.toString(r.get("inviter_nickname"), "");
            String inviteeNickName = Objects.toString(r.get("invitee_nickname"), "");
            String inviterAvatar = Objects.toString(r.get("inviter_avatar"), "");
            String inviteeAvatar = Objects.toString(r.get("invitee_avatar"), "");

            Map<String, Object> inviterNode = inviterMap.computeIfAbsent(inviterId, id -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", id);
                m.put("username", inviterName);
                m.put("avatar", inviterAvatar);
                m.put("nickname", inviterNickName);
                m.put("children", new ArrayList<Map<String, Object>>());
                return m;
            });

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> children = (List<Map<String, Object>>) inviterNode.get("children");
            boolean exists = children.stream().anyMatch(c -> Objects.equals(c.get("id"), inviteeId));
            if (!exists) {
                Map<String, Object> child = new HashMap<>();
                child.put("id", inviteeId);
                child.put("username", inviteeName);
                child.put("nickname", inviteeNickName);
                child.put("avatar", inviteeAvatar);
                children.add(child);
            }
        }
        return new ArrayList<>(inviterMap.values());
    }

    // 修改密码
    @Override
    public boolean updatePassword(Long userId, String oldPwd, String newPwd) {
        // 先查询用户
        User user = userMapper.selectById(userId);
        if (user == null || !Md5Util.md5(oldPwd).equals(user.getPassword())) {
            return false;
        }
        // 更新密码
        user.setPassword(Md5Util.md5(newPwd));
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateUser(user) > 0;
    }

    // 编辑个人资料
    @Override
    public boolean updateProfile(User user) {
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateUser(user) > 0;
    }
}