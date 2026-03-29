package com.apiiungo.service;

import com.apiiungo.entity.Sub;

import java.util.List;

public interface SubService {

    void addNotification(Long userId, String message, String type, String sourceTitle, String sourceCategory, String jumpUrl);

    List<Sub> listByUser(Long userId);

    boolean markRead(Long userId, Long id);

    int markAllRead(Long userId);

    boolean deleteById(Long userId, Long id);

    int deleteAll(Long userId);

    int deleteRead(Long userId);
}

