package com.apiiungo.service.impl;

import com.apiiungo.entity.Post;
import com.apiiungo.mapper.PostMapper;
import com.apiiungo.service.PostService;
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
    public boolean likePost(Long id) {
        return postMapper.incLikes(id) > 0;
    }

    @Override
    public boolean favoritePost(Long id) {
        return postMapper.incFavorites(id) > 0;
    }
}
