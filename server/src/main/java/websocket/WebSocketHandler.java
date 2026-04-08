package websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import websocket.commands.UserGameCommand;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

// Import the commands for client to server communication

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket Connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> System.out.println("connected");
                case MAKE_MOVE -> System.out.println("move made");
                case LEAVE -> System.out.println("left game");
                case RESIGN -> System.out.println("resigned");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        // Add this session
        connections.add(session);

        // TODO implement this method
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket Closed");
    }
}

