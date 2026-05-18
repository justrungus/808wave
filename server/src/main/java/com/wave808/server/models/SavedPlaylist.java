package com.wave808.server.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_playlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedPlaylist {

    @EmbeddedId
    private SavedPlaylistId id = new SavedPlaylistId();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("playlistId")
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    @Column(name = "saved_at")
    private LocalDateTime savedAt = LocalDateTime.now();
}
