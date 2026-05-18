package com.wave808.server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wave808.server.models.LikedTrack;
import com.wave808.server.models.LikedTrackId;

public interface LikedTrackRepository extends JpaRepository<LikedTrack, LikedTrackId> {
    List<LikedTrack> findByUserUserId(Long userId);
    boolean existsByUserUserIdAndTrackTrackId(Long userId, Long trackId);
}
