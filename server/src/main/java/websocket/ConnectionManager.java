package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import websocket.messages.ServerMessage;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    public void add(Session session) {
        connections.put(session, session);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    //TODO: figure out what the notification does here and if I need to build it out
    public void broadcast(Session excludeSession, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        for (Session c: connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
    
}
