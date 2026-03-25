package server;

import com.google.gson.Gson;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import model.LoginRequest;
import model.AuthData;

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


    // logout user


    // create game


    // list game


    // join game (as player)


    // public Pet addPet(Pet pet) throws ResponseException {
    //     var request = buildRequest("POST", "/pet", pet);
    //     var response = sendRequest(request);
    //     return handleResponse(response, Pet.class);
    // }

    // public void deletePet(int id) throws ResponseException {
    //     var path = String.format("/pet/%s", id);
    //     var request = buildRequest("DELETE", path, null);
    //     var response = sendRequest(request);
    //     handleResponse(response, null);
    // }

    // public void deleteAllPets() throws ResponseException {
    //     var request = buildRequest("DELETE", "/pet", null);
    //     sendRequest(request);
    // }

    // public PetList listPets() throws ResponseException {
    //     var request = buildRequest("GET", "/pet", null);
    //     var response = sendRequest(request);
    //     return handleResponse(response, PetList.class);
    // }

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
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "Error" + status + ": " + response.body()); 
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
