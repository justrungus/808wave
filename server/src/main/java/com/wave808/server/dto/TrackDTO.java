package com.wave808.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class TrackDTO {
    private Long id;
    private String title;
    private String genre;
    private Integer bpm;
    private String musicalKey;
    private String uploaderUsername;
    private String coverPath;
}
