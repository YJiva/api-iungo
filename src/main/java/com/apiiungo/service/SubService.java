package com.apiiungo.service;

import com.apiiungo.entity.Sub;

import java.util.List;

public interface SubService {

    void addNotification(Long userId, String message);

    List<Sub> listByUser(Long userId);

    boolean markRead(Long userId, Long id);

    int markAllRead(Long userId);
}

