package com.wave808.server.services;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wave808.server.dto.PlaylistDTO;
import com.wave808.server.dto.TrackDTO;
import com.wave808.server.models.Playlist;
import com.wave808.server.models.PlaylistTrack;
import com.wave808.server.models.PlaylistTrackId;
import com.wave808.server.models.SavedPlaylist;
import com.wave808.server.models.SavedPlaylistId;
import com.wave808.server.models.Track;
import com.wave808.server.models.User;
import com.wave808.server.repositories.PlaylistRepository;
import com.wave808.server.repositories.PlaylistTrackRepository;
import com.wave808.server.repositories.SavedPlaylistRepository;
import com.wave808.server.repositories.TrackRepository;
import com.wave808.server.repositories.UserRepository;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final SavedPlaylistRepository savedPlaylistRepository;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;

    @Value("${playlist.cover-dir:storage/playlist-covers}")
    private String coverDir;

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
        return toDTO(saved);
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

    public List<PlaylistDTO> getMyPlaylists(Long userId) {
        return playlistRepository.findByCreatorUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<PlaylistDTO> getSavedPlaylists(Long userId) {
        return savedPlaylistRepository.findByUserUserId(userId)
                .stream()
                .map(sp -> toDTO(sp.getPlaylist()))
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


    public List<PlaylistDTO> searchPlaylists(String query) {
        return playlistRepository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePlaylist(Long playlistId, Long requesterId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        if (!playlist.getCreator().getUserId().equals(requesterId)) {
            throw new RuntimeException("Only the creator can delete the playlist");
        }
        playlistTrackRepository.deleteAllByPlaylist_Id(playlistId);
        savedPlaylistRepository.deleteAllByPlaylist_Id(playlistId);
        playlistRepository.deleteById(playlistId);
    }

    public PlaylistDTO uploadCover(Long playlistId, Long requesterId, MultipartFile coverImage) throws IOException {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        if (!playlist.getCreator().getUserId().equals(requesterId)) {
            throw new RuntimeException("Only the creator can edit this playlist");
        }
        if (coverImage == null || coverImage.isEmpty()) {
            throw new RuntimeException("Cover image is empty");
        }

        Path dir = Paths.get(coverDir);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        String fileName = UUID.randomUUID() + "_cover.png";
        Path target = dir.resolve(fileName);
        BufferedImage buffered = ImageIO.read(coverImage.getInputStream());
        if (buffered == null) throw new RuntimeException("Unsupported image format");
        ImageIO.write(buffered, "PNG", target.toFile());

        playlist.setCoverPath(target.toString());
        Playlist saved = playlistRepository.save(playlist);
        return toDTO(saved);
    }

    private PlaylistDTO toDTO(Playlist p) {
        return new PlaylistDTO(
                p.getId(),
                p.getName(),
                p.getCreator().getUsername(),
                p.getCreator().getUserId(),
                p.getCoverPath()
        );
    }
}