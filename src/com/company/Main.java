package com.company;

import java.util.Scanner;

public class Main {

    public static final String MOVE_COMMAND = "move";
    public static final String LIST_COMMAND = "list";
    public static final String HELP_COMMAND = "help";
    public static final String QUIT = "quit";
    public static final String NEW_COMMAND = "new";
    public static final String BOARD_COMMAND = "board";
    public static final String HISTORY_COMMAND = "history";

    public static void main(String[] args) {
        System.out.println("Welcome to Chess!\n" + "Type 'help' for a list of commands");

        Game game = new Game();
        printHelpMenu();
        Scanner scanner = new Scanner(System.in);
        WHILE:
        while (!game.isOver()) {
            game.printWhoIsNext();
            System.out.println();
            String firstParameter = scanner.next().toLowerCase();
            switch (firstParameter) {
                case HELP_COMMAND:
                    printHelpMenu();
                    break;
                case QUIT:
                    System.out.println("Goodbye!");
                    break WHILE;
                case NEW_COMMAND:
                    game = new Game();
                    break;
                case BOARD_COMMAND:
                    game.printBoard();
                    break;
                case LIST_COMMAND:
                    game.printPossibleMoves();
                    break;
                case MOVE_COMMAND:
                    try {
                        move(game, scanner);
                    } catch (WrongMovementException e) {
                        System.out.println("Wrong command. Try once again please");
                    }
                    break;
                case HISTORY_COMMAND:
                    System.out.println("Moves history:");
                    game.printMovesHistory();
                    break;
                default:
                    System.out.println("Wrong command. Try once again please");
            }
        }
    }

    private static void printHelpMenu() {
        System.out.println("Possible commands:");
        System.out.println("'help'                      - Show this menu");
        System.out.println("'quit'                      - Quit Chess");
        System.out.println("'new'                       - Create a new game");
        System.out.println("'board'                     - Show the chess board");
        System.out.println("'list'                      - List all possible moves");
        System.out.println("'move' <colrow> <colrow>    - Make a move");
        System.out.println("'history'                   - Show moves history");
    }

    private static void move(Game game, Scanner scanner) throws WrongMovementException {
        String secondParameter = scanner.next();
        char letter1 = secondParameter.charAt(0);
        byte number1 = Byte.parseByte(secondParameter.substring(1, 2));
        String thirdParameter = scanner.next();
        char letter2 = thirdParameter.charAt(0);
        byte number2 = Byte.parseByte(thirdParameter.substring(1, 2));
        game.move(letter1, number1, letter2, number2);
    }
}
