package client;

import chess.ChessBoard;

public class DrawBoard {

    String letters = "abcdefgh";
    String nums = "12345678";
    
    public static String draw(ChessBoard board, String playerColor) {
        return switch(playerColor) {
            case("WHITE") -> drawWhiteBoard(board);
            case("BLACK") -> drawBlackBoard(board);
            default -> drawWhiteBoard(board);
        };
    }

    private static String drawWhiteBoard(ChessBoard board) {
        return "Successfully drew white/observe board";
    }

    private static String drawBlackBoard(ChessBoard board) {
        return "Successfully drew black/observer board";
    }

}
