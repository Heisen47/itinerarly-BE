package com.example.itinerarly_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class home {

    @GetMapping("/")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hello World");
    }

    @GetMapping("/start")
    public ResponseEntity<String> start() {
        return ResponseEntity.ok("Itinerarly BE is running");
    }
}
