package com.wave808.server.services;

import com.wave808.server.models.Playlist;
import com.wave808.server.models.SavedPlaylist;
import com.wave808.server.models.SavedPlaylistId;
import com.wave808.server.models.User;
import com.wave808.server.repositories.PlaylistRepository;
import com.wave808.server.repositories.SavedPlaylistRepository;
import com.wave808.server.repositories.UserRepository;
import com.wave808.server.dto.PlaylistDTO;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SavedPlaylistRepository savedPlaylistRepository;
    private final UserRepository userRepository;

    public PlaylistService(PlaylistRepository playlistRepository,
                           SavedPlaylistRepository savedPlaylistRepository,
                           UserRepository userRepository) {
        this.playlistRepository = playlistRepository;
        this.savedPlaylistRepository = savedPlaylistRepository;
        this.userRepository = userRepository;
    }

    public List<PlaylistDTO> getMyPlaylists(Long userId) {
        return playlistRepository.findByCreatorUserId(userId)
                .stream()
                .map(p -> new PlaylistDTO(p.getId(), p.getName(), p.getCreator().getUsername()))
                .collect(Collectors.toList());
    }

    public List<PlaylistDTO> getSavedPlaylists(Long userId) {
        return savedPlaylistRepository.findByUserUserId(userId)
                .stream()
                .map(sp -> {
                    Playlist p = sp.getPlaylist();
                    return new PlaylistDTO(p.getId(), p.getName(), p.getCreator().getUsername());
                })
                .collect(Collectors.toList());
    }

    public void savePlaylist(Long userId, Long playlistId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        SavedPlaylistId id = new SavedPlaylistId(userId, playlistId);
        if (!savedPlaylistRepository.existsById(id)) {
            savedPlaylistRepository.save(new SavedPlaylist(id, user, playlist, java.time.LocalDateTime.now()));
        }
    }
}
