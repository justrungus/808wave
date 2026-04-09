package com.wave808.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wave808.server.models.Track;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long>{

}
