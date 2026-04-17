package com.wave808.server.controllers;

import com.wave808.server.dto.AuthRequest;
import com.wave808.server.dto.UserDTO;
import com.wave808.server.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request){
        try{
            UserDTO user = userService.register(request.getUsername(), request.getEmail(), request.getPassword());
            return ResponseEntity.ok(user);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        try{
            UserDTO user = userService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(user);
        }catch (RuntimeException e){
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
