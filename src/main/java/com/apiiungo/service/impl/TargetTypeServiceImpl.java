package com.apiiungo.service.impl;

import com.apiiungo.service.TargetTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TargetTypeServiceImpl implements TargetTypeService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Long getIdByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id FROM target WHERE target_name = ? ORDER BY id ASC LIMIT 1",
                code.trim().toLowerCase()
        );
        if (rows == null || rows.isEmpty()) {
            return null;
        }
        Object id = rows.get(0).get("id");
        if (id instanceof Number) {
            return ((Number) id).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(id));
        } catch (Exception ignored) {
            return null;
        }
    }
}
