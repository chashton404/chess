package chess;

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

}
