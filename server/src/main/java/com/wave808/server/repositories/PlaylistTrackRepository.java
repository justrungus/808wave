package com.wave808.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wave808.server.models.PlaylistTrack;
import com.wave808.server.models.PlaylistTrackId;

@Repository
public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, PlaylistTrackId> {
}
