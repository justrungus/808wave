package com.wave808.server.repositories;

import com.wave808.server.models.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByCreatorUserId(Long userId);
    List<Playlist> findAllByOrderByCreatedAtDesc();
}