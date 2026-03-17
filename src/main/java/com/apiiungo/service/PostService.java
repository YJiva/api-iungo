package com.apiiungo.service;

import com.apiiungo.entity.Post;

import java.util.List;

public interface PostService {
    Post createPost(Post post);
    Post getPost(Long id);
    List<Post> listRecent(int offset, int limit);
    boolean likePost(Long id);
    boolean favoritePost(Long id);
}
