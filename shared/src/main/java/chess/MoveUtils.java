package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MoveUtils {
    
    public static int checkSpotStatus(ChessBoard board, ChessGame.TeamColor teamColor, ChessPosition newPosition) {
        ChessPiece newPiece = board.getPiece(newPosition);
        if (newPiece != null) {
            if (newPiece.getTeamColor() == teamColor) {
                return 1;
            } else {
                return 2;
            }
        } else {
            return 0;
        }
    }

    public static Collection<ChessMove> diagonalMovement(ChessBoard board, ChessPosition piecePosition, ChessPiece piece) {

        /* Initalize our lists, these are the lists that we will append the moves to and wwe will iterate over the different directions */
        List<Integer> direction = List.of(-1, 1);
        List<ChessMove> pieceMoves = new ArrayList<>();

        /* Save the current position as the start position that will be used when contstructing the chess move opbject */
        ChessPosition startPosition = new ChessPosition(piecePosition.getRow(), piecePosition.getColumn());

        /* We use a nested for loop to iterate over the four diagonal locations */
        for (Integer colDirection : direction) {
            for (Integer rowDirection : direction) {

                int currRow = startPosition.getRow();
                int currCol = startPosition.getColumn();

                pieceMoves.addAll(diagonalIteration(board, startPosition, piece, colDirection, rowDirection, currRow, currCol));

            }
        }
        return pieceMoves;
    }

        

    private static Collection<ChessMove> diagonalIteration(ChessBoard board, ChessPosition startPosition, ChessPiece piece, int colDirection, int rowDirection, int currRow, int currCol) {
        Collection<ChessMove> moves = new ArrayList<>();

        /* The diagonal search label here allows us to break out of the loop when we find a piece of our own team */
        diagonalSearch:
        while ( 0 < currRow && currRow < 9 && 0 < currCol && currRow < 9 ) {
            /* Iterate in each of the directions */
            currRow += rowDirection;
            currCol += colDirection;

            if (currRow == 0 || currRow == 9) {
                break diagonalSearch;
            } else if (currCol == 0 || currCol == 9) {
                break diagonalSearch;
            }
            
            ChessMove move = new ChessMove(startPosition, new ChessPosition(currRow, currCol), null);

            /* Perform a check to verify that the the new position doesnt have the team of the same piece there */
            ChessPiece otherPiece = board.getPiece(new ChessPosition(currRow, currCol));

            if (otherPiece != null) {
                if (otherPiece.getTeamColor() != piece.getTeamColor()) {
                    /* In the case of the opponent piece being in the path, add the move as a possible move and then break the loop */
                    moves.add(move);
                    break diagonalSearch; 
                } else {
                    /* Otherwise, don't add the piece and break the loop */
                    break diagonalSearch;
                }
            } else {
                moves.add(move);
            }
        
        }

        return moves;
    }
 
}
