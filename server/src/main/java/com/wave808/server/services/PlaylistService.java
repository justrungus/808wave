package com.wave808.server.services;

import com.wave808.server.dto.PlaylistDTO;
import com.wave808.server.dto.TrackDTO;
import com.wave808.server.models.*;
import com.wave808.server.repositories.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final SavedPlaylistRepository savedPlaylistRepository;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;

    public PlaylistService(PlaylistRepository playlistRepository,
                            PlaylistTrackRepository playlistTrackRepository,
                            SavedPlaylistRepository savedPlaylistRepository,
                            UserRepository userRepository,
                            TrackRepository trackRepository) {
        this.playlistRepository = playlistRepository;
        this.playlistTrackRepository = playlistTrackRepository;
        this.savedPlaylistRepository = savedPlaylistRepository;
        this.userRepository = userRepository;
        this.trackRepository = trackRepository;
    }

    public PlaylistDTO createPlaylist(String name, Long userId) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setCreator(creator);
        Playlist saved = playlistRepository.save(playlist);
        return new PlaylistDTO(saved.getId(), saved.getName(), saved.getCreator().getUsername());
    }

    public void addTrackToPlaylist(Long playlistId, Long trackId) {
        PlaylistTrackId id = new PlaylistTrackId(playlistId, trackId);
        if (playlistTrackRepository.existsById(id)) return;

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        int order = playlistTrackRepository.countByPlaylistId(playlistId);
        playlistTrackRepository.save(new PlaylistTrack(id, playlist, track, order));
    }

    public void removeTrackFromPlaylist(Long playlistId, Long trackId) {
        playlistTrackRepository.deleteById(new PlaylistTrackId(playlistId, trackId));
    }

    public List<TrackDTO> getPlaylistTracks(Long playlistId) {
        return playlistTrackRepository.findByPlaylistIdOrderByPlayOrder(playlistId)
                .stream()
                .map(pt -> {
                    Track t = pt.getTrack();
                    return new TrackDTO(t.getTrackId(), t.getTitle(), t.getGenre(),
                            t.getBpm(), t.getMusicalKey(), t.getUploader().getUsername(), t.getCoverPath());
                })
                .collect(Collectors.toList());
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
