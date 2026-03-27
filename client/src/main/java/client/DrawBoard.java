package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class DrawBoard {
    
    public static String draw(ChessBoard board, String playerColor) {
        return switch(playerColor) {
            case("WHITE") -> drawWhiteBoard(board);
            case("BLACK") -> drawBlackBoard(board);
            default -> drawWhiteBoard(board);
        };
    }

    private static String drawWhiteBoard(ChessBoard board) {
        StringBuilder chessBoard = new StringBuilder();
        chessBoard.append('\n');

        chessBoard.append(SET_BG_COLOR_DARK_GREY + RESET_TEXT_COLOR + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR +  "\n");
        for (int row = 8; row >= 1; row--) {
            chessBoard.append(SET_BG_COLOR_DARK_GREY + ' ' + row + ' ');

            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));

                if ((row + col) % 2 == 0) {
                    chessBoard.append(SET_BG_COLOR_BLACK);
                } else {
                    chessBoard.append(SET_BG_COLOR_WHITE);
                }

                if (piece == null){
                    chessBoard.append("   ");
                } else {
                    chessBoard.append(addPieces(piece));
                }
            }

            chessBoard.append(SET_BG_COLOR_DARK_GREY + RESET_TEXT_COLOR + ' ' + row + ' ' + RESET_BG_COLOR + '\n');
        }

        chessBoard.append(SET_BG_COLOR_DARK_GREY + RESET_TEXT_COLOR + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + "\n");
        chessBoard.append(RESET_BG_COLOR);

        return chessBoard.toString();
    }

    private static String drawBlackBoard(ChessBoard board) {
        StringBuilder chessBoard = new StringBuilder();
        chessBoard.append('\n');

        chessBoard.append(SET_BG_COLOR_DARK_GREY + RESET_TEXT_COLOR + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR +  "\n");
        for (int row = 1; row <= 8; row++) {
            chessBoard.append(SET_BG_COLOR_DARK_GREY + ' ' + row + ' ');

            for (int col = 8; col >= 1; col--) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));

                if ((row + col) % 2 == 0) {
                    chessBoard.append(SET_BG_COLOR_BLACK);
                } else {
                    chessBoard.append(SET_BG_COLOR_WHITE);
                }

                if (piece == null){
                    chessBoard.append("   ");
                } else {
                    chessBoard.append(addPieces(piece));
                }
            }

            chessBoard.append(SET_BG_COLOR_DARK_GREY + RESET_TEXT_COLOR + ' ' + row + ' ' + RESET_BG_COLOR + '\n');
        }

        chessBoard.append(SET_BG_COLOR_DARK_GREY + RESET_TEXT_COLOR + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR + "\n");
        chessBoard.append(RESET_BG_COLOR);

        return chessBoard.toString();
    }

    private static String getPieceColor(ChessPiece piece) {
        boolean whiteTeam = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        return whiteTeam ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE;
    }

    private static char getPiece(ChessPiece piece) {
        return switch(piece.getPieceType()) {
            case KING -> 'K';
            case QUEEN -> 'Q';
            case ROOK -> 'R';
            case KNIGHT -> 'N';
            case BISHOP -> 'B';
            case PAWN -> 'P';
        };
    }

    private static String addPieces(ChessPiece piece) {
        StringBuilder pieceText = new StringBuilder();
        
        pieceText.append(getPieceColor(piece));
        pieceText.append(getPiece(piece));
        pieceText.append(RESET_TEXT_COLOR);
        pieceText.append(" ");
        pieceText.append(" ");

        return pieceText.toString();
    }

}
