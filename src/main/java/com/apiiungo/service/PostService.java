package com.apiiungo.service;

import com.apiiungo.entity.Post;

import java.util.List;

public interface PostService {
    Post createPost(Post post);
    Post getPost(Long id);
    List<Post> listRecent(int offset, int limit);
    List<Post> listByCategory(Long categoryId, int offset, int limit);
    List<Post> listByCategoryWithFilter(Long categoryId, int offset, int limit, String keyword, Integer essenceOnly);
    boolean likePost(Long id);

    /** 与取消点赞同步：post.likes - 1（下限 0） */
    boolean unlikePost(Long id);

    boolean favoritePost(Long id);
    boolean deletePost(Long id);
    boolean toggleTop(Long id);
    boolean toggleEssence(Long id);
    boolean decCommentsByPost(Long id, int delta);
}
