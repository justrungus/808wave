package com.example.services;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import com.example.models.TrackDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TrackService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String BASE_URL = "http://localhost:8080/api/tracks";

    public List<TrackDTO> getRecentTracks() throws Exception{
        return getList(BASE_URL + "/recent");
    }

    public List<TrackDTO> getTopTracks() throws Exception {
        return getList(BASE_URL + "/top");
    }

    public List<TrackDTO> getTracksByUser(Long userId) throws Exception{
        return getList(BASE_URL + "/user/" + userId);
    }

    public List<TrackDTO> getLikedTracks(Long userId) throws Exception{
        return getList(BASE_URL + "/liked/" + userId);
    }

    private List<TrackDTO> getList(String url) throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            Type listType = new TypeToken<List<TrackDTO>>() {}.getType();
            return gson.fromJson(response.body(), listType);
        } else {
            throw new Exception(response.body());
        }
    }

    public TrackDTO upload(String title, String genre,
                            Integer bpm, String musicalKey,
                            Long userId, File audioFile, File coverImage) throws Exception {

        String boundary = UUID.randomUUID().toString();

        byte[] audioBytes = Files.readAllBytes(audioFile.toPath());

        StringBuilder textParts = new StringBuilder();
        textParts.append(textPart(boundary, "title", title));
        textParts.append(textPart(boundary, "genre", genre == null ? "" : genre));   
        textParts.append(textPart(boundary, "bpm", bpm == null ? "" : bpm.toString()));
        textParts.append(textPart(boundary, "musicalKey", musicalKey == null ? "" : musicalKey));
        textParts.append(textPart(boundary, "userId", userId.toString()));

        byte[] textBytes = textParts.toString().getBytes();

        //  audio
        String audioHeader = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"audio\"; filename=\"" + audioFile.getName() + "\"\r\n" +
                "Content-Type: audio/mpeg\r\n\r\n";
        byte[] audioHeaderBytes = audioHeader.getBytes();

        //  imagen 
        byte[] coverBytes = new byte[0];
        byte[] coverHeaderBytes = new byte[0];
        if (coverImage != null) {
            coverBytes = Files.readAllBytes(coverImage.toPath());
            String coverHeader = "\r\n--" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"cover\"; filename=\"" + coverImage.getName() + "\"\r\n" +
                    "Content-Type: image/jpeg\r\n\r\n";
            coverHeaderBytes = coverHeader.getBytes();
        }

        byte[] ending = ("\r\n--" + boundary + "--\r\n").getBytes();

        // Montar el cuerpo completo
        int totalSize = textBytes.length + audioHeaderBytes.length + audioBytes.length
                + coverHeaderBytes.length + coverBytes.length + ending.length;
        byte[] fullBody = new byte[totalSize];
        int offset = 0;
        offset = copy(fullBody, textBytes, offset);
        offset = copy(fullBody, audioHeaderBytes, offset);
        offset = copy(fullBody, audioBytes, offset);
        offset = copy(fullBody, coverHeaderBytes, offset);
        offset = copy(fullBody, coverBytes, offset);
        copy(fullBody, ending, offset);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/upload"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(fullBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), TrackDTO.class);
        } else {
            throw new Exception(response.body());
        }
    }

    private String textPart(String boundary, String name, String value) {
        return "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n" +
                value + "\r\n";
    }

    private int copy(byte[] dest, byte[] src, int offset) {
        System.arraycopy(src, 0, dest, offset, src.length);
        return offset + src.length;
    }
}