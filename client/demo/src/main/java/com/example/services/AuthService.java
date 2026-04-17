package com.example.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.google.gson.JsonObject;

// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;


public class AuthService {
    private final HttpClient client = HttpClient.newHttpClient();

    private final String BASE_URL = "http://localhost:8080/api/auth";


    public String register(String username, String email, String password) throws Exception {
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
            JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
            return jsonObject.get("username").getAsString();
        } else {
            // Si el servidor da error (ej. "El email ya existe"), explotamos con ese mensaje
            throw new Exception(response.body());
        }
    }
}
