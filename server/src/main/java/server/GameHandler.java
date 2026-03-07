package server;

import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import service.GameService;

import java.util.Map;

public class GameHandler {

    Integer gameNumber = 1;

    public void createGame(Context ctx) {
       String authToken = ctx.header("authorization");
       String gameName = ctx.body("gameName");

    }
    
}
