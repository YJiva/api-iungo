package com.apiiungo.service;

import com.apiiungo.entity.Blog;

import java.util.List;

public interface BlogService {
    boolean saveBlog(Blog blog);
    boolean restoreVersion(Long blogId, Long versionId);
    List<Blog> listByOpenScope(Long userId, Integer scope);
    List<String> listAllTags();

    /**
     * 获取博客详情并自增阅读次数
     */
    Blog getDetailAndIncRead(Long id);

    /**
     * 公开博客分页
     */
    List<Blog> listPublicByUser(Long userId, int page, int pageSize);

    long countPublicByUser(Long userId);
}
