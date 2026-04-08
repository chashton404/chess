package websocket;

import com.google.gson.Gson;

import chess.ChessMove;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import websocket.commands.MakeMoveCommand;
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
                case LEAVE -> leave(command, ctx.session);
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

    private void leave(UserGameCommand command, Session session) throws IOException {
        
        String authToken = command.getAuthToken();
        Integer gameID = command.getGameID();

        try {
            // Attempt to leave the game
            String username = gameService.leaveGame(authToken, gameID);

            // Update the connection after having left the game
            connections.remove(session);

            // Notify the others that the player has left
            String message = String.format("%s has left the game", username);
            NotificationMessage notificationMessage = new NotificationMessage(message);
            connections.notifyOthers(session, notificationMessage);


        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
            connections.notifyRoot(session, errorMessage);
        }
    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException {

        String authToken = command.getAuthToken();
        Integer gameID = command.getGameID();
        ChessMove move = command.getMove();

        // Verify that the move is valid (that it is in the list of valid moves for the starting position)
        

        // Update the game so that piece is moved

        // send LOAD_GAME to all every client


        // Notify others of move


        // Notify if in check, checkmate


        
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket Closed");
    }
}

