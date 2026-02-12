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
    private Map<ChessGame.TeamColor, ChessPosition> kingPieces;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();

        /* Create the new dictionary for the teamPieces */
        this.teamPieces = new HashMap<>();
        this.teamPieces.put(ChessGame.TeamColor.WHITE, new ArrayList<>());
        this.teamPieces.put(ChessGame.TeamColor.BLACK, new ArrayList<>());

        /* Create the new dictionary for the kingPieces */
        this.kingPieces = new HashMap<>();
        this.kingPieces.put(ChessGame.TeamColor.WHITE, new ChessPosition(0, 0));
        this.kingPieces.put(ChessGame.TeamColor.BLACK, new ChessPosition(0, 0));

        this.board.resetBoard();
        setTeamPieces();
    }

    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((teamTurn == null) ? 0 : teamTurn.hashCode());
        result = prime * result + ((board == null) ? 0 : board.hashCode());
        result = prime * result + ((teamPieces == null) ? 0 : teamPieces.hashCode());
        result = prime * result + ((kingPieces == null) ? 0 : kingPieces.hashCode());
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
        ChessGame other = (ChessGame) obj;
        if (teamTurn != other.teamTurn)
            return false;
        if (board == null) {
            if (other.board != null)
                return false;
        } else if (!board.equals(other.board))
            return false;
        if (teamPieces == null) {
            if (other.teamPieces != null)
                return false;
        } else if (!teamPieces.equals(other.teamPieces))
            return false;
        if (kingPieces == null) {
            if (other.kingPieces != null)
                return false;
        } else if (!kingPieces.equals(other.kingPieces))
            return false;
        return true;
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
        if (piece == null) {
            return null;
        }

        TeamColor teamColor = piece.getTeamColor();

        /* Get the opposing team */
        TeamColor opTeam;
        if (teamColor == TeamColor.WHITE) {
            opTeam = TeamColor.BLACK;
        } else {
            opTeam = TeamColor.WHITE;
        }

        Collection<ChessMove> possibleMoves = piece.pieceMoves(this.board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        /* Create temporary variables to store the orignal board, pieces, and kings */
        ChessBoard real_board = copyBoard(this.board);
        Map<ChessGame.TeamColor, Collection<ChessPosition>> real_pieces = copyPieces(this.teamPieces);
        Map<ChessGame.TeamColor, ChessPosition> real_kings = copyKings(this.kingPieces);

        for (ChessMove move : possibleMoves) {
            /* create a deepcopy of the board that we will do moves on */
            ChessBoard board_copy = copyBoard(real_board);
            Map<ChessGame.TeamColor, Collection<ChessPosition>> copy_pieces = copyPieces(real_pieces);
            Map<ChessGame.TeamColor, ChessPosition> copy_kings = copyKings(real_kings);

            this.board = board_copy;
            this.teamPieces = copy_pieces;
            this.kingPieces = copy_kings;

            if (this.board.getPiece(move.getEndPosition()) == null) {
                /* Change the position of the pieces in the player's list */
                this.teamPieces.get(piece.getTeamColor()).remove(move.getStartPosition());
                this.teamPieces.get(piece.getTeamColor()).add(move.getEndPosition());
                /* If it's a king then we need to update the position of the king in the list */
                if (piece.getPieceType() == PieceType.KING) {
                    this.kingPieces.put(piece.getTeamColor(), move.getEndPosition());
                }
            } else if (this.board.getPiece(move.getEndPosition()).getTeamColor() == opTeam) {
                /* Change the position of the pieces in the opponent's pieces */
                this.teamPieces.get(opTeam).remove(move.getEndPosition());
                this.teamPieces.get(piece.getTeamColor()).remove(move.getStartPosition());
                this.teamPieces.get(piece.getTeamColor()).add(move.getEndPosition());
                /* If it's a king then we need to update the position of the king in the list */
                if (piece.getPieceType() == PieceType.KING) {
                    this.kingPieces.put(piece.getTeamColor(), move.getEndPosition());
                }
            }

            /* make a move on the copied board, update the lists accordingly, and check for check */
            board_copy.addPiece(move.getStartPosition(), null);
            board_copy.addPiece(move.getEndPosition(), piece);

            Boolean possible_check = isInCheck(teamColor);

            if (!possible_check) {
                /* Only add the move if the king is not in check */
                validMoves.add(move);
            }
        }  
        /* Restore the old board and pieces*/
        this.board = real_board;
        this.teamPieces = real_pieces;
        this.kingPieces = real_kings;

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
        /* Raise an error if it's not the right team's turn */
        if (this.board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("Not your team's turn");
        }
        if (this.board.getPiece(move.getStartPosition()).getTeamColor() != this.teamTurn){
            throw new InvalidMoveException("Not your team's turn");
        }

        /* Get the piece */
        ChessPiece piece = this.board.getPiece(move.getStartPosition());

        /* Raise an error if this move is not in the list of possible moves */
        Collection<ChessMove> possibleMoves = validMoves(move.getStartPosition());
        if (!possibleMoves.contains(move)) {
            throw new InvalidMoveException("Not a valid move");
        }
        
        /* Get the opposing team */
        TeamColor opTeam;
        if (this.teamTurn == TeamColor.WHITE) {
            opTeam = TeamColor.BLACK;
        } else {
            opTeam = TeamColor.WHITE;
        }

 
        if (this.board.getPiece(move.getEndPosition()) == null) {
            /* Change the position of the pieces in the player's list */
            this.teamPieces.get(this.teamTurn).remove(move.getStartPosition());
            this.teamPieces.get(this.teamTurn).add(move.getEndPosition());
            /* If it's a king then we need to update the position of the king in the list */
            if (piece.getPieceType() == PieceType.KING) {
                this.kingPieces.put(piece.getTeamColor(), move.getEndPosition());
            }
        } else if (this.board.getPiece(move.getEndPosition()).getTeamColor() == opTeam) {
            /* Change the position of the pieces in the opponent's pieces */
            this.teamPieces.get(opTeam).remove(move.getEndPosition());
            this.teamPieces.get(this.teamTurn).remove(move.getStartPosition());
            this.teamPieces.get(this.teamTurn).add(move.getEndPosition());
            /* If it's a king then we need to update the position of the king in the list */
            if (piece.getPieceType() == PieceType.KING) {
                this.kingPieces.put(piece.getTeamColor(), move.getEndPosition());
            }
        } else {
            throw new InvalidMoveException("Can't move on to same team");
        }

        this.board.addPiece(move.getStartPosition(), null);
        this.board.addPiece(move.getEndPosition(), piece);
        this.teamTurn = opTeam;

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        /* Get the opposing team color */
        TeamColor opTeam;
        if (teamColor == TeamColor.WHITE) {
            opTeam = TeamColor.BLACK;
        } else {
            opTeam = TeamColor.WHITE;
        }

        /* Get the opposing teams positions, moves, and valid moves */
        Collection<ChessPosition> opPositions = this.teamPieces.get(opTeam);
        ChessPosition king = this.kingPieces.get(teamColor);
        /* Check to make sure that it's not null at these points, otherwise it will throw the null pointer error */
        if (opPositions == null) {
            return false;
        }
        if (king == null) {
            throw new IllegalArgumentException("King not found");
        }


        for (ChessPosition position : opPositions) {
            ChessPiece piece = this.board.getPiece(position);
            if (piece == null) {
                continue;
            }
            for (ChessMove move: piece.pieceMoves(this.board, position)) {
                if (move.getEndPosition().equals(king)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        /* Checkmate will occur when the current team is in check and if there are no valid moves */
        boolean inCheck = isInCheck(teamColor);
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        if (inCheck) {
            for (ChessPosition position: this.teamPieces.get(teamColor)){
                possibleMoves.addAll(validMoves(position));
            }
            if (possibleMoves.isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        for (ChessPosition position: this.teamPieces.get(teamColor)){
            possibleMoves.addAll(validMoves(position));
        }
        if (possibleMoves.isEmpty()){
            return true;
        } else {
            return false;
        }

    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board.squares = new ChessPiece[8][8];
        this.board = board;
        setTeamPieces();
    }

        /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
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
                        this.kingPieces.put(piece.getTeamColor(), position);
                    }
                }
            }
        }
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
     * Create a deepcopies of the teamPieces so that they can be updated while we check for check
     * @param chessPieces the list of chesspieces to be copied
     * @return a deepcopy of the chesspieces
     */
    public Map<ChessGame.TeamColor, Collection<ChessPosition>> copyPieces(Map<ChessGame.TeamColor, Collection<ChessPosition>> chessPieces) {
        Map<ChessGame.TeamColor, Collection<ChessPosition>> piecesCopy = new HashMap<>();
        piecesCopy.put(ChessGame.TeamColor.WHITE, new ArrayList<>());
        piecesCopy.put(ChessGame.TeamColor.BLACK, new ArrayList<>());

        for (ChessGame.TeamColor team : chessPieces.keySet()){
            for (ChessPosition position : chessPieces.get(team)) {
                piecesCopy.get(team).add(position);
            }
        }

        return piecesCopy;
    }

    /**
     * Create a deepcopy of the kingPieces that can then be updated while we check for check
     * @param kingPieces the dictionary containing the king's pieces
     * @param kingsCopy the copied list of kings
     */
    public Map<ChessGame.TeamColor, ChessPosition> copyKings(Map<ChessGame.TeamColor, ChessPosition> kingPieces) {
        Map<ChessGame.TeamColor, ChessPosition> kingsCopy = new HashMap<>();
        kingsCopy.put(ChessGame.TeamColor.WHITE, new ChessPosition(0, 0));
        kingsCopy.put(ChessGame.TeamColor.BLACK, new ChessPosition(0, 0));

        for (ChessGame.TeamColor team : kingPieces.keySet()){
                kingsCopy.put(team, kingPieces.get(team));

        }
        return kingsCopy;
    }
}
