package com.apiiungo.controller;

import com.apiiungo.entity.Post;
import com.apiiungo.service.PostService;
import com.apiiungo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private com.apiiungo.service.CommentService commentService;
    @Autowired
    private com.apiiungo.service.FavoriteService favoriteService;
    @Autowired
    private com.apiiungo.service.SubService subService;

    // 创建帖子（需登录）
    @PostMapping("/create")
    public Map<String, Object> createPost(@RequestBody Post post, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        String token = auth.substring(7).trim();
        if (!token.startsWith("dev-token:")) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        String username = token.substring("dev-token:".length());
        // find user
        com.apiiungo.entity.User user = userService.findByUsername(username);
        if (user == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        post.setAuthorId(user.getId());
        Post saved = postService.createPost(post);
        result.put("code", 200);
        result.put("data", saved);
        return result;
    }

    // 列表
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<Post> posts = postService.listRecent(offset, limit);
        result.put("code", 200);
        result.put("data", posts);
        return result;
    }

    // 详情
    @GetMapping("/detail")
    public Map<String, Object> detail(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        Post p = postService.getPost(id);
        if (p == null) {
            result.put("code", 404);
            result.put("msg", "帖子不存在");
        } else {
            result.put("code", 200);
            result.put("data", p);
        }
        return result;
    }

    @PostMapping("/like")
    public Map<String, Object> like(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean ok = postService.likePost(id);
        result.put("code", ok ? 200 : 400);
        return result;
    }

    @PostMapping("/favorite")
    public Map<String, Object> favorite(@RequestParam Long id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        // toggle favorite relation
        String auth = request.getHeader("Authorization");
        Long uid = getUserIdFromAuth(auth);
        if (uid == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        boolean now = favoriteService.toggleFavorite(id, uid);
        int count = favoriteService.countFavorites(id);
        result.put("code", 200);
        result.put("favorited", now);
        result.put("count", count);
        return result;
    }

    @PostMapping("/comment/add")
    public Map<String, Object> addComment(@RequestBody Map<String, String> body, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long postId = Long.valueOf(body.get("postId"));
        String content = body.get("content");
        String auth = request.getHeader("Authorization");
        Long uid = getUserIdFromAuth(auth);
        if (uid == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        com.apiiungo.entity.Comment comment = new com.apiiungo.entity.Comment();
        comment.setPostId(postId);
        comment.setUserId(uid);
        comment.setContent(content);
        commentService.addComment(comment);
        // 给帖子作者发送一条通知
        Post post = postService.getPost(postId);
        if (post != null && post.getAuthorId() != null && !post.getAuthorId().equals(uid)) {
            String msg = "你的帖子《" + (post.getTitle() == null ? "" : post.getTitle()) + "》收到了新评论";
            subService.addNotification(post.getAuthorId(), msg);
        }
        result.put("code", 200);
        result.put("data", comment);
        return result;
    }

    @GetMapping("/comment/list")
    public Map<String, Object> commentList(@RequestParam Long postId) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", commentService.listByPost(postId));
        return result;
    }

    private Long getUserIdFromAuth(String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) return null;
        auth = auth.substring(7).trim();
        if (!auth.startsWith("dev-token:")) return null;
        String username = auth.substring("dev-token:".length());
        com.apiiungo.entity.User user = userService.findByUsername(username);
        return user == null ? null : user.getId();
    }
}
