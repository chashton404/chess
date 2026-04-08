package client.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    // This is the silent "listener"
    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            // Crete the url
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            // create the WebSocket session
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    // When we receive a message these are the things that should be done
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(400, ex.getMessage());
        }
    }

    // The thing the endpoint requires but we don't have to do anything with
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    // OUTGOING METHODS
    // Method for the CONNECT Command
    public void connect(String authToken, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    // Method for MAKE_MOVE command

    // Method for LEAVE command

    // Method for RESIGN command

    




    
}
