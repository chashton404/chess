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
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == PieceType.PAWN) {
            /* TODO: IMPLEMENT PIECE MOVES FOR PAWNS */
            return List.of();
        } else if (piece.getPieceType() == PieceType.BISHOP) {
            /* TODO: IMPLEMENT PIECE MOVES FOR BISHOPS */

            /* Initalize our lists, these are the lists that we will append the moves to and wwe will iterate over the different directions */
            List<Integer> direction = List.of(-1, 1);
            List<ChessMove> moves = new ArrayList<>();

            /* Save the current position as the start position that will be used when contstructing the chess move opbject */
            ChessPosition startPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

            /* We use a nested for loop to iterate over the four diagonal locations */
            for (Integer colDirection : direction) {
                for (Integer rowDirection : direction) {

                    int currRow = startPosition.getRow();
                    int currCol = startPosition.getColumn();

                    /* The diagonal search label here allows us to break out of the loop when we find a piece of our own team */
                    diagonalSearch:
                    while ( 1 < currRow && currRow < 8 && 1 < currCol && currRow < 8 ){
                        /* Iterate in each of the directions */
                        currRow += rowDirection;
                        currCol += colDirection;
                        
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
                }
            }

            return moves;

        } else if (piece.getPieceType() == PieceType.ROOK) {
            /* TODO: IMPLEMENT PIECE MOVES FOR ROOKS */
            return List.of();
        } else if (piece.getPieceType() == PieceType.KNIGHT){  
            /* TODO: IMPLEMENT PIECE MOVES FOR KNIGHTS */       
            return List.of();
        } else if (piece.getPieceType() == PieceType.KING){
            /* TODO: IMPLEMENT PIECE MOVES FOR KING */
            return List.of();
        } else if (piece.getPieceType() == PieceType.QUEEN) {
            /* TODO: IMPLEMENT PIECE MOVES FOR QUEEN */
            return List.of(); 
        } else {
            throw new RuntimeException("Invalid Piece Type");
        }
    }
}
