package com.example.apiiungo.mapper;

import com.example.apiiungo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

// 原生 MyBatis 的 @Mapper 注解
@Mapper
public interface UserMapper {

    // 注解写 SQL：查询所有用户名
    @Select("SELECT username FROM user")
    List<String> getAllUsernames();

    // 注解写 SQL：根据用户名查用户（#{username} 防止 SQL 注入）
    @Select("SELECT * FROM user WHERE username = #{username}")
    User getUserByUsername(String username);
}
