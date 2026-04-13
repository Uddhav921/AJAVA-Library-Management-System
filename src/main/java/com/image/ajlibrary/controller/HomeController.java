package com.image.ajlibrary.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("app", "Library Book Issue Management System");
        info.put("status", "running");
        info.put("endpoints", Map.of(
                "users", "/api/users  — register / login / list",
                "books", "/api/books  — add / search / available",
                "borrow", "/api/borrow — issue / return / history / overdue"));
        return info;
    }
}
