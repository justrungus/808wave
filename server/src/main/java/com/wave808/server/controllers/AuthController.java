package com.wave808.server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wave808.server.dto.AuthRequest;
import com.wave808.server.dto.UserDTO;
import com.wave808.server.services.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody AuthRequest request) {
        UserDTO user = userService.register(request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody AuthRequest request) {
        UserDTO user = userService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(user);
    }
}
