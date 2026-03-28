package com.apiiungo.controller;

import com.apiiungo.entity.Comment;
import com.apiiungo.entity.User;
import com.apiiungo.service.CommentService;
import com.apiiungo.service.FavoriteService;
import com.apiiungo.service.LikeService;
import com.apiiungo.service.SubService;
import com.apiiungo.service.TargetTypeService;
import com.apiiungo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blog/interact")
public class BlogInteractController {

    @Autowired
    private UserService userService;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private TargetTypeService targetTypeService;
    @Autowired
    private SubService subService;

    @GetMapping("/status")
    public Map<String, Object> status(@RequestParam Long blogId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        if (blogId == null) {
            result.put("code", 400);
            result.put("msg", "blogId 不能为空");
            return result;
        }

        Long blogTypeId = targetTypeService.getIdByCode("blog");
        if (blogTypeId == null) {
            result.put("code", 500);
            result.put("msg", "target 表未配置 blog 类型");
            return result;
        }

        Long uid = getUserIdFromRequest(request);
        boolean liked = uid != null && likeService.isLiked(uid, blogTypeId, blogId);
        boolean favorited = uid != null && favoriteService.isFavorited(uid, blogTypeId, blogId);
        int likeCount = likeService.countLikes(blogTypeId, blogId);
        int favoriteCount = favoriteService.countFavorites(blogTypeId, blogId);
        int commentCount = commentService.listByTarget(blogTypeId, blogId).size();

        Map<String, Object> data = new HashMap<>();
        data.put("liked", liked);
        data.put("favorited", favorited);
        data.put("likeCount", likeCount);
        data.put("favoriteCount", favoriteCount);
        data.put("commentCount", commentCount);
        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    @PostMapping("/like/toggle")
    public Map<String, Object> toggleLike(@RequestParam Long blogId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long uid = getUserIdFromRequest(request);
        if (uid == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        Long blogTypeId = targetTypeService.getIdByCode("blog");
        if (blogTypeId == null) {
            result.put("code", 500);
            result.put("msg", "target 表未配置 blog 类型");
            return result;
        }
        boolean liked = likeService.toggleLike(uid, blogTypeId, blogId);
        if (liked) {
            try {
                com.apiiungo.entity.Blog blog = new com.apiiungo.entity.Blog();
                blog.setId(blogId);
                // 简化：无法直接拿作者时，跳过。后续可注入 BlogService 精确通知。
            } catch (Exception ignored) {
            }
        }
        result.put("code", 200);
        result.put("liked", liked);
        result.put("likeCount", likeService.countLikes(blogTypeId, blogId));
        return result;
    }

    @PostMapping("/favorite/toggle")
    public Map<String, Object> toggleFavorite(@RequestParam Long blogId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long uid = getUserIdFromRequest(request);
        if (uid == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        Long blogTypeId = targetTypeService.getIdByCode("blog");
        if (blogTypeId == null) {
            result.put("code", 500);
            result.put("msg", "target 表未配置 blog 类型");
            return result;
        }
        boolean favorited = favoriteService.toggleFavorite(uid, blogTypeId, blogId);
        result.put("code", 200);
        result.put("favorited", favorited);
        result.put("favoriteCount", favoriteService.countFavorites(blogTypeId, blogId));
        return result;
    }

    @GetMapping("/comments")
    public Map<String, Object> comments(@RequestParam Long blogId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long blogTypeId = targetTypeService.getIdByCode("blog");
        Long commentTypeId = targetTypeService.getIdByCode("comment");
        if (blogTypeId == null || commentTypeId == null) {
            result.put("code", 500);
            result.put("msg", "target 表未配置 blog/comment 类型");
            return result;
        }

        Long uid = getUserIdFromRequest(request);
        List<Comment> roots = commentService.listByTarget(blogTypeId, blogId);
        List<Map<String, Object>> rootList = new java.util.ArrayList<>();
        for (Comment root : roots) {
            Map<String, Object> node = toCommentNode(root, uid, commentTypeId);
            node.put("children", buildCommentChildren(commentTypeId, root.getId(), uid));
            rootList.add(node);
        }

        result.put("code", 200);
        result.put("data", rootList);
        return result;
    }

    @PostMapping("/comment/add")
    public Map<String, Object> addComment(@RequestParam Long blogId,
                                          @RequestParam(required = false) Long parentCommentId,
                                          @RequestParam String content,
                                          HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long uid = getUserIdFromRequest(request);
        if (uid == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        if (content == null || content.trim().isEmpty()) {
            result.put("code", 400);
            result.put("msg", "评论内容不能为空");
            return result;
        }
        Long blogTypeId = targetTypeService.getIdByCode("blog");
        Long commentTypeId = targetTypeService.getIdByCode("comment");
        if (blogTypeId == null || commentTypeId == null) {
            result.put("code", 500);
            result.put("msg", "target 表未配置 blog/comment 类型");
            return result;
        }
        Comment c = new Comment();
        c.setUserId(uid);
        if (parentCommentId != null && parentCommentId > 0) {
            c.setTargetType(commentTypeId);
            c.setTargetId(parentCommentId);
        } else {
            c.setTargetType(blogTypeId);
            c.setTargetId(blogId);
        }
        c.setContent(content.trim());
        c.setLikeCount(0);
        Comment saved = commentService.addComment(c);

        result.put("code", 200);
        result.put("msg", "评论成功");
        result.put("data", saved);
        return result;
    }

    @PostMapping("/comment/like/toggle")
    public Map<String, Object> toggleCommentLike(@RequestParam Long commentId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long uid = getUserIdFromRequest(request);
        if (uid == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        if (commentId == null) {
            result.put("code", 400);
            result.put("msg", "commentId 不能为空");
            return result;
        }

        Comment comment = commentService.getById(commentId);
        if (comment == null) {
            result.put("code", 404);
            result.put("msg", "评论不存在");
            return result;
        }

        Long commentTypeId = targetTypeService.getIdByCode("comment");
        if (commentTypeId == null) {
            result.put("code", 500);
            result.put("msg", "target 表未配置 comment 类型");
            return result;
        }

        boolean liked = likeService.toggleLike(uid, commentTypeId, commentId);
        commentService.updateLikeCount(commentId, liked ? 1 : -1);
        Comment latest = commentService.getById(commentId);

        result.put("code", 200);
        result.put("liked", liked);
        result.put("likeCount", latest != null && latest.getLikeCount() != null ? latest.getLikeCount() : 0);
        return result;
    }

    @PostMapping("/comment/delete")
    public Map<String, Object> deleteComment(@RequestParam Long commentId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long uid = getUserIdFromRequest(request);
        if (uid == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        if (commentId == null) {
            result.put("code", 400);
            result.put("msg", "commentId 不能为空");
            return result;
        }

        Comment comment = commentService.getById(commentId);
        if (comment == null) {
            result.put("code", 404);
            result.put("msg", "评论不存在");
            return result;
        }

        boolean isOwner = comment.getUserId() != null && comment.getUserId().equals(uid);
        boolean isAdmin = false;
        User user = userService.findByUsername(getUsernameFromRequest(request));
        if (user != null && user.getRoleId() != null && user.getRoleId() == 2L) {
            isAdmin = true;
        }
        if (!isOwner && !isAdmin) {
            result.put("code", 403);
            result.put("msg", "无权限删除");
            return result;
        }

        Long commentTypeId = targetTypeService.getIdByCode("comment");
        if (commentTypeId == null) {
            result.put("code", 500);
            result.put("msg", "target 表未配置 comment 类型");
            return result;
        }

        // 递归删除子评论
        deleteCommentCascade(commentTypeId, commentId);

        result.put("code", 200);
        result.put("msg", "删除成功");
        return result;
    }

    private Map<String, Object> toCommentNode(Comment c, Long uid, Long commentTypeId) {
        Map<String, Object> node = new HashMap<>();
        node.put("id", c.getId());
        node.put("userId", c.getUserId());
        node.put("targetType", c.getTargetType());
        node.put("targetId", c.getTargetId());
        node.put("content", c.getContent());
        node.put("likeCount", c.getLikeCount() == null ? 0 : c.getLikeCount());
        node.put("liked", uid != null && likeService.isLiked(uid, commentTypeId, c.getId()));
        node.put("createTime", c.getCreateTime());
        return node;
    }

    private List<Map<String, Object>> buildCommentChildren(Long commentTypeId, Long parentId, Long uid) {
        List<Comment> children = commentService.listByTarget(commentTypeId, parentId);
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Comment child : children) {
            Map<String, Object> node = toCommentNode(child, uid, commentTypeId);
            node.put("children", buildCommentChildren(commentTypeId, child.getId(), uid));
            result.add(node);
        }
        return result;
    }

    private void deleteCommentCascade(Long commentTypeId, Long commentId) {
        List<Comment> children = commentService.listByTarget(commentTypeId, commentId);
        for (Comment child : children) {
            deleteCommentCascade(commentTypeId, child.getId());
        }
        // 删除该评论的点赞关系
        likeService.deleteByTarget(commentTypeId, commentId);
        // 删除该评论
        commentService.deleteById(commentId);
    }

    private String getUsernameFromRequest(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null) {
            return null;
        }
        String token = auth.trim();
        if (token.isEmpty()) {
            return null;
        }
        String lower = token.toLowerCase();
        if (lower.startsWith("bearer ")) {
            token = token.substring(7).trim();
        }
        if (!token.startsWith("dev-token:")) {
            return null;
        }
        return token.substring("dev-token:".length());
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null) {
            return null;
        }
        String token = auth.trim();
        if (token.isEmpty()) {
            return null;
        }
        String lower = token.toLowerCase();
        if (lower.startsWith("bearer ")) {
            token = token.substring(7).trim();
        }
        if (!token.startsWith("dev-token:")) {
            return null;
        }
        String username = token.substring("dev-token:".length());
        User user = userService.findByUsername(username);
        return user == null ? null : user.getId();
    }
}
