package websocket;



import com.google.gson.Gson;

import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
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
import model.GameData;
import service.AuthService;
import service.GameService;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Collection;

// Import the commands for client to server communication

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameService gameService;
    private final AuthService authService;

    public WebSocketHandler(GameService gameService, AuthService authService) {
        this.gameService = gameService;
        this.authService = authService;
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
                case MAKE_MOVE -> makeMove(new Gson().fromJson(ctx.message(), MakeMoveCommand.class), ctx.session);
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

    private void makeMove(MakeMoveCommand command, Session session) throws IOException, InvalidMoveException, 
        DataAccessException, BadRequestException, UnauthorizedException {

        String authToken = command.getAuthToken();
        Integer gameID = command.getGameID();
        ChessMove newMove = command.getMove();

        GameData gameData = gameService.getGameData(authToken, gameID);
        ChessGame game = gameData.game();

        String username = authService.getUsername(authToken);

        // Verify that the move is valid (that it is in the list of valid moves for the starting position)
        ChessPosition startPosition = newMove.getStartPosition();
        Collection<ChessMove> validMoves = game.validMoves(startPosition);

        if (validMoves.contains(newMove)) {
            // Update the game by making the move
            game.makeMove(newMove);
            gameData.updateGame(game);

            // send LOAD_GAME to every client
            // TODO: change this so it draws the right color
            LoadGameMessage loadGameMessage = new LoadGameMessage(game, "WHITE");
            connections.notifyAll(loadGameMessage);

            // Notify others of move
            String message = buildMoveString(username, newMove);
            NotificationMessage notificationMessage = new NotificationMessage(message);
            connections.notifyOthers(session, notificationMessage);

            // Notify other of check and checkmate

        }

    }

    private String buildMoveString(String username, ChessMove move) {
        StringBuilder message = new StringBuilder();

        message.append(username).append( " moved ");
        message.append(moveToLetters(move));

        if (move.getPromotionPiece() != null) {
            message.append(" promoting to ");
            String pieceType = switch(move.getPromotionPiece()) {
                case QUEEN -> "queen";
                case KNIGHT -> "knight";
                case ROOK -> "rook";
                case BISHOP -> "bishop";
                default -> "unknown";
            };
            message.append(pieceType);
        }
        return message.toString();
    }

    private String moveToLetters(ChessMove move) {
        char startCol = (char) ('a' + move.getStartPosition().getColumn() - 1);
        int startRow = move.getStartPosition().getRow();
        String startPosition = "" + startCol + startRow;

        char endCol = (char) ('a' + move.getEndPosition().getColumn() - 1);
        int endRow = move.getEndPosition().getRow();
        String endPosition = "" + endCol + endRow;

        return startPosition + " to " + endPosition;
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket Closed");
    }
}

