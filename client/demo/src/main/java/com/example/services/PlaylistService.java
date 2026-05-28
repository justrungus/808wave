package com.example.services;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.models.PlaylistDTO;
import com.example.models.TrackDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PlaylistService {
    
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String BASE_URL = "http://localhost:8080/api/playlists";

    public PlaylistDTO createPlaylist(String name, Long userId) throws Exception{
        String body = gson.toJson(Map.of("name", name, "userId", userId));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200){
            return gson.fromJson(response.body(), PlaylistDTO.class);
        }else{
            throw new Exception(response.body());
        }
    }


    public List<PlaylistDTO> searchPlaylists(String query) throws Exception {
        String encoded = java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
        return getList(BASE_URL + "/search?query=" + encoded);
    }

    public List<PlaylistDTO> getMyPlaylists(Long userId) throws Exception {
        return getList(BASE_URL + "/mine/" + userId);
    }

    public List<TrackDTO> getPlaylistTracks(Long playlistId) throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + playlistId + "/tracks"))
                .GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200){
            Type listType = new TypeToken<List<TrackDTO>>() {}.getType();
            return gson.fromJson(response.body(), listType);
        }else{
            throw new Exception(response.body());
        }
    }

    public void addTrackToPlaylist(Long playlistId, Long trackId) throws Exception{
        String body = gson.toJson(Map.of("playlistId", playlistId, "trackId", trackId));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/add-track"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new Exception(response.body());
    }

    public void removeTrackFromPlaylist(Long playlistId, Long trackId) throws Exception{
        String body = gson.toJson(Map.of("playlistId", playlistId, "trackId", trackId));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/remove-track"))
                .header("Content-Type", "application/json")
                .method("DELETE", HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new Exception(response.body());
    }

    public void deletePlaylist(Long playlistId, Long userId) throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + playlistId + "?userId="+userId))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new Exception(response.body());
    }

    public PlaylistDTO uploadCover(Long playlistId, Long userId, File coverFile) throws Exception{
        String boundary = UUID.randomUUID().toString();
        byte[] coverBytes = Files.readAllBytes(coverFile.toPath());

        String userIdPart =
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"userId\"\r\n\r\n" +
                userId.toString() + "\r\n";
        byte[] userIdBytes = userIdPart.getBytes();
        
        String coverHeader = 
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"cover\"; filename=\"" + coverFile.getName() + "\"\r\n" +
                "Content-Type: image/png\r\n\r\n";
        byte[] coverHeaderBytes = coverHeader.getBytes();

        byte[] ending = ("\r\n--" + boundary + "--\r\n").getBytes();

        int totalSize = userIdBytes.length + coverHeaderBytes.length + coverBytes.length + ending.length;
        byte[] fullBody = new byte[totalSize];
        int offset = 0;
        System.arraycopy(userIdBytes, 0, fullBody, offset, userIdBytes.length);
        offset += userIdBytes.length;
        System.arraycopy(coverHeaderBytes, 0, fullBody, offset, coverHeaderBytes.length);
        offset += coverHeaderBytes.length;
        System.arraycopy(coverBytes, 0, fullBody, offset, coverBytes.length);
        offset += coverBytes.length;
        System.arraycopy(ending, 0, fullBody, offset, ending.length);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + playlistId + "/cover"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(fullBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), PlaylistDTO.class);
        } else {
            throw new Exception(response.body());
        }
    }

    private List<PlaylistDTO> getList(String url) throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == 200){
            Type lisType = new TypeToken<List<PlaylistDTO>>() {}.getType();
            return gson.fromJson(response.body(), lisType);
        }else{
            throw new Exception(response.body());
        }
    }
}
