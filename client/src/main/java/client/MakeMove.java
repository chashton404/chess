package client;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.WebSocketFacade;
import exception.ResponseException;

public class MakeMove {
    public static String move(ChessClient client, String... params) throws ResponseException {
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
    private static Boolean isValidStart(String param) {
        return param != null && param.matches("^[a-h][1-8]$");
    }
    
    private static Boolean isValidEnd(String param) {
        return param != null && param.matches("^[a-h][1-8][nqbr]?$");
    }

    // We use ASCII subraction to convert from letter to number
    private static ChessPosition lettersToPosition(String param) {
        int row = param.charAt(1) - '0';
        int col = param.charAt(0) - 'a' + 1;

        return new ChessPosition(row, col);
    }

}
