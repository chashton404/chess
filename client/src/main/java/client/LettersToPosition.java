package client;

import chess.ChessPosition;

public class LettersToPosition {
    
    // We use ASCII subraction to convert from letter to number
    public static ChessPosition convert(String param) {
        int row = param.charAt(1) - '0';
        int col = param.charAt(0) - 'a' + 1;

        return new ChessPosition(row, col);
    }

}
