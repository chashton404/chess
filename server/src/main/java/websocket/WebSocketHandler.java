package websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import model.ConnectionResult;
import service.GameService;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

// Import the commands for client to server communication

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameService gameService;

    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket Connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, ctx.session);
                case MAKE_MOVE -> System.out.println("move made");
                case LEAVE -> System.out.println("left game");
                case RESIGN -> System.out.println("resigned");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        // Verify the authToken, and the gameID
        String authToken = command.getAuthToken();
        Integer gameID = command.getGameID();

        try {
            // attempt to get the Connection Result
            ConnectionResult connection = gameService.connectGame(authToken, gameID);

            // Create the connection - for both players and observers
            connections.add(session);

            // Send LOAD_GAME to the root client - this also works for observers
            if (connection.playerColor() != null) {
                // In the case of the root client being a player
                LoadGameMessage loadGameMessage = new LoadGameMessage(connection.game(), connection.playerColor());
                connections.notifyRoot(session, loadGameMessage);

                String message = String.format("%s has joined the game as %s", connection.username(), connection.playerColor());
                NotificationMessage notificationMessage = new NotificationMessage(message);
                connections.notifyOthers(session, notificationMessage);
            } else {
                // In the case of the root client being an observer
                LoadGameMessage loadGameMessage = new LoadGameMessage(connection.game(), "WHITE");
                connections.notifyRoot(session, loadGameMessage);

                String message = String.format("%s has joined the game as an observer", connection.username());
                NotificationMessage notificationMessage = new NotificationMessage(message);
                connections.notifyOthers(session, notificationMessage);
            }

        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
            connections.notifyRoot(session, errorMessage);
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket Closed");
    }
}

