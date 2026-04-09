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

    // Command to broadcast to everyone but the root client
    public void notifyOthers(Session excludeSession, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        for (Session c: connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    // Command to broadcast to only the root client
    public void notifyRoot(Session session, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        session.getRemote().sendString(msg);
    }

    // Command to broadcast to everyone
    public void notifyAll(ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        for (Session c: connections.values()) {
            if (c.isOpen()) {
                c.getRemote().sendString(msg);
            }
            
        }
    }
 

    
}
