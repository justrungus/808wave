package com.wave808.server.controllers;

import com.wave808.server.dto.PlaylistDTO;
import com.wave808.server.dto.TrackDTO;
import com.wave808.server.services.PlaylistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }


    @PostMapping("/create")
    public ResponseEntity<PlaylistDTO> create(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        Long userId = Long.valueOf(body.get("userId").toString());
        return ResponseEntity.ok(playlistService.createPlaylist(name, userId));
    }

    @PostMapping("/add-track")
    public ResponseEntity<Void> addTrack(@RequestBody Map<String, Long> body){
        playlistService.addTrackToPlaylist(body.get("playlistId"), body.get("trackId"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove-track")
    public ResponseEntity<Void> removeTrack(@RequestBody Map<String, Long> body){
        playlistService.removeTrackFromPlaylist(body.get("playlistId"), body.get("trackId"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{playlistId}/tracks")
    public ResponseEntity<List<TrackDTO>> getTracks(@PathVariable Long playlistId) {
        return ResponseEntity.ok(playlistService.getPlaylistTracks(playlistId));
    }

    @GetMapping("/mine/{userId}")
    public ResponseEntity<List<PlaylistDTO>> getMyPlaylists(@PathVariable Long userId) {
        return ResponseEntity.ok(playlistService.getMyPlaylists(userId));
    }

    @GetMapping("/saved/{userId}")
    public ResponseEntity<List<PlaylistDTO>> getSavedPlaylists(@PathVariable Long userId) {
        return ResponseEntity.ok(playlistService.getSavedPlaylists(userId));
    }

    @PostMapping("/save")
    public ResponseEntity<Void> savePlaylist(@RequestBody Map<String, Long> body) {
        playlistService.savePlaylist(body.get("userId"), body.get("playlistId"));
        return ResponseEntity.ok().build();
    }
}