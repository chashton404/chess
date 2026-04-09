package client;

import exception.ResponseException;
import passoff.websocket.WebsocketTestingEnvironment;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;

public class InGameREPL implements NotificationHandler {
    
    // Initialize the necessary things
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
                case "move" -> MakeMove.move(client, params);
                case "resign" -> resign();
                case "highlight" -> highlightMoves(params);
                case "yes" -> confirmResignation();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String redrawBoard() {
        return DrawBoard.drawBoard(client.getLocalGame(), client.getLocalColor(), null, null);
    }

    private String leaveGame() throws ResponseException {
        
        WebSocketFacade ws = client.getWebSocket();
        // How to get the gameID?
        ws.leave(client.getAuthToken(), client.getLocalGameID());

        client.setState(State.SIGNEDIN);
        return "Left game";
    }

    
    private String resign() {
        // TODO: make it so that the other player has won the game, update it on the server side
        System.out.println(client.getState());
        if (client.getState() == State.OBSERVER) {
            return "Observers cannot resign";
        } else {
            pendingResignation = true;
            return "Are you sure? Type 'yes' to confirm";
        }

    }

    private String confirmResignation() throws ResponseException{
        if (pendingResignation == true) {
            return "successfully resigned";
        } else {
            throw new ResponseException(400, "BIG BIG TROUBLE");
        }
    }

    private String highlightMoves(String... params) throws ResponseException {
        if (params.length !=1 || !params[0].matches("^[a-h][1-8]")) {
            throw new ResponseException(400, "Expected: highlight <START>");
        }

        ChessPosition startPosition = LettersToPosition.convert(params[0].toLowerCase());

        var game = client.getLocalGame();
        ChessPiece piece = game.getBoard().getPiece(startPosition);

        if (piece == null) {
            throw new ResponseException(400, String.format("Okay that's silly there isn't even a piece at %s", params[0]));
        }

        Collection<ChessMove> validMoves = game.validMoves(startPosition);
        Collection<ChessPosition> endPositions = new ArrayList<>();

        for (ChessMove move: validMoves) {
            endPositions.add(move.getEndPosition());
        }

        return DrawBoard.drawBoard(game, client.getLocalColor(), startPosition, endPositions);
    }

    public String help() {
        return  SET_TEXT_COLOR_BLUE + "     redraw" + SET_TEXT_COLOR_BLACK + " - the chess board" +
                SET_TEXT_COLOR_BLUE + "\n     leave" + SET_TEXT_COLOR_BLACK + " - the game" +
                SET_TEXT_COLOR_BLUE + "\n     move <[START_LETTER][START_NUM]> <[END_LETTER][END_NUM]>" + SET_TEXT_COLOR_BLACK + " - to move your piece" +
                SET_TEXT_COLOR_BLUE + "\n     resign" + SET_TEXT_COLOR_BLACK + " - voluntarily lose the game" +
                SET_TEXT_COLOR_BLUE + "\n     highlight <START>" + SET_TEXT_COLOR_BLACK + " - view the valid moves for a piece";
    }

}
