package com.example.services;

import com.example.models.PlaylistDTO;
import com.example.models.TrackDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

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
