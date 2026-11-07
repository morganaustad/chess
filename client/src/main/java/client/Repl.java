package client;

import chess.ChessGame;
import exception.ResponseException;
import model.requestresponse.CreateGameResult;
import model.requestresponse.GameSummary;
import model.requestresponse.ListGamesResult;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class Repl {
    private final ServerFacade server;
    private String authToken = null;
    private State state = State.SIGNEDOUT;

    public Repl(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to Chess!");
        System.out.println(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable ex) {
                var message = ex.toString();
                System.out.print(message);
            }
        }
        System.out.println();
    }

    private String help() {
        if (state == State.SIGNEDOUT) {
            return """
                   - register <username> <password> <email>
                   - login <username> <password>
                   - help
                   - quit
                   """;
        }

        return """
               - logout
               - createGame <gameName>
               - listGames
               - joinGame <gameID> <color>
               - help
               - quit
               """;
    }

    private String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "creategame" -> createGame(params);
                case "listgames" -> listGames();
                case "joingame" -> joinGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            authToken = server.register(params[0], params[1], params[2]).authToken();
            state = State.SIGNEDIN;
            return "Registered! You are now signed in as: " + params[0];
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password> <email>");
    }

    private String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            authToken = server.login(params[0], params[1]).authToken();
            state = State.SIGNEDIN;
            return "You are now signed in as: " + params[0];
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
    }

    private String logout() throws ResponseException {
        server.logout(authToken);
        state = State.SIGNEDOUT;
        return "You are now signed out.";
    }

    private String createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            CreateGameResult gameResult = server.createGame(params[0], authToken);
            return "Game created with the ID: " + gameResult.gameID();
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameName>");
    }

    private String listGames() throws ResponseException {
        var gamesList = server.listGames(authToken).games().iterator();
        StringBuilder result = new StringBuilder();
        while (gamesList.hasNext()) {
            GameSummary game = gamesList.next();
            result.append(String.format(
                    "%d %s %s %s",
                    game.gameID(),
                    game.gameName(),
                    game.whiteUsername(),
                    game.blackUsername()
            ));
        }

        return result.toString();
    }

    private String joinGame(String... params) throws ResponseException {
        if (params.length >= 2) {
            ChessGame.TeamColor color = (
                    params[0].toLowerCase().equals("white")) ?
                    ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            server.joinGame(color, Integer.parseInt(params[0]), authToken);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameID> <color>");
    }
}
