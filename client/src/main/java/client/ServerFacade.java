package client;

import com.google.gson.Gson;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import model.LoginRequest;
import model.AuthData;
import model.RegisterRequest;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.ListGamesResult;
import model.JoinGameRequest;

import exception.ResponseException;

public class ServerFacade {
    // We create a server facade to separate the Http requests and responses from the user
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverURL;

    public ServerFacade(String url) {
        serverURL = url;
    }

    // login user
    public AuthData loginUser(LoginRequest req) throws ResponseException {
        var request = buildRequest("POST", "/session", req, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }


    // register user
    public AuthData registerUser(RegisterRequest req) throws ResponseException {
        var request = buildRequest("POST", "/user", req, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }


    // logout user
    public void logoutUser(String authToken) throws ResponseException {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }


    // create game
    public CreateGameResult createGame(CreateGameRequest req, String authToken) throws ResponseException {
        var request = buildRequest("POST", "/game", req, authToken);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }


    // list game
    public ListGamesResult listGames(String authToken) throws ResponseException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }


    // join game (as player)
    public void joinGame(JoinGameRequest req, String authToken) throws ResponseException {
        var request = buildRequest("PUT", "/game", req, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    // clear database (for testing purposes)
    public void clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }

        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }

        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage() != null ? ex.getMessage() : ex.toString());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        int errorCode = 500;
        if (status >= 400 && status <= 499) {
            errorCode = 400;
        }
        if (status >= 500 && status <=599) {
            errorCode = 500;
        }
         if (!isSuccessful(status)) {
            String errorMessage = extractErrorMessage(response.body());
            throw new ResponseException(status, "Error " + errorCode + ": " + errorMessage); 
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    private String extractErrorMessage(String responseBody) {
        try {
            if (responseBody == null || responseBody.isBlank()) {
                return "Unknown Error";
            }

            var jsonObject = new Gson().fromJson(responseBody, com.google.gson.JsonObject.class);

            if (jsonObject.has("message")) {
                String errorMessage = jsonObject.get("message").getAsString();
                return errorMessage.replace("Error: ", "");
            }

            return responseBody;
        } catch (Exception e) {
            return responseBody;
        }
    }

}
