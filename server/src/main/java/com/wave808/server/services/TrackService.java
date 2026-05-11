package com.wave808.server.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.wave808.server.models.User;
import com.wave808.server.models.Track;
import com.wave808.server.dto.TrackDTO;
import com.wave808.server.repositories.TrackRepository;
import com.wave808.server.repositories.UserRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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
        Integer bpm, String musicalKey, Long userId, 
        MultipartFile audioFile)throws IOException {
            User uploader = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID() + "_" + audioFile.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(audioFile.getInputStream(), filePath);

        Track track = new Track();
        track.setTitle(title);
        track.setGenre(genre);
        track.setAlbum(album);
        track.setBpm(bpm);
        track.setMusicalKey(musicalKey);
        track.setFilePath(filePath.toString());
        track.setUploader(uploader);

        Track saved = trackRepository.save(track);

        return new TrackDTO(
                saved.getTrackId(),
                saved.getTitle(),
                saved.getGenre(),
                saved.getAlbum(),
                saved.getBpm(),
                saved.getMusicalKey(),
                saved.getUploader().getUsername()
        );
    }

}
