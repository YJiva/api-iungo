package com.apiiungo.config;

import com.apiiungo.entity.User;
import com.apiiungo.entity.Post;
import com.apiiungo.entity.Comment;
import com.apiiungo.entity.Favorite;
import com.apiiungo.mapper.UserMapper;
import com.apiiungo.mapper.PostMapper;
import com.apiiungo.mapper.CommentMapper;
import com.apiiungo.mapper.FavoriteMapper;
import com.apiiungo.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TestDataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public void run(String... args) throws Exception {
        // 为开发环境创建一个默认测试用户（如果不存在）
        String testUsername = "test";

        // 使用您自定义的 selectByUsername 方法
        if (userMapper.selectByUsername(testUsername) == null) {
            User user = new User();
            user.setUsername(testUsername);
            user.setPassword(Md5Util.md5("123456"));
            user.setEmail("test@example.com");
            user.setNickname("Test User");
            user.setRoleId(1L);
            user.setStatus(1);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());

            // 使用您自定义的 insertUser 方法
            userMapper.insertUser(user);
            System.out.println("[TestDataInitializer] created test user: test / 123456");
        } else {
            System.out.println("[TestDataInitializer] test user already exists");
        }

        // 也添加示例帖子/评论/收藏用于演示
        User testUser = userMapper.selectByUsername(testUsername);
        if (testUser != null) {
            // 如果没有帖子存在，则创建帖子
            List<Post> recentPosts = postMapper.selectRecent(0, 1);
            if (recentPosts == null || recentPosts.isEmpty()) {
                Post p = new Post();
                p.setAuthorId(testUser.getId());
                p.setTitle("Welcome to the blog");
                p.setContent("This is a sample post created by TestDataInitializer.");
                p.setTags("sample");
                p.setLikes(0);
                p.setFavorites(0);
                p.setComments(0);
                p.setStatus(0);
                p.setCreateTime(LocalDateTime.now());
                p.setUpdateTime(LocalDateTime.now());
                postMapper.insertPost(p);
                System.out.println("[TestDataInitializer] created sample post");

                // 添加评论和收藏
                Comment c = new Comment();
                c.setTargetType(2L); // target: post
                c.setTargetId(p.getId());
                c.setUserId(testUser.getId());
                c.setContent("Nice post!");
                c.setCreateTime(LocalDateTime.now());
                commentMapper.insertComment(c);

                Favorite fav = new Favorite();
                fav.setTargetType(2L); // target: post
                fav.setTargetId(p.getId());
                fav.setUserId(testUser.getId());
                fav.setCreateTime(LocalDateTime.now());
                favoriteMapper.insertFavorite(fav);

                postMapper.incFavorites(p.getId());
                System.out.println("[TestDataInitializer] created sample comment and favorite");
            }
        }
    }
}