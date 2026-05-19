package com.wave808.server.controllers;

import com.wave808.server.dto.TrackDTO;
import com.wave808.server.services.TrackService;
import com.wave808.server.services.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;
    private final LikeService likeService;

    public TrackController(TrackService trackService, LikeService likeService) {
        this.trackService = trackService;
        this.likeService = likeService;
    }

    @PostMapping("/upload")
    public ResponseEntity<TrackDTO> upload(
            @RequestParam("title") String title,
            @RequestParam("genre") String genre,
            @RequestParam("bpm") Integer bpm,
            @RequestParam("musicalKey") String musicalKey,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("userId") Long userId,
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam(value = "cover", required = false) MultipartFile coverImage) throws Exception {
        return ResponseEntity.ok(trackService.uploadTrack(title, genre, bpm, musicalKey, description, userId, audioFile, coverImage));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<TrackDTO>> getRecent() {
        return ResponseEntity.ok(trackService.getRecentTracks());
    }

    @GetMapping("/top")
    public ResponseEntity<List<TrackDTO>> getTop() {
        return ResponseEntity.ok(trackService.getTopTracks());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TrackDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(trackService.getTracksByUser(userId));
    }

    @GetMapping("/liked/{userId}")
    public ResponseEntity<List<TrackDTO>> getLiked(@PathVariable Long userId) {
        return ResponseEntity.ok(likeService.getLikedTracks(userId));
    }

    @PostMapping("/like")
    public ResponseEntity<Map<String, Boolean>> toggleLike(@RequestBody Map<String, Long> body) {
        boolean liked = likeService.toggleLike(body.get("userId"), body.get("trackId"));
        return ResponseEntity.ok(Map.of("liked", liked));
    }
}