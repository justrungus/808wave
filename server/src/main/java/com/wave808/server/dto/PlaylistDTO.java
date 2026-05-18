package com.wave808.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaylistDTO {
    private Long id;
    private String name;
    private String creatorUsername;
}
