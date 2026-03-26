package client;

import chess.*;
import client.ChessClient;
import exception.ResponseException;

public class ClientMain {

    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        try {
            new ChessClient(serverUrl).run();
        } catch (ResponseException e) {
            e.printStackTrace();
        }
    }

}
