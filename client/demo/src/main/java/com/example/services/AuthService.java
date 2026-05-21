package com.example.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.example.models.User;
import com.google.gson.Gson;

// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;


public class AuthService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String BASE_URL = "http://localhost:8080/api/auth";


    public User register(String username, String email, String password) throws Exception {
        // 1. Preparamos los datos en formato JSON
        Map<String, String> data = Map.of(
            "username", username,
            "email", email,
            "password", password
        );
        String jsonBody = gson.toJson(data);

        // 2. Creamos la petición al servidor
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // 3. Enviamos y esperamos respuesta
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 4. Si el servidor dice OK (200), leemos el nombre de usuario y lo devolvemos
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), User.class);
        } else {
            
            throw new Exception(response.body());
        }
    }

    public User login(String username, String password) throws Exception{
        Map<String, String> data = Map.of(
            "username", username,
            "password", password
        );
        String jsonBody = gson.toJson(data);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(java.net.URI.create(BASE_URL + "/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), User.class);
        } else {
            throw new Exception(response.body());
        }
    }
}
