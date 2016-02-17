package com.company;

import com.company.Board.Cell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.company.Color.BLACK;

public class Game {
    private final Board board;
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;
    private boolean isOver;
    private final List<String> movesHistory = new ArrayList<>();

    public Game() {
        System.out.println("New game is created");
        board = new Board();
        board.reset();
        board.display();
        player1 = new Player(Color.WHITE);
        player2 = new Player(BLACK);
        this.currentPlayer = player1;
    }

    public void printPossibleMoves() {
        Map<Cell, Set<Cell>> possibleMoves = board.calculatePossibleMoves(currentPlayer.getColor());
        System.out.println("\n" + currentPlayer.getColor().displayName + "'s moves:");
        possibleMoves.entrySet().stream().forEach(fromCell -> {
            if (fromCell != null && fromCell.getKey() != null) {
                String fromX = "" + fromCell.getKey().getX();
                fromX = fromCell.getKey().getPiece().getColor() == BLACK ? fromX.toUpperCase() : fromX;
                String from = "" + fromX + fromCell.getKey().getY();
                fromCell.getValue().stream().forEach(toCell -> {
                    String toX = "" + toCell.getX();
                    toX = fromCell.getKey().getPiece().getColor() == BLACK ? toX.toUpperCase() : toX;
                    String to = "" + toX + toCell.getY();
                    System.out.println(from + " " + to);
                });
            }
        });
    }

    public void move(char fromX, byte fromY, char toX, byte toY) throws WrongMovementException {
        if (!board.isMovementAvailable(currentPlayer.getColor(), fromX, fromY, toX, toY)) {
            throw new WrongMovementException("Wrong movement from " + fromX + fromY + " to " + toX + toY);
        }
        board.setCurrentColorMoves(currentPlayer.getColor());
        board.update(fromX, fromY, toX, toY);
        movesHistory.add(currentPlayer.getColor() + " moves " + fromX + fromY + " " + toX + toY);
        isOver = board.isGameOver();
        board.display();
        if (isOver) {
            System.out.println("The game is over.  Congrats to " + currentPlayer.getColor().displayName);
        }
        switchPayer();
    }

    private void switchPayer() {
        currentPlayer = currentPlayer == player1 ? player2 : player1;
    }

    public boolean isOver() {
        return isOver;
    }

    public void printBoard() {
        board.display();
    }

    public void printWhoIsNext() {
        System.out.println("\n" + currentPlayer.getColor().displayName + "'s Move");
    }

    public void printMovesHistory() {
        movesHistory.stream().forEach(System.out::println);
    }
}
