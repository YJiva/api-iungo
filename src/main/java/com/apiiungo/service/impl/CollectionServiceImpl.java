package com.apiiungo.service.impl;

import com.apiiungo.entity.Blog;
import com.apiiungo.entity.Collection;
import com.apiiungo.entity.Post;
import com.apiiungo.mapper.BlogMapper;
import com.apiiungo.mapper.CollectionMapper;
import com.apiiungo.mapper.PostMapper;
import com.apiiungo.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private CollectionMapper collectionMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private BlogMapper blogMapper;

    @Override
    public List<Map<String, Object>> listForUser(Long userId) {
        List<Collection> list = collectionMapper.selectByUser(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Collection c : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", c.getId());
            item.put("targetType", c.getTargetType());
            item.put("targetId", c.getTargetId());
            item.put("createTime", c.getCreateTime());
            Long type = c.getTargetType();
            // 约定 target.id: 1=blog,2=post,3=comment
            if (Long.valueOf(2L).equals(type)) {
                Post p = postMapper.selectById(c.getTargetId());
                if (p != null) {
                    item.put("title", p.getTitle());
                }
            } else if (Long.valueOf(1L).equals(type)) {
                Blog b = blogMapper.selectById(c.getTargetId());
                if (b != null) {
                    item.put("title", b.getTitle());
                }
            }
            result.add(item);
        }
        return result;
    }

    @Override
    public boolean toggleCollection(Long userId, Long targetType, Long targetId) {
        int exists = collectionMapper.exists(userId, targetType, targetId);
        if (exists > 0) {
            collectionMapper.delete(userId, targetType, targetId);
            return false;
        }
        Collection c = new Collection();
        c.setUserId(userId);
        c.setTargetType(targetType);
        c.setTargetId(targetId);
        c.setCreateTime(LocalDateTime.now());
        collectionMapper.insert(c);
        return true;
    }

    @Override
    public boolean isCollected(Long userId, Long targetType, Long targetId) {
        return collectionMapper.exists(userId, targetType, targetId) > 0;
    }

    @Override
    public int countCollections(Long targetType, Long targetId) {
        return collectionMapper.countByTarget(targetType, targetId);
    }
}

