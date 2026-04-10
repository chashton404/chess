export const FILES = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
export const PROMOTION_OPTIONS = ['QUEEN', 'ROOK', 'BISHOP', 'KNIGHT'];

const PIECE_SYMBOLS = {
  WHITE: {
    KING: '♔',
    QUEEN: '♕',
    ROOK: '♖',
    BISHOP: '♗',
    KNIGHT: '♘',
    PAWN: '♙',
  },
  BLACK: {
    KING: '♚',
    QUEEN: '♛',
    ROOK: '♜',
    BISHOP: '♝',
    KNIGHT: '♞',
    PAWN: '♟',
  },
};

export function getPieceSymbol(piece) {
  if (!piece) {
    return '';
  }

  return PIECE_SYMBOLS[piece.pieceColor]?.[piece.type] ?? '';
}

export function squareLabel(position) {
  return `${FILES[position.col - 1]}${position.row}`;
}

export function boardRows(orientation) {
  return orientation === 'BLACK'
    ? [1, 2, 3, 4, 5, 6, 7, 8]
    : [8, 7, 6, 5, 4, 3, 2, 1];
}

export function boardCols(orientation) {
  return orientation === 'BLACK'
    ? [8, 7, 6, 5, 4, 3, 2, 1]
    : [1, 2, 3, 4, 5, 6, 7, 8];
}

export function isPromotionMove(piece, targetRow) {
  if (!piece || piece.type !== 'PAWN') {
    return false;
  }

  return (
    (piece.pieceColor === 'WHITE' && targetRow === 8) ||
    (piece.pieceColor === 'BLACK' && targetRow === 1)
  );
}

export function statusFromGame(game) {
  if (!game) {
    return 'Waiting for game state';
  }

  const turn = game.teamTurn ?? 'UNKNOWN';
  return `${turn} to move`;
}
