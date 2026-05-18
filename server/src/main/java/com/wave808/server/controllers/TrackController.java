package com.wave808.server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wave808.server.dto.TrackDTO;
import com.wave808.server.services.TrackService;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @PostMapping("/upload")
    public ResponseEntity<TrackDTO> upload(
            @RequestParam("title") String title,
            @RequestParam("genre") String genre,
            @RequestParam("bpm") Integer bpm,
            @RequestParam("musicalKey") String musicalKey,
            @RequestParam("userId") Long userId,
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam(value = "cover", required = false) MultipartFile coverImage) throws Exception {

        TrackDTO track = trackService.uploadTrack(
            title, genre, bpm, musicalKey, userId, audioFile, coverImage
        );
        return ResponseEntity.ok(track);
    }
}