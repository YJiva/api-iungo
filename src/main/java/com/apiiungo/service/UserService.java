package com.example.apiiungo.service;

import com.example.apiiungo.entity.User;
import java.util.List;

public interface UserService {
    List<String> getAllUsernames();
    User getUserByUsername(String username);
}
