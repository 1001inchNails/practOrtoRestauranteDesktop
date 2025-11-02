package com.example.practortorestaurantedesktopp.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:5000";
    private static ApiClient instance;
    private final HttpClient httpClient;

    private ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public CompletableFuture<JsonElement> get(String endpoint) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return JsonParser.parseString(response.body());
                    } else {
                        throw new RuntimeException("HTTP Error: " + response.statusCode() + " - " + response.body());
                    }
                });
    }

    public CompletableFuture<JsonElement> readMesa(String mesaId) {
        String endpoint = "/get/readmesa?mesaId=" + mesaId;
        return get(endpoint);
    }

    public CompletableFuture<JsonElement> patch(String endpoint) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return JsonParser.parseString(response.body());
                    } else {
                        throw new RuntimeException("HTTP Error: " + response.statusCode() + " - " + response.body());
                    }
                });
    }

    public CompletableFuture<JsonElement> cambiarEstadoPedido(String iddocu, String mesaId, boolean haSidoServido) {
        String endpoint = String.format("/patch/cambiarestadopedido?iddocu=%s&mesaId=%s&hasidoservido=%b",
                iddocu, mesaId, haSidoServido);
        return patch(endpoint);
    }

    public CompletableFuture<JsonElement> delete(String endpoint) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return JsonParser.parseString(response.body());
                    } else {
                        throw new RuntimeException("HTTP Error: " + response.statusCode() + " - " + response.body());
                    }
                });
    }

    public CompletableFuture<JsonElement> eliminarPedido(String iddocu, String mesaId) {
        String endpoint = String.format("/delete/deletepedido?mesaId=%s&idDocu=%s", mesaId, iddocu);
        return delete(endpoint);
    }
}
