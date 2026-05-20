package com.example.models;

public class PlaylistDTO {
    private Long id;
    private String name;
    private String creatorUsername;
    private Long creatorId;
    private String coverPath;

    public Long getId() {return id;}
    public String getName() {return name;}
    public String getCreatorUsername() {return creatorUsername;}
    public Long getCreatorId() {return creatorId;}
    public String getCoverPath() {return coverPath;}
}
