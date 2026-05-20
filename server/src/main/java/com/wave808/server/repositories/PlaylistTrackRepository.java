package com.wave808.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.wave808.server.models.PlaylistTrack;
import com.wave808.server.models.PlaylistTrackId;

import jakarta.transaction.Transactional;

@Repository
public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, PlaylistTrackId> {
    List<PlaylistTrack> findByPlaylistIdOrderByPlayOrder(Long playlistId);
    int countByPlaylistId(Long playlistId);

    @Modifying
    @Transactional
    void deleteAllByPlaylist_Id(Long playlistId);
}
