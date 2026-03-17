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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChessPiece other = (ChessPiece) obj;
        if (pieceColor != other.pieceColor) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
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
            
            Collection<ChessMove> pawnMoves = new ArrayList<>(PawnMoveCalculator.getPawnMoves(board, myPosition, piece));
            moves.addAll(pawnMoves);

            return moves;
            
        } else if (piece.getPieceType() == PieceType.BISHOP) {

            Collection<ChessMove> bishopMoves = new ArrayList<>(bishopMovesFunction(board, myPosition, piece));
            moves.addAll(bishopMoves);

            return moves;

        } else if (piece.getPieceType() == PieceType.ROOK) {

            Collection<ChessMove> rookMoves = new ArrayList<>(rookMovesFunction(board, myPosition, piece));
            moves.addAll(rookMoves);

            return moves;

        } else if (piece.getPieceType() == PieceType.KNIGHT){  
            
            Collection<ChessMove> knightMoves = new ArrayList<>(knightMovesFunction(board, myPosition, piece));
            moves.addAll(knightMoves);
                
            return moves;
        } else if (piece.getPieceType() == PieceType.KING){
            
            Collection<ChessMove> kingMoves = new ArrayList<>(kingMovesFunction(board, myPosition, piece));
            moves.addAll(kingMoves);

            return moves;
        } else if (piece.getPieceType() == PieceType.QUEEN) {

            Collection<ChessMove> queenMoves = new ArrayList<>(queenMovesFunction(board, myPosition, piece));
            moves.addAll(queenMoves);

            return moves;

        } else {
            throw new RuntimeException("Invalid Piece Type");
        }
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     * 
     * We use a switch statement instead of doing a set of if's and elif's
     * 
     * @param board
     * @param myPosition
     * @return
     */
    // public Collection<ChessMove> pieceMovesSwitch(ChessBoard board, ChessPosition myPosition) {
    //     List<ChessMove> moves = new ArrayList<>();
    //     ChessPiece piece = board.getPiece(myPosition);
    //     PieceType pieceType = piece.getPieceType();

    //     return switch(pieceType) {
    //         case PAWN -> getPawnMoves(board, myPosition, piece);
    //         case BISHOP -> getBishopMoves(board, myPosition, piece);
    //         case ROOK ->  getRookMoves(board, myPosition, piece);
    //         case KNIGHT -> getKnightMoves(board, myPosition, piece);
    //         case KING -> getKingMoves(board, myPosition, piece);
    //         case QUEEN -> getQueenMoves(board, myPosition, piece);
    //     };

    // }


    // ---------------------------- BISHOP MOVES ------------------------------------
    private Collection<ChessMove> bishopMovesFunction(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();
        
        List<ChessMove> diagonalMoves = new ArrayList<>(diagonalMovement(board, myPosition, piece));
        moves.addAll(diagonalMoves);

        return moves;
    }




    // ---------------------------- ROOK MOVES ------------------------------------
    private Collection<ChessMove> rookMovesFunction(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();

        List<ChessMove> straightMoves = new ArrayList<>(straightMovement(board, myPosition, piece));
        moves.addAll(straightMoves);

        return moves;
    }



    // ---------------------------- KNIGHT MOVES ------------------------------------
    private Collection<ChessMove> knightMovesFunction(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();

        /* Create our start position */
        ChessPosition startPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());


        /* Create our lists of directions we will move, either a one or a two */
        List<Integer> twos = List.of(-2,2);
        List<Integer> ones = List.of(-1,1);

        for (int rowDirection : twos) {
            for (int colDirection : ones) {
                int currRow = startPosition.getRow();
                int currCol = startPosition.getColumn();

                currRow += rowDirection;
                currCol += colDirection;

                ChessPosition newPosition = new ChessPosition(currRow, currCol);

                int knightStatus;
                if (0 < currRow && currRow < 9 && 0 < currCol && currCol < 9){
                    knightStatus = MoveUtils.checkSpotStatus(board, piece.getTeamColor(), newPosition);
                } else {
                    knightStatus = -1;
                }

                if (knightStatus == 0 || knightStatus == 2) {
                    moves.add(new ChessMove(startPosition, newPosition, null));

                }    
            }
        }

        for (int colDirection : twos) {
            for (int rowDirection : ones) {
                int currRow = startPosition.getRow();
                int currCol = startPosition.getColumn();

                currRow += rowDirection;
                currCol += colDirection;

                ChessPosition newPosition = new ChessPosition(currRow, currCol);

                int knightStatus;
                if (0 < currRow && currRow < 9 && 0 < currCol && currCol < 9){
                    knightStatus = MoveUtils.checkSpotStatus(board, piece.getTeamColor(), newPosition);
                } else {
                    knightStatus = -1;
                }

                if (knightStatus == 0 || knightStatus == 2) {
                    moves.add(new ChessMove(startPosition, newPosition, null));

                }    
            }
        }

        return moves;
    }



    // ---------------------------- KING MOVES ------------------------------------
    private Collection<ChessMove> kingMovesFunction(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();

        /* Create our starting piece */
        ChessPosition startPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

        /* Initialize our list of directions */
        List<Integer> directions = List.of(-1,0,1);

        for (Integer rowDirection : directions) {
            for (Integer colDirection : directions) {
                int currRow = startPosition.getRow();
                int currCol = startPosition.getColumn();

                currRow += rowDirection;
                currCol += colDirection;

                ChessPosition newPosition = new ChessPosition(currRow, currCol);

                int newPositionStatus;
                if (0 < currRow && currRow < 9 && 0 < currCol && currCol < 9 ) {
                    newPositionStatus = MoveUtils.checkSpotStatus(board, piece.getTeamColor(), newPosition);
                } else {
                    newPositionStatus = -1;
                }

                if (newPositionStatus == 0 || newPositionStatus == 2) {
                    moves.add(new ChessMove(startPosition, newPosition, null));
                }
            }
        }

        return moves;
    }



    // ---------------------------- QUEEN MOVES ------------------------------------
    private Collection<ChessMove> queenMovesFunction(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();
        
        List<ChessMove> straightMoves = new ArrayList<>(straightMovement(board, myPosition, piece));
        moves.addAll(straightMoves);

        List<ChessMove> diagonalMoves = new ArrayList<>(diagonalMovement(board, myPosition, piece));
        moves.addAll(diagonalMoves);
            
        return moves;
    }


    // ---------------------------- GENERAL USE -------------------------------------

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
}
