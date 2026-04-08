package client;

import exception.ResponseException;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

import chess.ChessBoard;
import chess.ChessGame;

import client.websocket.NotificationHandler;

public class InGameREPL implements NotificationHandler {
    
    private final ServerFacade server;
    private final ChessClient client;

    private Boolean pendingResignation = false;

    public InGameREPL(ChessClient client, ServerFacade server) {
        this.server = server;
        this.client = client;
    }
    

    public String inGameResponses(String cmd, String[] params) throws ResponseException{
        try {
            return switch(cmd) {
                case "redraw" -> redrawBoard();
                case "leave" -> leaveGame();
                case "move" -> move(params);
                case "resign" -> resign();
                case "highlight" -> highlightMoves();
                case "yes" -> confirmResignation();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String redrawBoard() {
        ChessGame game = new ChessGame();
        return DrawBoard.drawBoard(game);
    }

    private String leaveGame() {
        // TODO: allow remove the player from the game on the server side
        client.setState(State.SIGNEDIN);
        return "Left game";
    }

    private String move(String... params) {
        // TODO: filter to the appropriate number of params and make sure they are valid
        return "moved successfully";
    }

    private String resign() {
        // TODO: make it so that the other player has won the game, update it on the server side
        pendingResignation = true;
        return "Are you sure? Type 'yes' to confirm";
    }

    private String confirmResignation() throws ResponseException{
        if (pendingResignation == true) {
            return "successfully resigned";
        } else {
            throw new ResponseException(400, "BIG BIG TROUBLE");
        }
    }

    private String highlightMoves() {
        // TODO: Get the valid moves, and change the background color on them
        return "Successfully Highlighted moves";
    }

    public String help() {
        return  SET_TEXT_COLOR_BLUE + "     redraw" + SET_TEXT_COLOR_BLACK + " - the chess board" +
                SET_TEXT_COLOR_BLUE + "\n     leave" + SET_TEXT_COLOR_BLACK + " - the game" +
                SET_TEXT_COLOR_BLUE + "\n     move <START> <END>" + SET_TEXT_COLOR_BLACK + " - to move your piece" +
                SET_TEXT_COLOR_BLUE + "\n     resign" + SET_TEXT_COLOR_BLACK + " - voluntarily lose the game" +
                SET_TEXT_COLOR_BLUE + "\n     highlight <START>" + SET_TEXT_COLOR_BLACK + " - view the valid moves for a piece";
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                DrawBoard.drawBoard(loadGameMessage.getGame());
            }
            case ERROR -> {
                ErrorMessage errorMessage = (ErrorMessage) message;
                System.out.println("Error: " + errorMessage.getErrorMessage());

            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = (NotificationMessage) message;
                System.out.println(notificationMessage.getMessage());
            }
        }
        // Print the "[GAMEPLAY] >>>"" part again
        ChessClient.printPrompt();
    }







}
