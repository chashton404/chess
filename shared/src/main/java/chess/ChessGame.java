package chess;

import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import chess.ChessPiece.PieceType;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    /* These are the variables we will use throughout */
    private TeamColor teamTurn;
    private ChessBoard board;

    /* This is the dictionary that we will use to store each teams pieces */
    private Map<ChessGame.TeamColor, Collection<ChessPosition>> teamPieces;
    private Map<ChessGame.TeamColor, Collection<ChessPosition>> kingPieces;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamPieces = new HashMap<>();
        this.teamPieces.put(ChessGame.TeamColor.WHITE, new ArrayList<>());
        this.teamPieces.put(ChessGame.TeamColor.BLACK, new ArrayList<>());
        this.kingPieces.put(ChessGame.TeamColor.WHITE, new ArrayList<>());
        this.kingPieces.put(ChessGame.TeamColor.BLACK, new ArrayList<>());
        setTeamPieces();
    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        /* Return either BLACK or WHITE */
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        /* change the team color */
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        /* Initialize the piece, the list of possible moves, and the list of valid moves */
        ChessPiece piece = this.board.getPiece(startPosition);
        Collection<ChessMove> possibleMoves = piece.pieceMoves(this.board, startPosition);
        Collection<ChessMove> validMoves;

        for (ChessMove move : possibleMoves) {
            /* create a deepcopy of the board that we will do moves on */
            ChessBoard board_copy = copyBoard(this.board);

            /* make a move on the copied board, and check for check */
            board_copy.addPiece(move.getStartPosition(), null);
            board_copy.addPiece(move.getEndPosition(), piece);
            Boolean possible_check = board_copy.isInCheck(this.teamTurn);

            if (possible_check == false) {
                /* Only add the move if the king is not in check */
                validMoves.add(move);
            }
        }  
        /*Return the list of moves */
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (this.board.getPiece(move.getStartPosition()).getTeamColor() != this.teamTurn){
            throw new InvalidMoveException("Not your team's turn");
        }
        ChessPiece piece = this.board.getPiece(move.getStartPosition());
        this.board.addPiece(move.getStartPosition(), null);
        this.board.addPiece(move.getEndPosition(), piece);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        /*TODO: Get the list of the moves for this team */
        /*TODO: Get the position of the opposing team's king */
        /*TODO; Check the list of moves for the king's position, if he is there return true otherwise return false.*/
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        /* TODO: Get the list of moves for the king, if it's empty then return true otherwise return false */
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Create a deep copy of the current chessBoard
     * 
     * @param board the chessBoard to copy
     * @return a deep copy of the chessboard object
     */
    public ChessBoard copyBoard(ChessBoard board) {
        ChessBoard copy = new ChessBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.squares[row][col];
                if (piece != null) {
                    copy.squares[row][col] = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
                } else {
                    copy.squares[row][col] = null;
                }
            }
        }
        return copy;
    }

    /**
     * Initialize the teamPieces and kingPieces dictionaries with the positions of each of the pieces
     * 
     */
    public void setTeamPieces(){
        /* Iterate over each of the team pieces and put them in a dictionary to allow for easy access */
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                /* Get the piece at the position given */
                ChessPiece piece = this.board.squares[row][col];
                if (piece != null) {
                    /* If the piece is not null then add the position to the correct team's dictionary */
                    ChessPosition position = new ChessPosition(row + 1, col + 1);
                    this.teamPieces.get(piece.getTeamColor()).add(position);
                    if (piece.getPieceType() == PieceType.KING) {
                        /* If the piece is a king then we change it's position in the king dictionary as well */
                        this.kingPieces.get(piece.getTeamColor()).add(position);
                    }
                }
            }
        }

    }
}
