package com.apiiungo.service.impl;

import com.apiiungo.entity.Sub;
import com.apiiungo.mapper.SubMapper;
import com.apiiungo.service.SubService;
import com.apiiungo.util.TimestampId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubServiceImpl implements SubService {

    @Autowired
    private SubMapper subMapper;

    @Override
    public void addNotification(Long userId, String message, String type, String sourceTitle, String sourceCategory, String jumpUrl) {
        if (userId == null || message == null || message.trim().isEmpty()) {
            return;
        }
        Sub sub = new Sub();
        sub.setId(TimestampId.next());
        sub.setUserId(userId);
        sub.setMessage(message);
        sub.setType(type == null || type.trim().isEmpty() ? "SYSTEM" : type.trim());
        sub.setSourceTitle(sourceTitle);
        sub.setSourceCategory(sourceCategory);
        sub.setJumpUrl(jumpUrl);
        sub.setRead(0);
        sub.setCreateTime(LocalDateTime.now());
        subMapper.insert(sub);
    }

    @Override
    public List<Sub> listByUser(Long userId) {
        return subMapper.selectByUser(userId);
    }

    @Override
    public boolean markRead(Long userId, Long id) {
        return subMapper.markRead(id, userId) > 0;
    }

    @Override
    public int markAllRead(Long userId) {
        return subMapper.markAllRead(userId);
    }

    @Override
    public boolean deleteById(Long userId, Long id) {
        return subMapper.deleteById(id, userId) > 0;
    }

    @Override
    public int deleteAll(Long userId) {
        return subMapper.deleteAll(userId);
    }

    @Override
    public int deleteRead(Long userId) {
        return subMapper.deleteRead(userId);
    }
}

