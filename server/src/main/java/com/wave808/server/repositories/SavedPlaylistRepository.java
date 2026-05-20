package com.wave808.server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.wave808.server.models.SavedPlaylist;
import com.wave808.server.models.SavedPlaylistId;

import jakarta.transaction.Transactional;

public interface SavedPlaylistRepository extends JpaRepository<SavedPlaylist, SavedPlaylistId> {
    List<SavedPlaylist> findByUserUserId(Long userId);

    @Modifying
    @Transactional
    void deleteAllByPlaylist_Id(Long playlistId);
}