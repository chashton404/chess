package server;

import com.google.gson.Gson;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import model.LoginRequest;

public class ServerFacade {
    // We create a server facade to separate the Http requests and responses from the user
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverURL;

    public ServerFacade(String url) {
        serverURL = url;
    }

    // login user
    public void loginUser(LoginRequest req) {
        var request = buildRequest(method );
        var response = sendRequest(request);
        return handleResponse(response, )
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

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
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

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            // TODO : figure out how this error part works
        }
    }

}
