package com.example.models;

public class TrackDTO {
    private Long id;
    private String title;
    private String genre;
    private Integer bpm;
    private String musicalKey;
    private String uploaderUsername;
    private Long uploaderId;
    private String coverPath;
    private String description;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public Integer getBpm() { return bpm; }
    public String getMusicalKey() { return musicalKey; }
    public String getUploaderUsername() { return uploaderUsername; }
    public Long getUploaderId() { return uploaderId; }
    public String getCoverPath() { return coverPath; }
    public String getDescription() { return description; }
}