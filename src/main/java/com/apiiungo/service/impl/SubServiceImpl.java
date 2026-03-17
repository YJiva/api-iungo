package com.apiiungo.service.impl;

import com.apiiungo.entity.Sub;
import com.apiiungo.mapper.SubMapper;
import com.apiiungo.service.SubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubServiceImpl implements SubService {

    @Autowired
    private SubMapper subMapper;

    @Override
    public void addNotification(Long userId, String message) {
        if (userId == null || message == null || message.trim().isEmpty()) {
            return;
        }
        Sub sub = new Sub();
        sub.setUserId(userId);
        sub.setMessage(message);
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
}

