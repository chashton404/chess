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
            case "WHITE" -> draw(game.getBoard(), true, startPosition, endPositions);
            case "BLACK" -> draw(game.getBoard(), false, startPosition, endPositions);
            default -> draw(game.getBoard(), true, startPosition, endPositions);
        };

    }

    private static String draw(ChessBoard board, Boolean whitePerspective, ChessPosition startPosition, Collection<ChessPosition> endPositions) {
        StringBuilder chessBoard = new StringBuilder();
        chessBoard.append('\n');

        String columns = whitePerspective ? "    a  b  c  d  e  f  g  h    " : "    h  g  f  e  d  c  b  a    ";

        chessBoard.append(SET_BG_COLOR_DARK_GREY + RESET_TEXT_COLOR + columns + RESET_BG_COLOR + "\n");
        
        int row = whitePerspective ? 8 : 1;
        int rowStep = whitePerspective ? -1 : 1;

        for (int r = 0; r < 8; r++, row += rowStep) {
            chessBoard.append(SET_BG_COLOR_DARK_GREY + ' ' + row + ' ');

            int col = whitePerspective ? 8 : 1;
            int colStep = whitePerspective ? -1 : 1;

            for (int c = 0; c < 8; c++, col += colStep) {
                ChessPosition newPos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(newPos);

                chessBoard.append(backGroundColor(row, col, newPos, startPosition, endPositions));
                chessBoard.append(piece == null ? "   " : addPieces(piece));
            }

            chessBoard.append(SET_BG_COLOR_DARK_GREY).append(RESET_TEXT_COLOR).append(' ').append(row).append(' ').append(RESET_BG_COLOR).append('\n');

        }

        chessBoard.append(SET_BG_COLOR_DARK_GREY + RESET_TEXT_COLOR + columns + RESET_BG_COLOR + "\n");
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
