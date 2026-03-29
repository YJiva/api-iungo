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
    private com.apiiungo.service.LikeService likeService;
    @Autowired
    private com.apiiungo.service.SubService subService;
    @Autowired
    private com.apiiungo.service.TargetTypeService targetTypeService;
    @Autowired
    private com.apiiungo.mapper.PostCategoryMapper postCategoryMapper;
    @Autowired
    private com.apiiungo.mapper.PostAdminMapper postAdminMapper;
    @Autowired
    private com.apiiungo.mapper.PostCategoryMuteMapper postCategoryMuteMapper;

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
        // 吧内禁言校验：被禁言用户禁止在本吧发帖
        if (postCategoryMuteMapper.selectActive(post.getCategoryId(), user.getId(), java.time.LocalDateTime.now()) != null) {
            result.put("code", 403);
            result.put("msg", "你已被本吧禁言，暂时无法发帖");
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
                                    @RequestParam(required = false) Long categoryId,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) Integer essenceOnly) {
        Map<String, Object> result = new HashMap<>();
        List<Post> posts = categoryId == null
                ? postService.listRecent(offset, limit)
                : postService.listByCategoryWithFilter(categoryId, offset, limit, keyword, essenceOnly);
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

    /**
     * 帖子点赞状态（用于详情页初始化，与 {@link #like} 使用同一套 target_type=post）
     */
    @GetMapping("/like/status")
    public Map<String, Object> likeStatus(@RequestParam Long id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        if (id == null) {
            result.put("code", 400);
            result.put("msg", "id 不能为空");
            return result;
        }
        Long postTypeId = targetTypeService.getIdByCode("post");
        if (postTypeId == null) {
            result.put("code", 500);
            result.put("msg", "target 表未配置 post 类型");
            return result;
        }
        Long uid = getUserIdFromAuth(request.getHeader("Authorization"));
        boolean liked = uid != null && likeService.isLiked(uid, postTypeId, id);
        int count = likeService.countLikes(postTypeId, id);
        result.put("code", 200);
        result.put("liked", liked);
        result.put("likeCount", count);
        return result;
    }

    @PostMapping("/like")
    public Map<String, Object> like(@RequestParam Long id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long uid = getUserIdFromAuth(request.getHeader("Authorization"));
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
        boolean liked = likeService.toggleLike(uid, postTypeId, id);
        if (liked) {
            postService.likePost(id);
        } else {
            postService.unlikePost(id);
        }
        int count = likeService.countLikes(postTypeId, id);
        if (liked) {
            Post post = postService.getPost(id);
            if (post != null && post.getAuthorId() != null && !post.getAuthorId().equals(uid)) {
                com.apiiungo.entity.User actor = userService.findById(uid);
                String actorName = actor != null && actor.getUsername() != null ? actor.getUsername() : "有人";
                String sourceCategory = null;
                if (post.getCategoryId() != null) {
                    com.apiiungo.entity.PostCategory c = postCategoryMapper.selectById(post.getCategoryId());
                    sourceCategory = c == null ? null : c.getName();
                }
                subService.addNotification(
                        post.getAuthorId(),
                        actorName + " 点赞了你的帖子",
                        "POST_LIKE",
                        post.getTitle(),
                        sourceCategory,
                        "/post/detail?id=" + post.getId()
                );
            }
        }
        result.put("code", 200);
        result.put("liked", liked);
        result.put("likeCount", count);
        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestParam Long id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long uid = getUserIdFromAuth(request.getHeader("Authorization"));
        if (uid == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        Post post = postService.getPost(id);
        if (post == null) {
            result.put("code", 404);
            result.put("msg", "帖子不存在");
            return result;
        }
        com.apiiungo.entity.User user = userService.findById(uid);
        boolean isAdmin = user != null && user.getRoleId() != null && user.getRoleId() == 2L;
        boolean isBarAdmin = post.getCategoryId() != null && postAdminMapper.existsActive(post.getCategoryId(), uid) > 0;
        if (!isAdmin && !isBarAdmin && (post.getAuthorId() == null || !post.getAuthorId().equals(uid))) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        boolean ok = postService.deletePost(id);
        result.put("code", ok ? 200 : 400);
        result.put("msg", ok ? "删除成功" : "删除失败");
        return result;
    }

    @PostMapping("/admin/delete")
    public Map<String, Object> adminDelete(@RequestParam Long id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long uid = getUserIdFromAuth(request.getHeader("Authorization"));
        if (uid == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        com.apiiungo.entity.User user = userService.findById(uid);
        if (user == null || user.getRoleId() == null || user.getRoleId() != 2L) {
            result.put("code", 403);
            result.put("msg", "仅管理员可操作");
            return result;
        }
        boolean ok = postService.deletePost(id);
        result.put("code", ok ? 200 : 400);
        result.put("msg", ok ? "删除成功" : "删除失败");
        return result;
    }

    @PostMapping("/admin/top/toggle")
    public Map<String, Object> adminToggleTop(@RequestParam Long id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long uid = getUserIdFromAuth(request.getHeader("Authorization"));
        if (uid == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        Post post = postService.getPost(id);
        if (post == null) {
            result.put("code", 404);
            result.put("msg", "帖子不存在");
            return result;
        }
        com.apiiungo.entity.User user = userService.findById(uid);
        boolean isAdmin = user != null && user.getRoleId() != null && user.getRoleId() == 2L;
        boolean isBarAdmin = post.getCategoryId() != null && postAdminMapper.existsActive(post.getCategoryId(), uid) > 0;
        if (!isAdmin && !isBarAdmin) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        boolean ok = postService.toggleTop(id);
        result.put("code", ok ? 200 : 400);
        result.put("msg", ok ? "操作成功" : "操作失败");
        return result;
    }

    @PostMapping("/admin/essence/toggle")
    public Map<String, Object> adminToggleEssence(@RequestParam Long id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long uid = getUserIdFromAuth(request.getHeader("Authorization"));
        if (uid == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        Post post = postService.getPost(id);
        if (post == null) {
            result.put("code", 404);
            result.put("msg", "帖子不存在");
            return result;
        }
        com.apiiungo.entity.User user = userService.findById(uid);
        boolean isAdmin = user != null && user.getRoleId() != null && user.getRoleId() == 2L;
        boolean isBarAdmin = post.getCategoryId() != null && postAdminMapper.existsActive(post.getCategoryId(), uid) > 0;
        if (!isAdmin && !isBarAdmin) {
            result.put("code", 403);
            result.put("msg", "无权限");
            return result;
        }
        boolean ok = postService.toggleEssence(id);
        result.put("code", ok ? 200 : 400);
        result.put("msg", ok ? "操作成功" : "操作失败");
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

        // 吧内禁言校验：被禁言用户禁止在本吧发言（评论也算）
        Post post = postService.getPost(postId);
        if (post != null && post.getCategoryId() != null) {
            if (postCategoryMuteMapper.selectActive(post.getCategoryId(), uid, java.time.LocalDateTime.now()) != null) {
                result.put("code", 403);
                result.put("msg", "你已被本吧禁言，暂时无法发言");
                return result;
            }
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

        com.apiiungo.entity.User actor = userService.findById(uid);
        String actorName = actor != null && actor.getUsername() != null ? actor.getUsername() : "有人";
        String sourceCategory = null;
        if (post != null && post.getCategoryId() != null) {
            com.apiiungo.entity.PostCategory c = postCategoryMapper.selectById(post.getCategoryId());
            sourceCategory = c == null ? null : c.getName();
        }

        if (parentCommentId != null) {
            com.apiiungo.entity.Comment parent = commentService.getById(parentCommentId);
            if (parent != null && parent.getUserId() != null && !parent.getUserId().equals(uid) && post != null) {
                String msg = actorName + " 回复了你的评论：" + trimContent(content.trim());
                subService.addNotification(parent.getUserId(), msg, "POST_REPLY", post.getTitle(), sourceCategory, "/post/detail?id=" + post.getId());
            }
        } else if (post != null && post.getAuthorId() != null && !post.getAuthorId().equals(uid)) {
            String msg = actorName + " 评论了你的帖子：" + trimContent(content.trim());
            subService.addNotification(post.getAuthorId(), msg, "POST_REPLY", post.getTitle(), sourceCategory, "/post/detail?id=" + post.getId());
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
        com.apiiungo.entity.Comment comment = commentService.getById(commentId);
        if (comment == null) {
            result.put("code", 404);
            result.put("msg", "评论不存在");
            return result;
        }

        boolean liked = likeService.toggleLike(uid, commentTypeId, commentId);
        int likeCount = likeService.countLikes(commentTypeId, commentId);

        if (liked) {
            Long notifyUserId = null;
            // 点赞楼中楼：通知被回复的人（父评论作者）
            if (comment.getTargetType() != null && comment.getTargetType().equals(commentTypeId)) {
                com.apiiungo.entity.Comment parent = commentService.getById(comment.getTargetId());
                if (parent != null) {
                    notifyUserId = parent.getUserId();
                }
            } else {
                // 点赞一层楼：通知该楼作者
                notifyUserId = comment.getUserId();
            }
            if (notifyUserId != null && !notifyUserId.equals(uid)) {
                Post post = resolvePostByComment(comment);
                if (post != null) {
                    com.apiiungo.entity.User actor = userService.findById(uid);
                    String actorName = actor != null && actor.getUsername() != null ? actor.getUsername() : "有人";
                    String sourceCategory = null;
                    if (post.getCategoryId() != null) {
                        com.apiiungo.entity.PostCategory c = postCategoryMapper.selectById(post.getCategoryId());
                        sourceCategory = c == null ? null : c.getName();
                    }
                    subService.addNotification(
                            notifyUserId,
                            actorName + " 赞了你的回复",
                            "POST_COMMENT_LIKE",
                            post.getTitle(),
                            sourceCategory,
                            "/post/detail?id=" + post.getId()
                    );
                }
            }
        }

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

    private Post resolvePostByComment(com.apiiungo.entity.Comment comment) {
        if (comment == null || comment.getTargetType() == null || comment.getTargetId() == null) {
            return null;
        }
        Long postTypeId = targetTypeService.getIdByCode("post");
        Long commentTypeId = targetTypeService.getIdByCode("comment");
        if (postTypeId == null || commentTypeId == null) {
            return null;
        }
        Long type = comment.getTargetType();
        Long targetId = comment.getTargetId();
        if (type.equals(postTypeId)) {
            return postService.getPost(targetId);
        }
        while (type.equals(commentTypeId)) {
            com.apiiungo.entity.Comment parent = commentService.getById(targetId);
            if (parent == null || parent.getTargetType() == null || parent.getTargetId() == null) {
                return null;
            }
            if (parent.getTargetType().equals(postTypeId)) {
                return postService.getPost(parent.getTargetId());
            }
            type = parent.getTargetType();
            targetId = parent.getTargetId();
        }
        return null;
    }

    private String trimContent(String content) {
        if (content == null) {
            return "";
        }
        String text = content.trim();
        return text.length() > 30 ? text.substring(0, 30) + "..." : text;
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
