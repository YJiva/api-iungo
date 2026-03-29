package com.apiiungo.service;

import com.apiiungo.entity.Blog;

import java.util.List;
import java.util.Map;

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

    Blog getLatestDraft(Long userId);

    int clearDraft(Long userId);

    List<Blog> listCircleBlogs(Long viewerId);

    List<Blog> listCloseFriendBlogs(Long viewerId);

    /**
     * 前台公开博客分页：status=1 且 open_scope=4；置顶(top=1)在同档位内按 read 降序，其余按发布时间降序。
     */
    Map<String, Object> pagePublicBlogs(String keyword, int page, int pageSize);
}
