package com.wave808.server.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

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

    public TrackDTO uploadTrack(String title, String genre,
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
        String audioFileName = UUID.randomUUID() + "_" + audioFile.getOriginalFilename().replace(" ", "_");
        Path audioPath = uploadPath.resolve(audioFileName);
        Files.copy(audioFile.getInputStream(), audioPath);

        // portada
        String coverFilePath = null;
        if (coverImage != null && !coverImage.isEmpty()) {
            String coverFileName = UUID.randomUUID() + "_cover.png";
            Path coverPath = uploadPath.resolve(coverFileName);
            
            // Convertir a PNG independientemente del formato original
            BufferedImage buffered = ImageIO.read(coverImage.getInputStream());
            ImageIO.write(buffered, "PNG", coverPath.toFile());
            
            coverFilePath = coverPath.toString();
        }

        Track track = new Track();
        track.setTitle(title);
        track.setGenre(genre);
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
                saved.getBpm(),
                saved.getMusicalKey(),
                saved.getUploader().getUsername(),
                saved.getCoverPath()
        );
    }

    public List<TrackDTO> getRecentTracks() {
        return trackRepository.findAllByOrderByUploadedAtDesc()
                .stream()
                .map(this::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<TrackDTO> getTopTracks() {
        return trackRepository.findAllByOrderByPlayCountDesc()
                .stream()
                .map(this::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<TrackDTO> getTracksByUser(Long userId) {
        return trackRepository.findByUploaderUserIdOrderByUploadedAtDesc(userId)
                .stream()
                .map(this::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    private TrackDTO toDTO(Track t) {
        return new TrackDTO(
                t.getTrackId(),
                t.getTitle(),
                t.getGenre(),
                t.getBpm(),
                t.getMusicalKey(),
                t.getUploader().getUsername(),
                t.getCoverPath()
        );
    }
}