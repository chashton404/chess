package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(squares);
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
        ChessBoard other = (ChessBoard) obj;
        if (!Arrays.deepEquals(squares, other.squares))
            return false;
        return true;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        /* First we add the pawns */
        int numCols = 8;
        for (int colIndex = 0; colIndex < numCols; colIndex++) {
            squares[1][colIndex] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            squares[6][colIndex] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }

        /* Rooks */
        squares[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        squares[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        squares[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        squares[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        /* Knights */
        squares[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);

        /* Bishops */
        squares[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);

        /* King and Queen */
        squares[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        squares[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        squares[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        squares[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
    }

    @Override
    public String toString() {
        StringBuilder chessBoard = new StringBuilder();
        chessBoard.append('|');
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                chessBoard.append('|');
                ChessPiece piece = getPiece(new ChessPosition(row + 1, col + 1));
                if (piece == null) {
                    chessBoard.append(' ')
                } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        chessBoard.append('K');
                    } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                        chessBoard.append('Q');
                    } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                        chessBoard.append('B');
                    } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                        chessBoard.append('N');
                    } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                        chessBoard.append('R');
                    } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                        chessBoard.append('P');
                    }
                } else {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        chessBoard.append('k');
                    } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                        chessBoard.append('q');
                    } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                        chessBoard.append('b');
                    } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                        chessBoard.append('n');
                    } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                        chessBoard.append('r');
                    } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                        chessBoard.append('p');
                    }
                }
            }
            chessBoard.append('\n');
        }

        return chessBoard.toString();
    }
}
