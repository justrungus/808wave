package com.wave808.server.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wave808.server.dto.ProfileDTO;
import com.wave808.server.dto.TrackDTO;
import com.wave808.server.dto.UserDTO;
import com.wave808.server.services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<ProfileDTO> getProfile(
            @PathVariable Long userId,
            @RequestParam(required = false) Long requesterId) {
        return ResponseEntity.ok(userService.getProfile(userId, requesterId));
    }

    @PostMapping("/follow")
    public ResponseEntity<Void> follow(@RequestBody Map<String, Long> body) {
        userService.follow(body.get("followerId"), body.get("followedId"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unfollow")
    public ResponseEntity<Void> unfollow(@RequestBody Map<String, Long> body) {
        userService.unfollow(body.get("followerId"), body.get("followedId"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<UserDTO>> getFriends(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFriends(userId));
    }

    @GetMapping("/{userId}/following-feed")
    public ResponseEntity<List<TrackDTO>> getFollowingFeed(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFollowingFeed(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }

    @PostMapping("/{userId}/profile-picture")
    public ResponseEntity<UserDTO> updateProfilePicture(
            @PathVariable Long userId,
            @RequestParam("image") MultipartFile image) throws IOException {
        return ResponseEntity.ok(userService.updateProfilePicture(userId, image));
    }
}