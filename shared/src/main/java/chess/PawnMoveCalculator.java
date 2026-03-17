package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

public class PawnMoveCalculator {

    public static Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        
        List<ChessMove> moves = new ArrayList<>();
        int direction;

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            /* Implement the logic for if the pawn is white */
            direction = 1;
        } else {
            /* Implement the logic for if the pawn is black */
            direction = -1;
        }

        /* Get the forward moves for the Pawn */
        moves.addAll(pawnForwardMoves(board, myPosition, piece, direction));
        
        /* Add the logic for the diagonal moves for the pawns */
        /* Create the column movement directions that we will loop over */
        moves.addAll(pawnDiagonalMoves(board, myPosition, piece, direction));

        return moves;
    }

    private static Collection<ChessMove> pawnForwardMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece, int direction) {       
        /* Initialize the moves and start position */
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition startPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

        /* Initialize positions */
        int currRow = startPosition.getRow();
        int currCol = startPosition.getColumn();
        int startRow = startPosition.getRow();

        /* Look at the space in front of the pawn */
        currRow += direction;
        ChessPosition inFront = new ChessPosition(currRow, myPosition.getColumn());

        /* Check the Status of the piece in front */
        int inFrontStatus;
        if (currRow > 0 && currRow < 9) {
            inFrontStatus = MoveUtils.checkSpotStatus(board, piece.getTeamColor(), inFront);
        } else {
            inFrontStatus = -1;
        }

        /* Work with the different cases of what is in front */
        if (inFrontStatus == 0) {
            /* Spot is empty, this is the only situation in which the pawn can move forward
            we now see if it's the first position or the last position */
            int start = -1;
            int end = -1;
            
            switch(piece.getTeamColor()) {
                case WHITE -> {start = 2; end = 7; }
                case BLACK -> {start = 7; end = 2; }
            };
    
            if (startRow == start) {
                /* This is to say that the current team is on the start.
                Add the move like normal
                then check the position ahead to see if the pawn is able to move forward again */
                moves.add(new ChessMove(startPosition, inFront, null));
    
                currRow += direction;
                ChessPosition farFront = new ChessPosition(currRow, currCol);
    
                int farFrontStatus = MoveUtils.checkSpotStatus(board, piece.getTeamColor(), farFront);
    
                moves.addAll(addFarMove(farFrontStatus, startPosition, farFront));
    
            } else if (startRow == end) {
                /* It's the second to last position, the player is moving into a spot where they are able to promote their piece
                accept a promotion */
                moves.add(new ChessMove(startPosition, inFront, PieceType.QUEEN));
                moves.add(new ChessMove(startPosition, inFront, PieceType.BISHOP));
                moves.add(new ChessMove(startPosition, inFront, PieceType.ROOK));
                moves.add(new ChessMove(startPosition, inFront, PieceType.KNIGHT));
    
            } else {
                /* it's not the first or the last position, just add the move like normal */
                /* but also check to make sure that the move is a valid move */
                moves.add(new ChessMove(startPosition, inFront, null));
            }
    
        }

        return moves;
    }

    private static Collection<ChessMove> addFarMove(int farFrontStatus, ChessPosition startPosition, ChessPosition farFront){
        Collection<ChessMove> moves = new ArrayList<>();
        if (farFrontStatus == 0) {
            /* the spot in front of the pawn is available, add it to the moves */
            moves.add(new ChessMove(startPosition, farFront, null));
        }
        return moves;
    }

    private static Collection<ChessMove> pawnDiagonalMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece, int direction) {
        /* Initialize lists */
        ChessPosition startPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        Collection<ChessMove> moves = new ArrayList<>();
        List<Integer> colDirection = List.of(-1,1);

        /* Initialize positions */
        int currRow = startPosition.getRow();
        int currCol = startPosition.getColumn();
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();

        for (Integer colMove : colDirection){
            /* Check the diagonal by moving forward 1 row and then incrementing by the column as well */
            currRow = startRow;
            currCol = startCol;

            currRow += direction;
            currCol += colMove;
            
            ChessPosition diagonal = new ChessPosition(currRow, currCol);

            int diagStatus;
            if (0 < currRow && currRow < 9 && 0 < currCol && currCol < 9){
                diagStatus = MoveUtils.checkSpotStatus(board, piece.getTeamColor(), diagonal);
            } else {
                diagStatus = -1;
            }

            
            int end = -1;
            switch(piece.getTeamColor()) {
                case WHITE -> end = 7;
                case BLACK -> end = 2;
            }

            
            if (diagStatus == 2) {
                if (startRow == end) {
                    moves.addAll(addPromotionPieces(startPosition, diagonal));
                } else {
                    moves.add(new ChessMove(startPosition, diagonal, null));
                }
            }
        }
        
        return moves; 
    }

    private static Collection<ChessMove> addPromotionPieces(ChessPosition startPosition, ChessPosition endPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        moves.add(new ChessMove(startPosition, endPosition, PieceType.QUEEN));
        moves.add(new ChessMove(startPosition, endPosition, PieceType.BISHOP));
        moves.add(new ChessMove(startPosition, endPosition, PieceType.ROOK));
        moves.add(new ChessMove(startPosition, endPosition, PieceType.KNIGHT));
        return moves;
    }
}


