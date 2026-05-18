package com.wave808.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.wave808.server.models.PlaylistTrack;
import com.wave808.server.models.PlaylistTrackId;

@Repository
public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, PlaylistTrackId> {
    List<PlaylistTrack> findByPlaylistIdOrderByPlayOrder(Long playlistId);
    int countByPlaylistId(Long playlistId);
}
