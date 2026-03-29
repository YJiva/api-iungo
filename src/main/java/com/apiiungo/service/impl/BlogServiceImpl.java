package com.apiiungo.service.impl;

import com.apiiungo.entity.Blog;
import com.apiiungo.mapper.BlogMapper;
import com.apiiungo.service.BlogService;
import com.apiiungo.util.TimestampId;
import com.apiiungo.vo.PublicBlogItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogMapper blogMapper;

    @Override
    public boolean saveBlog(Blog blog) {
        LocalDateTime now = LocalDateTime.now();
        if (blog.getId() == null) {
            blog.setCreateTime(now);
        }
        blog.setUpdateTime(now);
        if (blog.getStatus() == null) {
            // 默认 1 表示已发布
            blog.setStatus(1);
        }
        if (blog.getTop() == null) {
            blog.setTop(0);
        }
        if (blog.getOpenScope() == null) {
            // 默认 4 表示全部可见；0 代表草稿
            blog.setOpenScope(4);
        }
        int affected;
        if (blog.getId() == null) {
            blog.setId(TimestampId.next());
            affected = blogMapper.insertBlog(blog);
        } else {
            affected = blogMapper.updateBlog(blog);
        }
        return affected > 0;
    }

    @Override
    public boolean restoreVersion(Long blogId, Long versionId) {
        // 当前数据库未设计版本表，仅预留接口。
        // 这里先返回 false，后续如增加 blog_version 表再实现真实恢复逻辑。
        return false;
    }

    @Override
    public List<Blog> listByOpenScope(Long userId, Integer scope) {
        // scope 约定：
        // 0：只看自己的博客
        // 1：自己的博客 + 公共博客
        // 2：仅公共博客
        if (scope == null) {
            scope = 1;
        }
        Map<Long, Blog> resultMap = new LinkedHashMap<>();
        if (scope == 0 || scope == 1) {
            List<Blog> own = blogMapper.selectByUser(userId);
            for (Blog b : own) {
                // status=1 才允许前台展示（软删除/隐藏不展示）
                if (b != null && b.getStatus() != null && b.getStatus() == 1) {
                    resultMap.put(b.getId(), b);
                }
            }
        }
        if (scope == 1 || scope == 2) {
            List<Blog> pub = blogMapper.selectPublic();
            for (Blog b : pub) {
                // selectPublic 已经限定 status=1
                if (b != null && b.getId() != null) {
                    resultMap.putIfAbsent(b.getId(), b);
                }
            }
        }
        return new ArrayList<>(resultMap.values());
    }

    @Override
    public List<String> listAllTags() {
        return blogMapper.selectAllTags();
    }

    @Override
    public Blog getDetailAndIncRead(Long id) {
        if (id == null) {
            return null;
        }
        Blog blog = blogMapper.selectById(id);
        if (blog == null) {
            return null;
        }
        // status=0 表示已软删除/不在前台展示
        if (blog.getStatus() == null || blog.getStatus() != 1) {
            return null;
        }
        Long currentRead = blog.getRead();
        if (currentRead == null) {
            currentRead = 0L;
        }
        blog.setRead(currentRead + 1);
        // 简单直接更新整行（包含最新的阅读数）
        blogMapper.updateBlog(blog);
        return blog;
    }

    @Override
    public List<Blog> listPublicByUser(Long userId, int page, int pageSize) {
        List<Blog> all = blogMapper.selectPublicByUser(userId);
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(pageSize, 1);
        int from = (safePage - 1) * safeSize;
        if (from >= all.size()) {
            return new ArrayList<>();
        }
        int to = Math.min(from + safeSize, all.size());
        return all.subList(from, to);
    }

    @Override
    public long countPublicByUser(Long userId) {
        List<Blog> all = blogMapper.selectPublicByUser(userId);
        return all == null ? 0 : all.size();
    }

    @Override
    public Blog getLatestDraft(Long userId) {
        if (userId == null) {
            return null;
        }
        return blogMapper.selectLatestDraftByUser(userId);
    }

    @Override
    public int clearDraft(Long userId) {
        if (userId == null) {
            return 0;
        }
        return blogMapper.clearDraftByUser(userId);
    }

    @Override
    public List<Blog> listCircleBlogs(Long viewerId) {
        if (viewerId == null) {
            return new ArrayList<>();
        }
        return blogMapper.selectCircleBlogs(viewerId);
    }

    @Override
    public List<Blog> listCloseFriendBlogs(Long viewerId) {
        if (viewerId == null) {
            return new ArrayList<>();
        }
        return blogMapper.selectCloseFriendBlogs(viewerId);
    }

    @Override
    public Map<String, Object> pagePublicBlogs(String keyword, int page, int pageSize) {
        String kw = keyword == null ? "" : keyword.trim();
        String pattern = kw.isEmpty() ? null : "%" + kw + "%";
        int p = Math.max(page, 1);
        int size = Math.min(Math.max(pageSize, 1), 2000);
        int offset = (p - 1) * size;
        long total = blogMapper.countPublicBlogItems(pattern);
        List<PublicBlogItem> list = blogMapper.selectPublicBlogItems(pattern, offset, size);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        data.put("page", p);
        data.put("pageSize", size);
        data.put("hasMore", (long) p * size < total);
        return data;
    }
}
