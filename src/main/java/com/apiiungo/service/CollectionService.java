package com.apiiungo.service;

import java.util.List;
import java.util.Map;

public interface CollectionService {

    /**
     * 返回当前用户的收藏列表，包含目标标题等简要信息。
     */
    List<Map<String, Object>> listForUser(Long userId);
}

