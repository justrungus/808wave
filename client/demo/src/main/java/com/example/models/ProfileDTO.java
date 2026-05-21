package com.example.models;

import java.util.List;

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

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getProfilePicturePath() { return profilePicturePath; }
    public int getFollowersCount() { return followersCount; }
    public int getFollowingCount() { return followingCount; }
    public boolean isFollowing() { return isFollowing; }
    public List<TrackDTO> getTracks() { return tracks; }
    public List<PlaylistDTO> getPlaylists() { return playlists; }
}