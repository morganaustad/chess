package client;

import exception.ResponseException;
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
}
