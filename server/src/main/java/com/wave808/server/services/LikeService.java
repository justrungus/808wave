package com.wave808.server.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.wave808.server.dto.TrackDTO;
import com.wave808.server.models.LikedTrack;
import com.wave808.server.models.LikedTrackId;
import com.wave808.server.models.Track;
import com.wave808.server.models.User;
import com.wave808.server.repositories.LikedTrackRepository;
import com.wave808.server.repositories.TrackRepository;
import com.wave808.server.repositories.UserRepository;

@Service
public class LikeService {

    private final LikedTrackRepository likedTrackRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    public LikeService(LikedTrackRepository likedTrackRepository,
                        TrackRepository trackRepository,
                        UserRepository userRepository) {
        this.likedTrackRepository = likedTrackRepository;
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
    }

    public boolean toggleLike(Long userId, Long trackId) {
        LikedTrackId id = new LikedTrackId(userId, trackId);

        if (likedTrackRepository.existsById(id)) {
            likedTrackRepository.deleteById(id);
            return false;
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Track track = trackRepository.findById(trackId)
                    .orElseThrow(() -> new RuntimeException("Track not found"));
            likedTrackRepository.save(new LikedTrack(id, user, track, java.time.LocalDateTime.now()));
            return true;
        }
    }

    public List<TrackDTO> getLikedTracks(Long userId) {
        return likedTrackRepository.findByUserUserId(userId)
                .stream()
                .map(lt -> {
                    Track t = lt.getTrack();
                    return new TrackDTO(
                            t.getTrackId(),
                            t.getTitle(),
                            t.getGenre(),
                            t.getBpm(),
                            t.getMusicalKey(),
                            t.getUploader().getUsername(),
                            t.getUploader().getUserId(),
                            t.getCoverPath(),
                            t.getDescription()
                    );
                })
                .collect(Collectors.toList());
    }
}