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
    @Autowired
    private com.apiiungo.service.TargetTypeService targetTypeService;
    @Autowired
    private com.apiiungo.service.LikeService likeService;
    @Autowired
    private com.apiiungo.mapper.PostCategoryMapper postCategoryMapper;
    @Autowired
    private com.apiiungo.mapper.PostAdminMapper postAdminMapper;

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
        if (post.getCategoryId() == null) {
            result.put("code", 400);
            result.put("msg", "必须选择所属吧（categoryId）");
            return result;
        }
        if (postCategoryMapper.selectById(post.getCategoryId()) == null) {
            result.put("code", 404);
            result.put("msg", "吧不存在");
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
    public Map<String, Object> list(@RequestParam(defaultValue = "0") int offset,
                                    @RequestParam(defaultValue = "10") int limit,
                                    @RequestParam(required = false) Long categoryId) {
        Map<String, Object> result = new HashMap<>();
        List<Post> posts = categoryId == null
                ? postService.listRecent(offset, limit)
                : postService.listByCategory(categoryId, offset, limit);
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
        Long postTypeId = targetTypeService.getIdByCode("post");
        if (postTypeId == null) {
            result.put("code", 500);
            result.put("msg", "target 表未配置 post 类型");
            return result;
        }
        boolean now = favoriteService.toggleFavorite(uid, postTypeId, id);
        int count = favoriteService.countFavorites(postTypeId, id);
        result.put("code", 200);
        result.put("favorited", now);
        result.put("count", count);
        return result;
    }

    @PostMapping("/comment/add")
    public Map<String, Object> addComment(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long postId = body.get("postId") == null ? null : Long.valueOf(String.valueOf(body.get("postId")));
        Long parentCommentId = body.get("parentCommentId") == null ? null : Long.valueOf(String.valueOf(body.get("parentCommentId")));
        String content = body.get("content") == null ? null : String.valueOf(body.get("content"));
        if (postId == null || content == null || content.trim().isEmpty()) {
            result.put("code", 400);
            result.put("msg", "参数不完整");
            return result;
        }

        String auth = request.getHeader("Authorization");
        Long uid = getUserIdFromAuth(auth);
        if (uid == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }

        Long postTypeId = targetTypeService.getIdByCode("post");
        Long commentTypeId = targetTypeService.getIdByCode("comment");
        if (postTypeId == null || commentTypeId == null) {
            result.put("code", 500);
            result.put("msg", "target 表未配置 post/comment 类型");
            return result;
        }

        // 取消层级限制，仅校验父评论存在
        if (parentCommentId != null) {
            com.apiiungo.entity.Comment parent = commentService.getById(parentCommentId);
            if (parent == null) {
                result.put("code", 404);
                result.put("msg", "父评论不存在");
                return result;
            }
        }

        com.apiiungo.entity.Comment comment = new com.apiiungo.entity.Comment();
        comment.setUserId(uid);
        if (parentCommentId == null) {
            comment.setTargetType(postTypeId);
            comment.setTargetId(postId);
        } else {
            comment.setTargetType(commentTypeId);
            comment.setTargetId(parentCommentId);
        }
        comment.setContent(content.trim());
        commentService.addComment(comment);

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
        Long postTypeId = targetTypeService.getIdByCode("post");
        Long commentTypeId = targetTypeService.getIdByCode("comment");
        if (postTypeId == null || commentTypeId == null) {
            result.put("code", 500);
            result.put("msg", "target 表未配置 post/comment 类型");
            return result;
        }
        List<com.apiiungo.entity.Comment> roots = commentService.listByTarget(postTypeId, postId);
        List<Map<String, Object>> data = new java.util.ArrayList<>();
        for (com.apiiungo.entity.Comment root : roots) {
            Map<String, Object> node = toCommentNode(root);
            node.put("children", buildChildren(commentTypeId, root.getId()));
            data.add(node);
        }
        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    private List<Map<String, Object>> buildChildren(Long commentTypeId, Long parentId) {
        List<com.apiiungo.entity.Comment> children = commentService.listByTarget(commentTypeId, parentId);
        List<Map<String, Object>> data = new java.util.ArrayList<>();
        for (com.apiiungo.entity.Comment child : children) {
            Map<String, Object> node = toCommentNode(child);
            node.put("children", buildChildren(commentTypeId, child.getId()));
            data.add(node);
        }
        return data;
    }

    private Map<String, Object> toCommentNode(com.apiiungo.entity.Comment c) {
        Map<String, Object> node = new HashMap<>();
        node.put("id", c.getId());
        node.put("userId", c.getUserId());
        node.put("targetType", c.getTargetType());
        node.put("targetId", c.getTargetId());
        node.put("content", c.getContent());
        Long commentTypeId = targetTypeService.getIdByCode("comment");
        if (commentTypeId != null) {
            node.put("likeCount", likeService.countLikes(commentTypeId, c.getId()));
        } else {
            node.put("likeCount", 0);
        }
        node.put("createTime", c.getCreateTime());
        return node;
    }

    @PostMapping("/comment/like/toggle")
    public Map<String, Object> toggleCommentLike(@RequestParam Long commentId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long uid = getUserIdFromAuth(request.getHeader("Authorization"));
        if (uid == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        Long commentTypeId = targetTypeService.getIdByCode("comment");
        if (commentTypeId == null) {
            result.put("code", 500);
            result.put("msg", "target 表未配置 comment 类型");
            return result;
        }
        boolean liked = likeService.toggleLike(uid, commentTypeId, commentId);
        int likeCount = likeService.countLikes(commentTypeId, commentId);
        result.put("code", 200);
        result.put("liked", liked);
        result.put("likeCount", likeCount);
        return result;
    }

    @PostMapping("/comment/delete")
    public Map<String, Object> deleteComment(@RequestParam Long commentId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long uid = getUserIdFromAuth(request.getHeader("Authorization"));
        if (uid == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        com.apiiungo.entity.Comment c = commentService.getById(commentId);
        if (c == null) {
            result.put("code", 404);
            result.put("msg", "评论不存在");
            return result;
        }
        if (!uid.equals(c.getUserId())) {
            result.put("code", 403);
            result.put("msg", "仅作者可删除");
            return result;
        }
        deleteCommentCascade(commentId);
        result.put("code", 200);
        result.put("msg", "删除成功");
        return result;
    }

    private void deleteCommentCascade(Long commentId) {
        Long commentTypeId = targetTypeService.getIdByCode("comment");
        if (commentTypeId == null) return;
        List<com.apiiungo.entity.Comment> children = commentService.listByTarget(commentTypeId, commentId);
        for (com.apiiungo.entity.Comment child : children) {
            deleteCommentCascade(child.getId());
        }
        likeService.deleteByTarget(commentTypeId, commentId);
        commentService.deleteById(commentId);
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
