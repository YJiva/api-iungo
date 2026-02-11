package com.example.apiiungo.controller;

import com.example.apiiungo.common.Result;
import com.example.apiiungo.entity.User;
import com.example.apiiungo.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/test")
public class TestUserController {

    @Resource
    private UserService userService;

    // 查询所有用户名
    @GetMapping("/usernames")
    public Result<List<String>> getAllUsernames() {
        List<String> usernames = userService.getAllUsernames();
        return Result.success(usernames);
    }

    // 根据用户名查用户
    @GetMapping("/user")
    public Result<User> getUserByUsername(@RequestParam String username) {
        User user = userService.getUserByUsername(username);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.fail("用户名不存在");
        }
    }
}
