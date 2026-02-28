package com.example.apiiungo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String inviteCode;
    private LocalDateTime createTime;
}
