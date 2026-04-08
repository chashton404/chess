package client;

import exception.ResponseException;
import passoff.websocket.WebsocketTestingEnvironment;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;

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
        return DrawBoard.drawBoard(client.getLocalGame(), client.getLocalColor());
    }

    private String leaveGame() throws ResponseException {
        
        WebSocketFacade ws = client.getWebSocket();
        // How to get the gameID?
        ws.leave(client.getAuthToken(), client.getLocalGameID());

        client.setState(State.SIGNEDIN);
        return "Left game";
    }

    private String move(String... params) throws ResponseException {
        if (client.getState() != State.INGAME) {
            return "Observers cannot make moves";
        }
        

        if (params.length == 2) {
            String start = params[0].toLowerCase();
            String end = params[1].toLowerCase();

            // Verify that they are each valid moves okay so the problem with this is we don't know if they are promoting their piece or not
            if (!isValidStart(start) || !isValidEnd(end)) {
                throw new ResponseException(400, "Expected: <[START_LETTER][START_NUM]> <[END_LETTER][END_NUM][PROM_LETTER]> Ex: h4 h5 or a7 a8q");
            }
            
            // Now we need to convert the letters into a chessmove and pass that into the websocket call

            if (end.length() == 2) {
                // In this case that we don't need to handle a promotion, just leave it null
                ChessMove newMove = new ChessMove(lettersToPosition(start), lettersToPosition(end), null);

                // Call on the websocket
                WebSocketFacade ws = client.getWebSocket();
                ws.move(client.getAuthToken(), client.getLocalGameID(), newMove);

                return String.format("Move from %s to %s", start, end);

            } else {
                // In this case we do need to handle a promotion so we include that
                ChessPiece.PieceType promotionPiece;
                String promotionString;

                // Create the promotion piece based on the third character
                switch(end.charAt(2)) {
                    case 'q' -> {promotionPiece = ChessPiece.PieceType.QUEEN; promotionString = "queen";}
                    case 'n' -> {promotionPiece = ChessPiece.PieceType.KNIGHT; promotionString = "knight";}
                    case 'r' -> {promotionPiece = ChessPiece.PieceType.ROOK; promotionString = "rook";}
                    case 'b' -> {promotionPiece = ChessPiece.PieceType.BISHOP; promotionString = "bishop";}
                    default -> throw new ResponseException(400, "Invalid promotion must be 'q'-queen 'n'-knight 'r'-rook 'b'-bishop");
                }

                // Create the move
                ChessMove newMove = new ChessMove(lettersToPosition(start), lettersToPosition(end), promotionPiece);

                // Call on the websocket
                WebSocketFacade ws = client.getWebSocket();
                ws.move(client.getAuthToken(), client.getLocalGameID(), newMove);

                return String.format("Move from %s to %s with promotion to %s", start, end.substring(0,2), promotionString);
            }
        }

        throw new ResponseException(400, "Expected: <[START_LETTER][START_NUM]> <[END_LETTER][END_NUM][PROM_LETTER]> Ex: h4 h5");
    }

    // We use some regex to verify that each of the parameters are valid moves
    private Boolean isValidStart(String param) {
        return param != null && param.matches("^[a-h][1-8]$");
    }
    
    private Boolean isValidEnd(String param) {
        return param != null && param.matches("^[a-h][1-8][nqbr]?$");
    }

    // We use ASCII subraction to convert from letter to number
    private ChessPosition lettersToPosition(String param) {
        int row = param.charAt(1);
        int col = param.charAt(0) - 'a' + 1;

        return new ChessPosition(row, col);
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

    private String highlightMoves() {
        // TODO: Get the valid moves, and change the background color on them
        return "Successfully Highlighted moves";
    }

    public String help() {
        return  SET_TEXT_COLOR_BLUE + "     redraw" + SET_TEXT_COLOR_BLACK + " - the chess board" +
                SET_TEXT_COLOR_BLUE + "\n     leave" + SET_TEXT_COLOR_BLACK + " - the game" +
                SET_TEXT_COLOR_BLUE + "\n     move <[START_LETTER][START_NUM]> <[END_LETTER][END_NUM]>" + SET_TEXT_COLOR_BLACK + " - to move your piece" +
                SET_TEXT_COLOR_BLUE + "\n     resign" + SET_TEXT_COLOR_BLACK + " - voluntarily lose the game" +
                SET_TEXT_COLOR_BLUE + "\n     highlight <START>" + SET_TEXT_COLOR_BLACK + " - view the valid moves for a piece";
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                System.out.println(DrawBoard.drawBoard(loadGameMessage.getGame(), loadGameMessage.getPlayerColor()));
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
