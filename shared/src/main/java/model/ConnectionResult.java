package model;

import chess.ChessGame;

public record ConnectionResult(String username, String playerColor, ChessGame game) {}
