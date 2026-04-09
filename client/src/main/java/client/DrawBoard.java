package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

import static ui.EscapeSequences.*;

public class DrawBoard {

    public static String drawBoard(ChessGame game, String playerColor, ChessPosition startPosition, Collection<ChessPosition> endPositions) {
        return switch(playerColor) {
            case "WHITE" -> drawWhiteBoard(game.getBoard(), startPosition, endPositions);
            case "BLACK" -> drawBlackBoard(game.getBoard(), startPosition, endPositions);
            default -> drawWhiteBoard(game.getBoard(), startPosition, endPositions);
        };

    }

    private static String drawWhiteBoard(ChessBoard board, ChessPosition startPosition, Collection<ChessPosition> endPositions) {
        StringBuilder chessBoard = new StringBuilder();
        chessBoard.append('\n');

        chessBoard.append(SET_BG_COLOR_DARK_GREY + RESET_TEXT_COLOR + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR +  "\n");
        for (int row = 8; row >= 1; row--) {
            chessBoard.append(SET_BG_COLOR_DARK_GREY + ' ' + row + ' ');

            for (int col = 1; col <= 8; col++) {

                // This is where we are going to make the WHITE chessboard
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));

                // Append change the background on the WHITE chessboard with this method
                chessBoard.append(backGroundColor(row, col, new ChessPosition(row, col), startPosition, endPositions));

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

    private static String drawBlackBoard(ChessBoard board, ChessPosition startPosition, Collection<ChessPosition> endPositions) {
        StringBuilder chessBoard = new StringBuilder();
        chessBoard.append('\n');

        chessBoard.append(SET_BG_COLOR_DARK_GREY + RESET_TEXT_COLOR + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR +  "\n");
        for (int row = 1; row <= 8; row++) {
            chessBoard.append(SET_BG_COLOR_DARK_GREY + ' ' + row + ' ');

            for (int col = 8; col >= 1; col--) {

                // This is where we are going to make the WHITE chessboard
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));

                // Append change the background on the WHITE chessboard with this method
                chessBoard.append(backGroundColor(row, col, new ChessPosition(row, col), startPosition, endPositions));

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

    private static String backGroundColor(int row, int col, ChessPosition curr, 
                                          ChessPosition start, Collection<ChessPosition> endPositions) {
        if (start != null && start.equals(curr)) {
            return SET_BG_COLOR_YELLOW;
        }
        if (endPositions != null && endPositions.contains(curr)) {
            return SET_BG_COLOR_GREEN;
        }
        return ((row + col) % 2 == 0) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
    } 
    
    private static String assembleInterior()

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
        
        pieceText.append(" ");
        pieceText.append(getPieceColor(piece));
        pieceText.append(getPiece(piece));
        pieceText.append(RESET_TEXT_COLOR);
        pieceText.append(" ");
    

        return pieceText.toString();
    }

}
