package com.wave808.server.services;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletResponse;

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

    public TrackDTO uploadTrack(String title, String genre,
                                Integer bpm, String musicalKey,
                                String description,
                                Long userId, MultipartFile audioFile,
                                MultipartFile coverImage) throws IOException {

        User uploader = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        String audioFileName = UUID.randomUUID() + "_" + audioFile.getOriginalFilename().replace(" ", "_");
        Path audioPath = uploadPath.resolve(audioFileName);
        Files.copy(audioFile.getInputStream(), audioPath);

        String coverFilePath = null;
        if (coverImage != null && !coverImage.isEmpty()) {
            String coverFileName = UUID.randomUUID() + "_cover.png";
            Path coverPath = uploadPath.resolve(coverFileName);
            BufferedImage buffered = ImageIO.read(coverImage.getInputStream());
            ImageIO.write(buffered, "PNG", coverPath.toFile());
            coverFilePath = coverPath.toString();
        }

        Track track = new Track();
        track.setTitle(title);
        track.setGenre(genre);
        track.setBpm(bpm);
        track.setMusicalKey(musicalKey);
        track.setDescription(description);
        track.setFilePath(audioPath.toString());
        track.setCoverPath(coverFilePath);
        track.setUploader(uploader);

        return toDTO(trackRepository.save(track));
    }

    public void streamTrack(Long trackId, String rangeHeader, HttpServletResponse response) throws IOException {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        Path filePath = Paths.get(track.getFilePath());
        if (!Files.exists(filePath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Audio file not found");
            return;
        }

        long fileSize = Files.size(filePath);
        String fileName = filePath.getFileName().toString().toLowerCase();
        String contentType = fileName.endsWith(".mp3") ? "audio/mpeg" : "audio/wav";

        response.setContentType(contentType);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Cache-Control", "no-cache");

        track.setPlayCount(track.getPlayCount() + 1);
        trackRepository.save(track);

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            long start = Long.parseLong(ranges[0].trim());
            long end = (ranges.length > 1 && !ranges[1].trim().isEmpty())
                    ? Long.parseLong(ranges[1].trim()) : fileSize - 1;
            end = Math.min(end, fileSize - 1);
            long length = end - start + 1;

            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
            response.setContentLengthLong(length);

            try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r")) {
                raf.seek(start);
                byte[] buffer = new byte[8192];
                long remaining = length;
                while (remaining > 0) {
                    int toRead = (int) Math.min(buffer.length, remaining);
                    int bytesRead = raf.read(buffer, 0, toRead);
                    if (bytesRead == -1) break;
                    response.getOutputStream().write(buffer, 0, bytesRead);
                    remaining -= bytesRead;
                }
            }
        } else {
            response.setContentLengthLong(fileSize);
            Files.copy(filePath, response.getOutputStream());
        }
        response.getOutputStream().flush();
    }

    public List<TrackDTO> getRecentTracks() {
        return trackRepository.findAllByOrderByUploadedAtDesc()
                .stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
    }

    public List<TrackDTO> getTopTracks() {
        return trackRepository.findAllByOrderByPlayCountDesc()
                .stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
    }

    public List<TrackDTO> getTracksByUser(Long userId) {
        return trackRepository.findByUploaderUserIdOrderByUploadedAtDesc(userId)
                .stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
    }

    public List<TrackDTO> searchTracks(String query) {
        return trackRepository.findByTitleContainingIgnoreCase(query)
                .stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
    }

    private TrackDTO toDTO(Track t) {
        return new TrackDTO(
                t.getTrackId(), t.getTitle(), t.getGenre(), t.getBpm(),
                t.getMusicalKey(), t.getUploader().getUsername(),
                t.getUploader().getUserId(), t.getCoverPath(), t.getDescription()
        );
    }
}