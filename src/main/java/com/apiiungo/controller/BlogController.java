package com.apiiungo.controller;

import com.apiiungo.entity.Blog;
import com.apiiungo.entity.BlogType;
import com.apiiungo.mapper.BlogTypeMapper;
import com.apiiungo.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/blog")
public class BlogController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogTypeMapper blogTypeMapper;

    // 保存博客（草稿/发布）
    @PostMapping("/save")
    public Map<String, Object> saveBlog(@RequestBody Blog blog) {
        Map<String, Object> result = new HashMap<>();
        boolean success = blogService.saveBlog(blog);
        result.put("code", success ? 200 : 400);
        result.put("msg", success ? "保存成功" : "保存失败");
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
    public Map<String, Object> detail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Blog blog = blogService.getDetailAndIncRead(id);
        if (blog == null) {
            result.put("code", 404);
            result.put("msg", "博客不存在");
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
}