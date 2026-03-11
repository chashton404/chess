package server;

import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import service.GameService;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.ListGamesResult;
import model.JoinGameRequest;

import java.util.Map;

public class GameHandler {

    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    Integer gameNumber = 1;

    public void createGame(Context ctx) {
        try {
            // Our the header stores the authToken
            String authToken = ctx.header("authorization");

            // The body stores the gameName, which we turn into the CreateGameRequest using ctx.bodyAsClass
            CreateGameRequest req = ctx.bodyAsClass(CreateGameRequest.class);

            // Now we call the createGame function from the service class
            CreateGameResult res = gameService.createGame(authToken, req);

            // Return the result
            ctx.status(200);
            ctx.json(res);
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.json(Map.of("message", e.getMessage()));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(Map.of("message", e.getMessage()));
        }
    }

    public void listGames(Context ctx) {
        try {
            // Per usual the header has the authtoken, we use it to call the listGames function from the service class
            String authToken = ctx.header("authorization");
            ListGamesResult games = gameService.listGames(authToken);
    
            ctx.status(200);
            ctx.json(games);
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(Map.of("message", e.getMessage()));
        }
    }

    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            JoinGameRequest req = ctx.bodyAsClass(JoinGameRequest.class);

            gameService.joinGame(authToken, req);

            ctx.status(200);
            ctx.result("{}");
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.json(Map.of("message", e.getMessage()));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(Map.of("message", e.getMessage()));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.json(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(Map.of("message", e.getMessage()));
        }
    }
    
}


