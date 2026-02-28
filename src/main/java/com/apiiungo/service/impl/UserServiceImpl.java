package com.example.apiiungo.service.impl;

import com.example.apiiungo.entity.User;
import com.example.apiiungo.mapper.UserMapper;
import com.example.apiiungo.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<String> getAllUsernames() {
        // 调用原生 MyBatis Mapper 的方法
        return userMapper.getAllUsernames();
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }
}
