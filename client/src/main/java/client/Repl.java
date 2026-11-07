package client;

import chess.ChessBoard;
import chess.ChessGame;
import exception.ResponseException;
import model.requestresponse.GameSummary;
import server.ServerFacade;
import ui.BoardRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Repl {
    private final ServerFacade server;
    private String authToken = null;
    private State state = State.SIGNEDOUT;
    private List<GameSummary> games = new ArrayList<>();

    public Repl(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to Chess!");
        System.out.println(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print(">>> ");
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result + "\n");
            } catch (Throwable ex) {
                var message = ex.toString();
                System.out.print(message + "\n");
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
               - joinGame <game number> <color>
               - observe <game number>
               - help
               - quit
               """;
    }

    private String eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "creategame" -> createGame(params);
                case "listgames" -> listGames();
                case "joingame" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String register(String... params) throws ResponseException {
        verifyIsState(State.SIGNEDOUT);

        if (params.length >= 3) {
            authToken = server.register(params[0], params[1], params[2]).authToken();
            state = State.SIGNEDIN;
            return "Registered! You are now signed in as: " + params[0];
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password> <email>");
    }

    private String login(String... params) throws ResponseException {
        verifyIsState(State.SIGNEDOUT);

        if (params.length >= 2) {
            authToken = server.login(params[0], params[1]).authToken();
            state = State.SIGNEDIN;
            return "You are now signed in as: " + params[0];
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
    }

    private String logout() throws ResponseException {
        verifyIsState(State.SIGNEDIN);

        server.logout(authToken);
        state = State.SIGNEDOUT;
        authToken = null;
        games = new ArrayList<>();
        return "You are now signed out.";
    }

    private String createGame(String... params) throws ResponseException {
        verifyIsState(State.SIGNEDIN);

        if (params.length >= 1) {
            String gameName = String.join(" ", params);
            server.createGame(gameName, authToken);
            return "Game created: " + gameName;
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameName>");
    }

    private String listGames() throws ResponseException {
        verifyIsState(State.SIGNEDIN);

        games = new ArrayList<>(server.listGames(authToken).games());
        if (games.isEmpty()) {
            return "No games yet. Create one with: createGame <gameName>";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < games.size(); i++) {
            GameSummary game = games.get(i);
            String white = game.whiteUsername() == null ? "<open>" : game.whiteUsername();
            String black = game.blackUsername() == null ? "<open>" : game.blackUsername();
            result.append(
                    String.format(
                            "%d] %s White: %s Black: %s\n",
                            i + 1,
                            game.gameName(),
                            white,
                            black
                    )
            );
        }

        return result.toString();
    }

    private GameSummary gameFromListNumber(String input) throws ResponseException {
        int num;

        try {
            num = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ResponseException(ResponseException.Code.ClientError, "Game number must be a number.");
        }

        if (games.isEmpty()) {
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    "Games not found. Try running listGames to refresh the feed."
            );
        }

        if (num < 1 || num > games.size()) {
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    "No game " + num + ". Pick 1-" + games.size());
        }

        return games.get(num - 1);
    }

    private String joinGame(String... params) throws ResponseException {
        verifyIsState(State.SIGNEDIN);

        if (params.length != 2) {
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    "Expected: <game number> <WHITE|BLACK>"
            );
        }

        GameSummary game = gameFromListNumber(params[0]);
        ChessGame.TeamColor color;

        if (params[1].equalsIgnoreCase("white")) {
            color = ChessGame.TeamColor.WHITE;
        } else if (params[1].equalsIgnoreCase("black")) {
            color = ChessGame.TeamColor.BLACK;
        } else {
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    "Team color must be WHITE or BLACK");
        }

        server.joinGame(color, game.gameID(), authToken);
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        return "Joined " + game.gameName() + " as " + color + ".\n" + BoardRenderer.render(board, color);
    }

    private String observeGame(String... params) throws ResponseException {
        verifyIsState(State.SIGNEDIN);

        if (params.length != 1) {
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    "Expected: <game number>"
            );
        }

        GameSummary game = gameFromListNumber(params[0]);
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        return "Observing " + game.gameName() + "\n" + BoardRenderer.render(board, ChessGame.TeamColor.WHITE);
    }

    private void verifyIsState(State state) throws ResponseException {
        if (!this.state.equals(state)) {
            String stateMessage = (state.equals(State.SIGNEDIN)) ? "sign in" : "sign out";
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    "You must " + stateMessage + " to run this command."
            );
        }
    }
}
