package com.apiiungo.service.impl;

import com.apiiungo.entity.Post;
import com.apiiungo.mapper.PostMapper;
import com.apiiungo.service.PostService;
import com.apiiungo.util.TimestampId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Override
    public Post createPost(Post post) {
        if (post.getId() == null) {
            post.setId(TimestampId.next());
        }
        if (post.getCreateTime() == null) post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        if (post.getLikes() == null) post.setLikes(0);
        if (post.getFavorites() == null) post.setFavorites(0);
        if (post.getComments() == null) post.setComments(0);
        post.setStatus(0);
        postMapper.insertPost(post);
        return post;
    }

    @Override
    public Post getPost(Long id) {
        return postMapper.selectById(id);
    }

    @Override
    public List<Post> listRecent(int offset, int limit) {
        return postMapper.selectRecent(offset, limit);
    }

    @Override
    public List<Post> listByCategory(Long categoryId, int offset, int limit) {
        return postMapper.selectByCategory(categoryId, offset, limit);
    }

    @Override
    public List<Post> listByCategoryWithFilter(Long categoryId, int offset, int limit, String keyword, Integer essenceOnly) {
        return postMapper.selectByCategoryWithFilter(categoryId, offset, limit, keyword, essenceOnly);
    }

    @Override
    public boolean likePost(Long id) {
        if (id == null) {
            return false;
        }
        return postMapper.incLikes(id) > 0;
    }

    @Override
    public boolean unlikePost(Long id) {
        if (id == null) {
            return false;
        }
        return postMapper.decLikes(id) > 0;
    }

    @Override
    public boolean favoritePost(Long id) {
        return postMapper.incFavorites(id) > 0;
    }

    @Override
    public boolean deletePost(Long id) {
        return postMapper.softDeleteById(id) > 0;
    }

    @Override
    public boolean toggleTop(Long id) {
        return postMapper.toggleTop(id) > 0;
    }

    @Override
    public boolean toggleEssence(Long id) {
        return postMapper.toggleEssence(id) > 0;
    }

    @Override
    public boolean decCommentsByPost(Long id, int delta) {
        if (id == null || delta <= 0) return false;
        return postMapper.decCommentsByPost(id, delta) > 0;
    }
}
