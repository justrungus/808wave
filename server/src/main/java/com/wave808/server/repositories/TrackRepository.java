package com.wave808.server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wave808.server.models.Track;

public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> findByUploaderUserIdOrderByUploadedAtDesc(Long userId);
    List<Track> findAllByOrderByUploadedAtDesc();
    List<Track> findAllByOrderByPlayCountDesc();
    List<Track> findByUploaderUserIdInOrderByUploadedAtDesc(List<Long> userIds);
    List<Track> findByTitleContainingIgnoreCase(String query);
}