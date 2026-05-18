package com.wave808.server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wave808.server.models.SavedPlaylist;
import com.wave808.server.models.SavedPlaylistId;

public interface SavedPlaylistRepository extends JpaRepository<SavedPlaylist, SavedPlaylistId> {
    List<SavedPlaylist> findByUserUserId(Long userId);
}