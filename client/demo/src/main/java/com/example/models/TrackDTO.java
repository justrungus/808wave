package com.example.models;

public class TrackDTO {
    private Long id;
    private String title;
    private String genre;
    private String album;
    private Integer bpm;
    private String musicalKey;
    private String uploaderUsername;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public String getAlbum() { return album; }
    public Integer getBpm() { return bpm; }
    public String getMusicalKey() { return musicalKey; }
    public String getUploaderUsername() { return uploaderUsername; }
}