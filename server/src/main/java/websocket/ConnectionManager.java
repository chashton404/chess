package websocket;

import org.eclipse.jetty.websocket.api.Session;

import chess.ChessGame;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.Notification;

import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Connection> connections = new ConcurrentHashMap<>();

    public void add(Connection connection) {
        connections.put(connection.session(), connection);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    // Command to broadcast to everyone but the root client
    public void notifyOthers(Session excludeSession, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        for (Connection connection: connections.values()) {
            Session session = connection.session();
            if (session.isOpen()) {
                if (!session.equals(excludeSession)) {
                    session.getRemote().sendString(msg);
                }
            }
        }
    }

    // Command to broadcast to only the root client
    public void notifyRoot(Session session, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        session.getRemote().sendString(msg);
    }

    // Command to broadcast to everyone
    public void notifyAll(ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        for (Connection connection: connections.values()) {
            Session session = connection.session();
            if (session.isOpen()) {
                session.getRemote().sendString(msg);
            }
            
        }
    }

    // Command to broadcast to a specific game
    public void notifyGame(int gameID, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        for (Connection connection: connections.values()) {
            Session session = connection.session();
            if (connection.gameID() == gameID && session.isOpen()) {
                session.getRemote().sendString(msg);
            }
        }
    }

    // Command to broadcast to everyon in a specific game except the root client
    public void notifyGameExceptRoot(int gameID, Session excludeSession, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        for (Connection connection : connections.values()) {
            Session session = connection.session();
            if (connection.gameID() == gameID && session.isOpen() && session != excludeSession) {
                session.getRemote().sendString(msg);
            }
        }

    }

    // Command for notifiying everyone once the board has changed
    public void notifyGameBoardChange(int gameID, ChessGame game) throws IOException {
        for (Connection connection : connections.values()) {
            Session session = connection.session();
            if (connection.gameID() == gameID && session.isOpen()) {
                // We are going to do this so we properly show the board for each player
                String playerColor = connection.playerColor() == null ? "WHITE" : connection.playerColor();
                LoadGameMessage loadGameMessage = new LoadGameMessage(game, playerColor);
                session.getRemote().sendString(loadGameMessage.toString());
            }
        }
    }

    // Command for notifying the user they are in checkmate
    public void notifyInMate(int gameID, ChessGame game, String status) throws IOException {
        for (Connection connection : connections.values()) {
            Session session = connection.session();
            String currentTeamTurn = switch(game.getTeamTurn()) {
                case WHITE -> "WHITE";
                case BLACK -> "BLACK";
            };
            if (connection.gameID() == gameID && connection.playerColor().equals(currentTeamTurn) && session.isOpen()) {
                NotificationMessage notificationMessage = new NotificationMessage(String.format("%s is in %s", connection.username(), status));
                session.getRemote().sendString(notificationMessage.toString());
            }
        }
    }
 

    
}
