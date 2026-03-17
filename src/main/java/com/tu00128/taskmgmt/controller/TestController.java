package com.tu00128.taskmgmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test")
    public String test() {
        return "✅ Test controller is working!";
    }

    @GetMapping("/test-db")
    public String testDatabase() {
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT COUNT(*) as count FROM users");
            return "✅ Database connection OK! Users count: " + result.get(0).get("count");
        } catch (Exception e) {
            return "❌ Database error: " + e.getMessage();
        }
    }

    @GetMapping("/test-tables")
    public String testTables() {
        try {
            List<String> tables = jdbcTemplate.queryForList(
                    "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE'",
                    String.class
            );
            return "✅ Tables in database: " + tables;
        } catch (Exception e) {
            return "❌ Error getting tables: " + e.getMessage();
        }
    }
}