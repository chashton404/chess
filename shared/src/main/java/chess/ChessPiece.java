package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pieceColor == null) ? 0 : pieceColor.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChessPiece other = (ChessPiece) obj;
        if (pieceColor != other.pieceColor)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();

        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == PieceType.PAWN) {
            
            ChessPosition startPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

            int direction;

            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                /* Implement the logic for if the pawn is white */
                direction = 1;
            } else {
                /* Implement the logic for if the pawn is black */
                direction = -1;
            }

            /* First we check directly in front of the pawn by doing the current row += direction */
            int currRow = startPosition.getRow();
            int startRow = startPosition.getRow();
            currRow += direction;
            ChessPosition inFront = new ChessPosition(currRow, myPosition.getColumn());

            /* Now we check that position */
            int inFrontStatus = checkSpotStatus(board, piece.getTeamColor(), inFront);

            if (inFrontStatus == 0) {
                /* Spot is empty, this is the only situation in which the pawn can move forward
                 we now see if it's the first position or the last position */
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    if (startRow == 2) {
                        /* This is the start position for the white team.
                        Add the move like normal
                        then check the position ahead to see if the pawn is able to move forward again */
                        moves.add(new ChessMove(startPosition, inFront, null));

                        currRow += direction;
                        ChessPosition farFront = new ChessPosition(currRow, myPosition.getColumn());

                        int farFrontStatus = checkSpotStatus(board, piece.getTeamColor(), farFront);

                        if (farFrontStatus == 0) {
                            /* the spot in front of the pawn is available, add it to the moves */
                            moves.add(new ChessMove(startPosition, farFront, null));
                        }

                    } else if (startRow == 7) {
                        /* It's the second to last position, the player is moving into a spot where they are able to promote their piece
                        accept a promotion */
                        moves.add(new ChessMove(startPosition, inFront, PieceType.QUEEN));
                        moves.add(new ChessMove(startPosition, inFront, PieceType.BISHOP));
                        moves.add(new ChessMove(startPosition, inFront, PieceType.ROOK));
                        moves.add(new ChessMove(startPosition, inFront, PieceType.KNIGHT));

                    } else {
                        /* it's not the first or the last position, just add the move like normal */
                        moves.add(new ChessMove(startPosition, inFront, null));
                    }
                } else {
                    if (startRow == 7) {
                        /* This is the start position for the black team.
                        Add the move like normal
                        then check the position ahead to see if the pawn is able to move forward again */
                        moves.add(new ChessMove(startPosition, inFront, null));

                        currRow += direction;
                        ChessPosition farFront = new ChessPosition(currRow, myPosition.getColumn());

                        int farFrontStatus = checkSpotStatus(board, piece.getTeamColor(), farFront);

                        if (farFrontStatus == 0) {
                            /* the spot in front of the pawn is available, add it to the moves */
                            moves.add(new ChessMove(startPosition, farFront, null));
                        }
                    } else if (startRow == 2) {
                        /* This is the last position for the black team, accept a promotion as well */
                        moves.add(new ChessMove(startPosition, inFront, PieceType.QUEEN));
                        moves.add(new ChessMove(startPosition, inFront, PieceType.BISHOP));
                        moves.add(new ChessMove(startPosition, inFront, PieceType.ROOK));
                        moves.add(new ChessMove(startPosition, inFront, PieceType.KNIGHT));
                    } else {
                        /* This isn't the first or last postion, add the move as normal */
                        moves.add(new ChessMove(startPosition, inFront, null));
                    }
                }
            }      
            
            /* Add the logic for the diagonal moves for the pawns */

            return moves;
            
        } else if (piece.getPieceType() == PieceType.BISHOP) {

            List<ChessMove> diagonalMoves = new ArrayList<>(diagonalMovement(board, myPosition, piece));
            moves.addAll(diagonalMoves);

            return moves;

        } else if (piece.getPieceType() == PieceType.ROOK) {

            List<ChessMove> straightMoves = new ArrayList<>(straightMovement(board, myPosition, piece));
            moves.addAll(straightMoves);

            return moves;

        } else if (piece.getPieceType() == PieceType.KNIGHT){  
            /* TODO: IMPLEMENT PIECE MOVES FOR KNIGHTS */       
            return List.of();

        } else if (piece.getPieceType() == PieceType.KING){
            /* TODO: IMPLEMENT PIECE MOVES FOR KING */
            return List.of();

        } else if (piece.getPieceType() == PieceType.QUEEN) {

            List<ChessMove> straightMoves = new ArrayList<>(straightMovement(board, myPosition, piece));
            moves.addAll(straightMoves);

            List<ChessMove> diagonalMoves = new ArrayList<>(diagonalMovement(board, myPosition, piece));
            moves.addAll(diagonalMoves);

            return moves;

        } else {
            throw new RuntimeException("Invalid Piece Type");
        }
    }

    private Collection<ChessMove> diagonalMovement(ChessBoard board, ChessPosition piecePosition, ChessPiece piece) {
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

                /* The diagonal search label here allows us to break out of the loop when we find a piece of our own team */
                diagonalSearch:
                while ( 0 < currRow && currRow < 9 && 0 < currCol && currRow < 9 ){
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
                            pieceMoves.add(move);
                            break diagonalSearch; 
                        } else {
                            /* Otherwise, don't add the piece and break the loop */
                            break diagonalSearch;
                        }
                    } else {
                        pieceMoves.add(move);
                    }
                }
            }
        }
        return pieceMoves;
    }

    private Collection<ChessMove> straightMovement(ChessBoard board, ChessPosition position, ChessPiece piece) {
        /* Create the list of directions that we will use to move and the moves*/
        List<Integer> direction = List.of(-1,1);
        List<ChessMove> pieceMoves = new ArrayList<>();


        ChessPosition startPosition = new ChessPosition(position.getRow(), position.getColumn());

        int currRow = startPosition.getRow();
        int currCol = startPosition.getColumn();
        /* Get the moves on the vertical axis */

        for (Integer rowDirection : direction) {
            rowSearch:
            while( 0 < currRow && currRow < 9) {
                currRow += rowDirection;

                if (currRow == 0 || currRow == 9){
                    currRow = startPosition.getRow();
                    break rowSearch;
                }

                ChessPosition newPosition = new ChessPosition(currRow, currCol);
                ChessMove newMove = new ChessMove(startPosition, newPosition, null); 
                ChessPiece otherPiece = board.getPiece(newPosition);

                if (otherPiece != null) {
                    if (otherPiece.getTeamColor() != piece.getTeamColor()) {
                        /* In the case of the opponent piece being in the path, add the move as a possible move and then break the loop */
                        pieceMoves.add(newMove);
                        currRow = startPosition.getRow();
                        break rowSearch; 
                    } else {
                        /* Otherwise, don't add the piece, reset the row search, and break the loop */
                        currRow = startPosition.getRow();
                        break rowSearch;
                    }
                } else {
                    pieceMoves.add(newMove);
                }
            }
            currRow = startPosition.getRow();
        }

        for (Integer colDirection : direction) {
            colSearch:
            while( 0 < currCol && currCol < 9) {
                currCol += colDirection;

                if (currCol == 0 || currCol == 9){
                    currCol = startPosition.getRow();
                    break colSearch;
                }

                ChessPosition newPosition = new ChessPosition(currRow, currCol);
                ChessMove newMove = new ChessMove(startPosition, newPosition, null); 
                ChessPiece otherPiece = board.getPiece(newPosition);

                if (otherPiece != null) {
                    if (otherPiece.getTeamColor() != piece.getTeamColor()) {
                        /* In the case of the opponent piece being in the path, add the move as a possible move and then break the loop */
                        pieceMoves.add(newMove);
                        currCol = startPosition.getColumn();
                        break colSearch; 
                    } else {
                        /* Otherwise, don't add the piece, reset the column, and break the loop */
                        currCol = startPosition.getColumn();
                        break colSearch;
                    }
                } else {
                    pieceMoves.add(newMove);
                }
            }
            currCol = startPosition.getColumn();
        }
        return pieceMoves;
    }

    private int checkSpotStatus(ChessBoard board, ChessGame.TeamColor teamColor, ChessPosition newPosition) {
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
}
