package com.example.services;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.models.ProfileDTO;
import com.example.models.TrackDTO;
import com.example.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class UserService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String BASE_URL = "http://localhost:8080/api/users";

    public ProfileDTO getProfile(Long userId, Long requesterId) throws Exception {
        String url = BASE_URL + "/" + userId + "/profile";
        if (requesterId != null) url += "?requesterId=" + requesterId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), ProfileDTO.class);
        } else {
            throw new Exception(response.body());
        }
    }

    public void follow(Long followerId, Long followedId) throws Exception {
        String body = gson.toJson(Map.of("followerId", followerId, "followedId", followedId));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/follow"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new Exception(response.body());
    }

    public void unfollow(Long followerId, Long followedId) throws Exception {
        String body = gson.toJson(Map.of("followerId", followerId, "followedId", followedId));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/unfollow"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new Exception(response.body());
    }

    public List<User> getFriends(Long userId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + userId + "/friends"))
                .GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            Type listType = new TypeToken<List<User>>() {}.getType();
            return gson.fromJson(response.body(), listType);
        } else {
            throw new Exception(response.body());
        }
    }

    public List<TrackDTO> getFollowingFeed(Long userId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + userId + "/following-feed"))
                .GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            Type listType = new TypeToken<List<TrackDTO>>() {}.getType();
            return gson.fromJson(response.body(), listType);
        } else {
            throw new Exception(response.body());
        }
    }

    public List<User> searchUsers(String query) throws Exception {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/search?query=" + encoded))
                .GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            Type listType = new TypeToken<List<User>>() {}.getType();
            return gson.fromJson(response.body(), listType);
        } else {
            throw new Exception(response.body());
        }
    }

    public User updateProfilePicture(Long userId, File imageFile) throws Exception {
        String boundary = UUID.randomUUID().toString();
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

        String header =
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"image\"; filename=\"" + imageFile.getName() + "\"\r\n" +
                "Content-Type: image/png\r\n\r\n";
        byte[] headerBytes = header.getBytes();
        byte[] ending = ("\r\n--" + boundary + "--\r\n").getBytes();

        byte[] body = new byte[headerBytes.length + imageBytes.length + ending.length];
        System.arraycopy(headerBytes, 0, body, 0, headerBytes.length);
        System.arraycopy(imageBytes, 0, body, headerBytes.length, imageBytes.length);
        System.arraycopy(ending, 0, body, headerBytes.length + imageBytes.length, ending.length);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + userId + "/profile-picture"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), User.class);
        } else {
            throw new Exception(response.body());
        }
    }
}