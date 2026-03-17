package com.apiiungo.controller;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();
        if (file == null || file.isEmpty()) {
            result.put("code", 400);
            result.put("msg", "文件为空");
            return result;
        }
        // 简单大小限制：2MB 以内
        long maxSize = 2L * 1024 * 1024;
        if (file.getSize() > maxSize) {
            result.put("code", 400);
            result.put("msg", "图片数据过大，请选择不超过2MB的图片");
            return result;
        }
        // 允许的简单图片类型校验
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            result.put("code", 400);
            result.put("msg", "仅支持图片文件");
            return result;
        }
        // 构建本地保存路径： <project_root>/upload/avatars/yyyyMMdd/
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path avatarDir = Paths.get(System.getProperty("user.dir"), "upload", "avatars", dateDir);
        Files.createDirectories(avatarDir);

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "avatar.png" : file.getOriginalFilename());
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            ext = original.substring(dot);
        }
        // 使用时间戳命名，满足「按时间戳命名」的需求
        String filename = System.currentTimeMillis() + ext;
        Path target = avatarDir.resolve(filename);
        // 确保父目录存在（某些环境下相对路径可能不同）
        Files.createDirectories(target.getParent());
        file.transferTo(target.toFile());

        // 返回前端可直接使用的相对 URL
        String url = "/upload/avatars/" + dateDir + "/" + filename;
        result.put("code", 200);
        result.put("msg", "上传成功");
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        result.put("data", data);
        return result;
    }

    /**
     * 富文本编辑器图片上传
     */
    @PostMapping(value = "/upload-editor-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadEditorImage(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();
        if (file == null || file.isEmpty()) {
            result.put("code", 400);
            result.put("msg", "文件为空");
            return result;
        }
        // 适当放宽一点大小限制，比如 5MB
        long maxSize = 5L * 1024 * 1024;
        if (file.getSize() > maxSize) {
            result.put("code", 400);
            result.put("msg", "图片数据过大，请选择不超过5MB的图片");
            return result;
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            result.put("code", 400);
            result.put("msg", "仅支持图片文件");
            return result;
        }

        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path imgDir = Paths.get(System.getProperty("user.dir"), "upload", "editor", dateDir);
        Files.createDirectories(imgDir);

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "image.png" : file.getOriginalFilename());
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            ext = original.substring(dot);
        }
        String filename = System.currentTimeMillis() + ext;
        Path target = imgDir.resolve(filename);
        Files.createDirectories(target.getParent());
        file.transferTo(target.toFile());

        String url = "/upload/editor/" + dateDir + "/" + filename;
        result.put("code", 200);
        result.put("msg", "上传成功");
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        result.put("data", data);
        return result;
    }
}

