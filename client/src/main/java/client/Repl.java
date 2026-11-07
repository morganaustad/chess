package client;

import java.util.Arrays;
import java.util.Scanner;

public class Repl {
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

    public Repl(String serverUrl) {
        this.serverUrl = serverUrl;
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
            }
        }
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
}
