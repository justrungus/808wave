package com.example.services;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.UUID;

import com.example.models.TrackDTO;
import com.google.gson.Gson;

public class TrackService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String BASE_URL = "http://localhost:8080/api/tracks";

    public TrackDTO upload(String title, String genre, String album,
                           Integer bpm, String musicalKey,
                           Long userId, File audioFile) throws Exception {

        String boundary = UUID.randomUUID().toString();

        byte[] audioBytes = Files.readAllBytes(audioFile.toPath());

        String body = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"title\"\r\n\r\n" + title + "\r\n" +
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"genre\"\r\n\r\n" + genre + "\r\n" +
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"album\"\r\n\r\n" + album + "\r\n" +
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"bpm\"\r\n\r\n" + bpm + "\r\n" +
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"musicalKey\"\r\n\r\n" + musicalKey + "\r\n" +
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"userId\"\r\n\r\n" + userId + "\r\n";

        byte[] bodyStart = body.getBytes();
        String fileHeader = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"audio\"; filename=\"" + audioFile.getName() + "\"\r\n" +
                "Content-Type: audio/mpeg\r\n\r\n";
        byte[] fileHeaderBytes = fileHeader.getBytes();
        String bodyEnd = "\r\n--" + boundary + "--\r\n";
        byte[] bodyEndBytes = bodyEnd.getBytes();

        byte[] fullBody = new byte[bodyStart.length + fileHeaderBytes.length + audioBytes.length + bodyEndBytes.length];
        System.arraycopy(bodyStart, 0, fullBody, 0, bodyStart.length);
        System.arraycopy(fileHeaderBytes, 0, fullBody, bodyStart.length, fileHeaderBytes.length);
        System.arraycopy(audioBytes, 0, fullBody, bodyStart.length + fileHeaderBytes.length, audioBytes.length);
        System.arraycopy(bodyEndBytes, 0, fullBody, bodyStart.length + fileHeaderBytes.length + audioBytes.length, bodyEndBytes.length);

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
}