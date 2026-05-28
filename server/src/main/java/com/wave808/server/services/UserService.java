package com.wave808.server.services;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wave808.server.dto.PlaylistDTO;
import com.wave808.server.dto.ProfileDTO;
import com.wave808.server.dto.TrackDTO;
import com.wave808.server.dto.UserDTO;
import com.wave808.server.models.Track;
import com.wave808.server.models.User;
import com.wave808.server.repositories.PlaylistRepository;
import com.wave808.server.repositories.TrackRepository;
import com.wave808.server.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TrackRepository trackRepository;
    private final PlaylistRepository playlistRepository;

    @Value("${profile.picture-dir:storage/profile-pictures}")
    private String picDir;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       TrackRepository trackRepository, PlaylistRepository playlistRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.trackRepository = trackRepository;
        this.playlistRepository = playlistRepository;
    }

    public UserDTO register(String username, String email, String password) {
        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException("Username already in use");
        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("Email already in use");
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        return toDTO(userRepository.save(user));
    }

    public UserDTO login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(password, user.getPasswordHash()))
            throw new RuntimeException("Incorrect password");
        return toDTO(user);
    }

    public ProfileDTO getProfile(Long profileUserId, Long requesterId) {
        User target = userRepository.findById(profileUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isFollowing = false;
        if (requesterId != null && !requesterId.equals(profileUserId)) {
            User requester = userRepository.findById(requesterId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            isFollowing = requester.getFollowing().stream()
                    .anyMatch(u -> u.getUserId().equals(profileUserId));
        }

        List<TrackDTO> tracks = trackRepository
                .findByUploaderUserIdOrderByUploadedAtDesc(profileUserId)
                .stream().map(this::trackToDTO).collect(Collectors.toList());

        List<PlaylistDTO> playlists = playlistRepository
                .findByCreatorUserId(profileUserId)
                .stream().map(this::playlistToDTO).collect(Collectors.toList());

        return new ProfileDTO(
                target.getUserId(),
                target.getUsername(),
                target.getEmail(),
                target.getProfilePicturePath(),
                target.getFollowers().size(),
                target.getFollowing().size(),
                isFollowing,
                tracks,
                playlists
        );
    }

    @Transactional
    public void follow(Long followerId, Long followedId) {
        if (followerId.equals(followedId))
            throw new IllegalArgumentException("You cannot follow yourself");
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean alreadyFollowing = follower.getFollowing().stream()
                .anyMatch(u -> u.getUserId().equals(followedId));
        if (!alreadyFollowing) {
            follower.getFollowing().add(followed);
            userRepository.save(follower);
        }
    }

    @Transactional
    public void unfollow(Long followerId, Long followedId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        follower.getFollowing().removeIf(u -> u.getUserId().equals(followedId));
        userRepository.save(follower);
    }

    public List<UserDTO> getFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getFollowing().stream()
                .filter(followed -> followed.getFollowers().stream()
                        .anyMatch(f -> f.getUserId().equals(userId)))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Map<String, List<UserDTO>> getFriendsWithStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        java.time.LocalDateTime threshold = java.time.LocalDateTime.now().minusSeconds(60);

        List<UserDTO> online = new ArrayList<>();
        List<UserDTO> offline = new ArrayList<>();

        user.getFollowing().stream()
                .filter(followed -> followed.getFollowers().stream()
                        .anyMatch(f -> f.getUserId().equals(userId)))
                .forEach(friend -> {
                    if (friend.getLastSeen() != null && friend.getLastSeen().isAfter(threshold)) {
                        online.add(toDTO(friend));
                    } else {
                        offline.add(toDTO(friend));
                    }
                });

        return Map.of("online", online, "offline", offline);
    }

    @Transactional
    public void heartbeat(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastSeen(java.time.LocalDateTime.now());
        userRepository.save(user);
    }

    public List<TrackDTO> getFollowingFeed(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Long> followingIds = user.getFollowing().stream()
                .map(User::getUserId)
                .collect(Collectors.toList());
        if (followingIds.isEmpty()) return List.of();
        return trackRepository.findByUploaderUserIdInOrderByUploadedAtDesc(followingIds)
                .stream().map(this::trackToDTO).collect(Collectors.toList());
    }

    public List<UserDTO> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UserDTO updateProfilePicture(Long userId, MultipartFile image) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Path dir = Paths.get(picDir);
        if (!Files.exists(dir)) Files.createDirectories(dir);
        String fileName = UUID.randomUUID() + "_avatar.png";
        Path target = dir.resolve(fileName);
        BufferedImage buffered = ImageIO.read(image.getInputStream());
        if (buffered == null) throw new RuntimeException("Unsupported image format");
        ImageIO.write(buffered, "PNG", target.toFile());
        user.setProfilePicturePath(target.toString());
        return toDTO(userRepository.save(user));
    }

    private UserDTO toDTO(User u) {
        return new UserDTO(u.getUserId(), u.getUsername(), u.getEmail(), u.getProfilePicturePath());
    }

    private TrackDTO trackToDTO(Track t) {
        return new TrackDTO(t.getTrackId(), t.getTitle(), t.getGenre(), t.getBpm(),
                t.getMusicalKey(), t.getUploader().getUsername(), t.getUploader().getUserId(),
                t.getCoverPath(), t.getDescription());
    }

    private PlaylistDTO playlistToDTO(com.wave808.server.models.Playlist p) {
        return new PlaylistDTO(p.getId(), p.getName(),
                p.getCreator().getUsername(), p.getCreator().getUserId(), p.getCoverPath());
    }
}