package com.wave808.server.models;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name="liked_tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikedTrack {

    @EmbeddedId
    private LikedTrackId id = new LikedTrackId();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("trackId")
    @JoinColumn(name = "track_id")
    private Track track;

    @Column(name="liked_at")
    private LocalDateTime likedAt = LocalDateTime.now();
}