package websocket;

import org.eclipse.jetty.websocket.api.Session;

public record Connection(Session session, Integer gameID, String username, String playerColor) {}

