package com.apiiungo.controller;

import com.apiiungo.entity.Blog;
import com.apiiungo.entity.BlogType;
import com.apiiungo.entity.User;
import com.apiiungo.mapper.BlogMapper;
import com.apiiungo.mapper.BlogTypeMapper;
import com.apiiungo.mapper.FollowMapper;
import com.apiiungo.service.BlogService;
import com.apiiungo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/blog")
public class BlogController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogTypeMapper blogTypeMapper;
    @Autowired
    private FollowMapper followMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private BlogMapper blogMapper;

    // 保存博客（草稿/发布）
    @PostMapping("/save")
    public Map<String, Object> saveBlog(@RequestBody Blog blog) {
        Map<String, Object> result = new HashMap<>();
        boolean success = blogService.saveBlog(blog);
        result.put("code", success ? 200 : 400);
        result.put("msg", success ? "保存成功" : "保存失败");
        return result;
    }

    // 自动保存草稿（open_scope=0）
    @PostMapping("/save-draft")
    public Map<String, Object> saveDraft(@RequestBody Blog blog, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        blog.setUserId(userId);
        blog.setStatus(1);
        blog.setOpenScope(0);
        if (blog.getTop() == null) {
            blog.setTop(0);
        }
        boolean success = blogService.saveBlog(blog);
        result.put("code", success ? 200 : 400);
        result.put("msg", success ? "草稿已保存" : "草稿保存失败");
        result.put("id", blog.getId());
        return result;
    }

    @GetMapping("/draft/latest")
    public Map<String, Object> latestDraft(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        Blog draft = blogService.getLatestDraft(userId);
        result.put("code", 200);
        result.put("data", draft);
        return result;
    }

    @PostMapping("/draft/clear")
    public Map<String, Object> clearDraft(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        int cleared = blogService.clearDraft(userId);
        result.put("code", 200);
        result.put("msg", "草稿已清空");
        result.put("cleared", cleared);
        return result;
    }

    // 历史版本恢复
    @PostMapping("/restore/{blogId}/{versionId}")
    public Map<String, Object> restoreVersion(@PathVariable Long blogId, @PathVariable Long versionId) {
        Map<String, Object> result = new HashMap<>();
        boolean success = blogService.restoreVersion(blogId, versionId);
        result.put("code", success ? 200 : 400);
        result.put("msg", success ? "恢复成功" : "恢复失败");
        return result;
    }

    // 按邀请关系筛选博客
    @GetMapping("/list-by-scope")
    public Map<String, Object> listByScope(@RequestParam Long userId, @RequestParam Integer scope) {
        Map<String, Object> result = new HashMap<>();
        List<Blog> list = blogService.listByOpenScope(userId, scope);
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    // 查询所有已使用的标签（用于发博客时选择）
    @GetMapping("/tags")
    public Map<String, Object> listTags() {
        Map<String, Object> result = new HashMap<>();
        List<String> tags = blogService.listAllTags();
        result.put("code", 200);
        result.put("data", tags);
        return result;
    }

    /**
     * 对外暴露的博客标签类型列表（用于前台列表解析 tags）
     */
    @GetMapping("/types")
    public Map<String, Object> listTypes() {
        Map<String, Object> result = new HashMap<>();
        List<BlogType> list = blogTypeMapper.selectList(null);
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    /**
     * 博客详情：返回博客内容 + 解析后的标签列表，并自增阅读次数
     */
    @GetMapping("/detail/{id}")
    public Map<String, Object> detail(@PathVariable Long id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Blog blog = blogService.getDetailAndIncRead(id);
        if (blog == null) {
            result.put("code", 404);
            result.put("msg", "博客不存在");
            return result;
        }
        Long viewerId = getUserIdFromRequest(request);
        if (!canViewBlog(blog, viewerId)) {
            result.put("code", 403);
            result.put("msg", "无权限查看该博客");
            return result;
        }

        // 根据 blog.tags 里的 id 列表，查询 blog_type，并按原顺序返回
        List<Map<String, Object>> tagList = new ArrayList<>();
        String tagsStr = blog.getTags();
        if (tagsStr != null && !tagsStr.trim().isEmpty()) {
            String[] parts = tagsStr.split(",");
            Set<Integer> idSet = new HashSet<>();
            List<Integer> idOrder = new ArrayList<>();
            for (String p : parts) {
                try {
                    int tagId = Integer.parseInt(p.trim());
                    if (idSet.add(tagId)) {
                        idOrder.add(tagId);
                    }
                } catch (NumberFormatException ignore) {
                }
            }
            if (!idOrder.isEmpty()) {
                List<BlogType> types = blogTypeMapper.selectBatchIds(idOrder);
                Map<Integer, BlogType> typeMap = new HashMap<>();
                for (BlogType t : types) {
                    typeMap.put(t.getId(), t);
                }
                for (Integer tid : idOrder) {
                    BlogType t = typeMap.get(tid);
                    if (t != null) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("id", t.getId());
                        m.put("name", t.getName());
                        m.put("show", t.getShow());
                        tagList.add(m);
                    }
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("blog", blog);
        data.put("tags", tagList);

        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    /**
     * 前台公开博客统一列表：仅 status=1 且 open_scope=4；支持 keyword 分页；置顶文在同档位内按阅读量降序。
     * data: { list, total, page, pageSize, hasMore }
     */
    @GetMapping("/public-feed")
    public Map<String, Object> publicFeed(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        int p = page == null || page < 1 ? 1 : page;
        int size = pageSize == null ? 500 : Math.min(Math.max(pageSize, 1), 2000);
        String kw = keyword == null ? "" : keyword.trim();
        Map<String, Object> data = blogService.pagePublicBlogs(kw, p, size);
        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    /**
     * 圈层博客：仅 open_scope=2（粉丝可见），且当前用户已关注作者；status=1 才展示。
     */
    @GetMapping("/circle")
    public Map<String, Object> circle(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long viewerId = getUserIdFromRequest(request);
        if (viewerId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        List<Blog> list = blogService.listCircleBlogs(viewerId);
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    /**
     * 密友博客：仅展示 open_scope=3（仅互关可见）的帖子，且当前用户与作者互关或为作者本人
     */
    @GetMapping("/close-friends")
    public Map<String, Object> closeFriends(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long viewerId = getUserIdFromRequest(request);
        if (viewerId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        List<Blog> list = blogService.listCloseFriendBlogs(viewerId);
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    @GetMapping("/visibility/my")
    public Map<String, Object> myVisibility(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        Integer scope = blogMapper.selectPreferredScopeByUser(userId);
        if (scope == null) scope = 4;
        result.put("code", 200);
        result.put("data", Collections.singletonMap("visibilityScope", scope));
        return result;
    }

    @PostMapping("/visibility/my/update")
    public Map<String, Object> updateMyVisibility(@RequestParam Integer visibilityScope, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        if (visibilityScope == null || visibilityScope < 1 || visibilityScope > 4) {
            result.put("code", 400);
            result.put("msg", "visibilityScope 取值应为 1-4");
            return result;
        }
        int affected = blogMapper.updateScopeByUser(userId, visibilityScope);
        result.put("code", 200);
        result.put("msg", "更新成功");
        result.put("affected", affected);
        return result;
    }

    /**
     * 用户公开博客分页
     */
    @GetMapping("/public-by-user")
    public Map<String, Object> publicByUser(@RequestParam Long userId,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "5") int pageSize) {
        Map<String, Object> result = new HashMap<>();
        if (userId == null) {
            result.put("code", 400);
            result.put("msg", "userId 不能为空");
            return result;
        }
        List<Blog> list = blogService.listPublicByUser(userId, page, pageSize);
        long total = blogService.countPublicByUser(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    private boolean canViewBlog(Blog blog, Long viewerId) {
        Long authorId = blog == null ? null : blog.getUserId();
        Integer scope = blog == null ? null : blog.getOpenScope();
        if (authorId == null) {
            return false;
        }
        if (viewerId != null && authorId.equals(viewerId)) {
            return true;
        }
        if (scope == null) {
            scope = 4;
        }
        if (scope == 4) {
            return true;
        }
        if (viewerId == null) {
            return false;
        }
        int viewerFollowAuthor = followMapper.exists(viewerId, authorId);
        if (scope == 2) {
            return viewerFollowAuthor > 0;
        }
        if (scope == 3) {
            int authorFollowViewer = followMapper.exists(authorId, viewerId);
            return viewerFollowAuthor > 0 && authorFollowViewer > 0;
        }
        if (scope == 1) {
            return false;
        }
        return false;
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        User user = getUserFromRequest(request);
        return user == null ? null : user.getId();
    }

    private User getUserFromRequest(HttpServletRequest request) {
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
        return userService.findByUsername(username);
    }
}