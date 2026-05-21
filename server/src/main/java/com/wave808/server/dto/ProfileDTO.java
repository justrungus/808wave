package com.wave808.server.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileDTO {
    private Long id;
    private String username;
    private String email;
    private String profilePicturePath;
    private int followersCount;
    private int followingCount;
    private boolean isFollowing;
    private List<TrackDTO> tracks;
    private List<PlaylistDTO> playlists;
}