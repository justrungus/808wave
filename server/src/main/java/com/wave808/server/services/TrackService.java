package com.wave808.server.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wave808.server.dto.TrackDTO;
import com.wave808.server.models.Track;
import com.wave808.server.models.User;
import com.wave808.server.repositories.TrackRepository;
import com.wave808.server.repositories.UserRepository;

@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public TrackService(TrackRepository trackRepository, UserRepository userRepository) {
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
    }

    public TrackDTO uploadTrack(String title, String genre, String album,
                                Integer bpm, String musicalKey,
                                Long userId, MultipartFile audioFile,
                                MultipartFile coverImage) throws IOException {

        User uploader = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // audio
        String audioFileName = UUID.randomUUID() + "_" + audioFile.getOriginalFilename();
        Path audioPath = uploadPath.resolve(audioFileName);
        Files.copy(audioFile.getInputStream(), audioPath);

        // portada
        String coverFilePath = null;
        if (coverImage != null && !coverImage.isEmpty()) {
            String coverFileName = UUID.randomUUID() + "_" + coverImage.getOriginalFilename();
            Path coverPath = uploadPath.resolve(coverFileName);
            Files.copy(coverImage.getInputStream(), coverPath);
            coverFilePath = coverPath.toString();
        }

        Track track = new Track();
        track.setTitle(title);
        track.setGenre(genre);
        track.setAlbum(album);
        track.setBpm(bpm);
        track.setMusicalKey(musicalKey);
        track.setFilePath(audioPath.toString());
        track.setCoverPath(coverFilePath);
        track.setUploader(uploader);

        Track saved = trackRepository.save(track);

        return new TrackDTO(
                saved.getTrackId(),
                saved.getTitle(),
                saved.getGenre(),
                saved.getAlbum(),
                saved.getBpm(),
                saved.getMusicalKey(),
                saved.getUploader().getUsername(),
                saved.getCoverPath()
        );
    }
}