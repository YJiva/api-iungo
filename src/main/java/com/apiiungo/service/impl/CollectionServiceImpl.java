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
            String type = c.getTargetType();
            if ("post".equalsIgnoreCase(type)) {
                Post p = postMapper.selectById(c.getTargetId());
                if (p != null) {
                    item.put("title", p.getTitle());
                }
            } else if ("blog".equalsIgnoreCase(type)) {
                Blog b = blogMapper.selectById(c.getTargetId());
                if (b != null) {
                    item.put("title", b.getTitle());
                }
            }
            result.add(item);
        }
        return result;
    }
}

