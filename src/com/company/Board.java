package com.company;

import java.util.*;

import static com.company.Board.Direction.*;
import static com.company.Color.BLACK;
import static com.company.Color.WHITE;
import static com.company.Piece.Type.*;

public class Board {
    public static final char MIN_LETTER = 'a';
    public static final char MAX_LETTER = 'h';
    public static final int MIN_NUMBER = 1;
    public static final int MAX_NUMBER = 8;
    private final Map<Color, List<Piece>> pieces = new HashMap<>();
    private final Map<Byte, Map<Character, Cell>> board = new HashMap<>();
    private Piece whiteKing;
    private Piece blackKing;
    private Color currentColorMoves;
    private Map<Color, Map<Cell, Set<Cell>>> nextPossibleMoves = new HashMap<>();

    public void reset() {
        pieces.clear();
        for (byte number = MIN_NUMBER; number <= MAX_NUMBER; number++) {
            HashMap<Character, Cell> row = new HashMap<>();
            for (char letter = MIN_LETTER; letter <= MAX_LETTER; letter++) {
                row.put(letter, new Cell(letter, number));
            }
            board.put(number, row);
        }
        createPieces(WHITE);
        createPieces(BLACK);
        setupPieces(WHITE);
        setupPieces(BLACK);
    }

    public void update(char fromX, byte fromY, char toX, byte toY) {
        swap(fromX, fromY, toX, toY);
        nextPossibleMoves.put(currentColorMoves, calculatePossibleMoves(currentColorMoves));
        Color opponentsColor = getOpponentsColor(currentColorMoves);
        nextPossibleMoves.put(opponentsColor, calculatePossibleMoves(opponentsColor));
    }

    private void swap(char fromX, byte fromY, char toX, byte toY) {
        Cell fromCell = board.get(fromY).get(fromX);
        Cell toCell = board.get(toY).get(toX);
        toCell.setPiece(fromCell.getPiece());
        fromCell.setPiece(null);
    }

    public boolean isGameOver() {
        if (isGameJustStarted()) {
            return false;
        }
        Piece king = currentColorMoves == WHITE ? blackKing : whiteKing;
        if (isCheck(king)) {
            System.out.print("\nCheck");
            if (calculatePossibleMoves(king).isEmpty() && !isPossibleRemoveCheck(king)) {
                System.out.println("mate");
                return true;
            }
        }
        return false;
    }

    private boolean isGameJustStarted() {
        return nextPossibleMoves.isEmpty() || nextPossibleMoves.size() < 2;
    }

    private boolean isPossibleRemoveCheck(Piece king) {
        Map<Cell, Set<Cell>> possibleMoves = calculatePossibleMoves(king.getColor());
        for (Cell from : possibleMoves.keySet()) {
            for (Cell to : possibleMoves.get(from)) {
                Piece piece = board.get(to.getY()).get(to.getX()).getPiece();
                update(from.getX(), from.getY(), to.getX(), to.getY());
                if (isCheck(king)) {
                    update(to.getX(), to.getY(), from.getX(), from.getY());
                    to.setPiece(piece);
                } else {
                    update(to.getX(), to.getY(), from.getX(), from.getY());
                    to.setPiece(piece);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isCheck(Piece king) {
        if (isGameJustStarted()) {
            return false;
        }
        Map<Cell, Set<Cell>> nextOpponentsMoves = nextPossibleMoves.get(getOpponentsColor(king.getColor()));
        for (Cell from : nextOpponentsMoves.keySet()) {
            for (Cell to : nextOpponentsMoves.get(from)) {
                if (to.getPiece() != null && to.getPiece().equals(king)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Color getOpponentsColor(Color currentColor) {
        return currentColor == WHITE ? BLACK : WHITE;
    }

    public void display() {
        System.out.println();
        printAxis();
        for (Byte number : board.keySet()) {
            printSplitter();
            System.out.print(number + " ");
            for (Character letter : board.get(number).keySet()) {
                System.out.print("| " + getDisplayName(board.get(number).get(letter)) + " ");
            }
            System.out.print("| " + number);
        }
        printSplitter();
        printAxis();
        System.out.println();
    }

    public boolean isMovementAvailable(Color color, char fromX, byte fromY, char toX, byte toY) {
        boolean isPossibleMovement = calculatePossibleMoves(color).get(new Cell(fromX, fromY)).contains(new Cell(toX, toY));
        return isFromValid(fromX, fromY) && isCellPlaceValid(toX, toY) && isPossibleMovement;
    }

    public Map<Cell, Set<Cell>> calculatePossibleMoves(Color color) {
        Map<Cell, Set<Cell>> possibleMoves = new HashMap<>();
        for (Piece piece : pieces.get(color)) {
            possibleMoves.put(findCell(piece), calculatePossibleMoves(piece));
        }
        return possibleMoves;
    }

    private void createPieces(Color color) {
        List<Piece> coloredPieces = new ArrayList<>();

        Piece leftRook = new Piece(ROOK, color);
        Piece leftKnight = new Piece(KNIGHT, color);
        Piece leftBishop = new Piece(BISHOP, color);
        Piece queen = new Piece(QUEEN, color);
        Piece king = new Piece(KING, color);
        Piece rightBishop = new Piece(BISHOP, color);
        Piece rightKnight = new Piece(KNIGHT, color);
        Piece rightRok = new Piece(ROOK, color);

        coloredPieces.add(leftRook);
        coloredPieces.add(leftKnight);
        coloredPieces.add(leftBishop);
        coloredPieces.add(queen);
        coloredPieces.add(king);
        coloredPieces.add(rightBishop);
        coloredPieces.add(rightKnight);
        coloredPieces.add(rightRok);

        for (char i = MIN_LETTER; i <= MAX_LETTER; i++) {
            Piece pawn = new Piece(PAWN, color);
            coloredPieces.add(pawn);
        }

        pieces.put(color, coloredPieces);
    }

    private void setupPieces(Color color) {
        List<Piece> pieces = this.pieces.get(color);
        byte number = (byte) (color == WHITE ? MIN_NUMBER : MAX_NUMBER);
        byte pawnNumber = (byte) (color == WHITE ? MIN_NUMBER + 1 : MAX_NUMBER - 1);
        byte index = 0;
        byte pawnIndex = MAX_NUMBER;

        for (char i = MIN_LETTER; i <= MAX_LETTER; i++) {
            Piece piece = pieces.get(index);
            if (piece.getType() == KING) {
                if (piece.color == WHITE) {
                    whiteKing = piece;
                } else {
                    blackKing = piece;
                }
            }
            board.get(number).get(i).setPiece(piece); // king, queen, etc...
            index++;
            board.get(pawnNumber).get(i).setPiece(pieces.get(pawnIndex++));   // king, queen, etc...
        }
    }

    private String getDisplayName(Cell cell) {
        String value = " ";
        if (cell.getPiece() != null) {
            if (cell.getPiece().getColor() == BLACK) {
                value = cell.getPiece().getType().displayName;
            } else {
                value = cell.getPiece().getType().displayName.toLowerCase();
            }
        }
        return value;
    }

    private void printAxis() {
        System.out.print("  ");
        for (char letter = MIN_LETTER; letter <= MAX_LETTER; letter++) {
            System.out.print("  " + letter + " ");
        }
        System.out.print("  ");
    }

    private void printSplitter() {
        System.out.println();
        System.out.print("  ");
        for (byte i = MIN_NUMBER; i <= MAX_NUMBER; i++) {
            System.out.print("+---");
        }
        System.out.print("+  ");
        System.out.println();
    }

    private boolean isCellPlaceValid(char x, byte y) {
        return !(board.get(y) == null || board.get(y).get(x) == null);
    }

    private boolean isFromValid(char fromX, byte fromY) {
        return isCellPlaceValid(fromX, fromY) && board.get(fromY).get(fromX).getPiece() != null;
    }

    private Set<Cell> calculatePossibleMoves(Piece piece) {
        Cell cell = findCell(piece);
        Set<Cell> possibleMoves = new HashSet<>();
        if (cell != null) {
            switch (piece.getType()) {
                case KING:
                    Arrays.asList(Direction.values()).stream().forEach(d ->
                            findMovesOnDirection(0, 1, d, cell.getX(), cell.getY(), cell, possibleMoves));
                    break;
                case QUEEN:
                    Arrays.asList(Direction.values()).stream().forEach(d ->
                            findMovesOnDirection(0, 7, d, cell.getX(), cell.getY(), cell, possibleMoves));
                    break;
                case BISHOP:
                    findMovesOnDirection(0, 7, NORTH_EAST, cell.getX(), cell.getY(), cell, possibleMoves);
                    findMovesOnDirection(0, 7, SOUTH_EAST, cell.getX(), cell.getY(), cell, possibleMoves);
                    findMovesOnDirection(0, 7, SOUTH_WEST, cell.getX(), cell.getY(), cell, possibleMoves);
                    findMovesOnDirection(0, 7, NORTH_WEST, cell.getX(), cell.getY(), cell, possibleMoves);
                    break;
                case KNIGHT:
                    findMovesForKnight(NORTH, cell, possibleMoves);
                    findMovesForKnight(EAST, cell, possibleMoves);
                    findMovesForKnight(SOUTH, cell, possibleMoves);
                    findMovesForKnight(WEST, cell, possibleMoves);
                    break;
                case ROOK:
                    findMovesOnDirection(0, 7, NORTH, cell.getX(), cell.getY(), cell, possibleMoves);
                    findMovesOnDirection(0, 7, EAST, cell.getX(), cell.getY(), cell, possibleMoves);
                    findMovesOnDirection(0, 7, SOUTH, cell.getX(), cell.getY(), cell, possibleMoves);
                    findMovesOnDirection(0, 7, WEST, cell.getX(), cell.getY(), cell, possibleMoves);
                    break;
                default:
                    findMovesForPawn(cell, possibleMoves);
            }

        }

        return possibleMoves;
    }

    private void findMovesForKnight(Direction d, Cell cell, Set<Cell> possibleMoves) {
        if (d.xOffset == 0) {
            char possibleX1 = (char) (cell.getX() + 1);
            char possibleX2 = (char) (cell.getX() - 1);
            byte possibleY = (byte) (cell.getY() + 2 * d.yOffset);
            checkCell(possibleX1, possibleY, cell, possibleMoves);
            checkCell(possibleX2, possibleY, cell, possibleMoves);
        } else if (d.yOffset == 0) {
            char possibleX = (char) (2 * cell.getX());
            byte possibleY1 = (byte) (cell.getY() + 1);
            byte possibleY2 = (byte) (cell.getY() - 1);
            checkCell(possibleX, possibleY1, cell, possibleMoves);
            checkCell(possibleX, possibleY2, cell, possibleMoves);
        }
    }

    private void checkCell(char x, byte y, Cell cell, Set<Cell> possibleMoves) {
        if (isCellFree(x, y)) {
            possibleMoves.add(board.get(y).get(x));
        } else if (isOpponent(cell, x, y)) {
            possibleMoves.add(board.get(y).get(x));
        }
    }

    private void findMovesForPawn(Cell cell, Set<Cell> possibleMoves) {
        if (cell.getPiece().getColor() == WHITE) {
            if (isCellFree(cell.getX(), (byte) (cell.getY() + 1))) {
                possibleMoves.add(board.get((byte) (cell.getY() + 1)).get(cell.getX()));
                if (cell.getY() == 2 && isCellFree(cell.getX(), (byte) (cell.getY() + 2))) {
                    possibleMoves.add(board.get((byte) (cell.getY() + 2)).get(cell.getX()));
                }
            }
            if (isOpponent(cell, (char) (cell.getX() + 1), (byte) (cell.getY() + 1))) {
                possibleMoves.add(board.get((byte) (cell.getY() + 1)).get((char) (cell.getX() + 1)));
            }
            if (isOpponent(cell, (char) (cell.getX() - 1), (byte) (cell.getY() + 1))) {
                possibleMoves.add(board.get((byte) (cell.getY() + 1)).get((char) (cell.getX() - 1)));
            }
        } else if (cell.getPiece().getColor() == BLACK) {
            if (isCellFree(cell.getX(), (byte) (cell.getY() - 1))) {
                possibleMoves.add(board.get((byte) (cell.getY() - 1)).get(cell.getX()));
                if (cell.getY() == 7 && isCellFree(cell.getX(), (byte) (cell.getY() - 2))) {
                    possibleMoves.add(board.get((byte) (cell.getY() - 2)).get(cell.getX()));
                }
            }
            if (isOpponent(cell, (char) (cell.getX() + 1), (byte) (cell.getY() - 1))) {
                possibleMoves.add(board.get((byte) (cell.getY() - 1)).get((char) (cell.getX() + 1)));
            }
            if (isOpponent(cell, (char) (cell.getX() - 1), (byte) (cell.getY() - 1))) {
                possibleMoves.add(board.get((byte) (cell.getY() - 1)).get((char) (cell.getX() - 1)));
            }
        }
    }

    private void findMovesOnDirection(int depth, int maxDepth, Direction direction, char x, byte y, Cell cellWithPiece, Set<Cell> possibleMoves) {
        if (depth == maxDepth) {
            return;
        }
        char possibleX = (char) (x + direction.xOffset);
        byte possibleY = (byte) (y + direction.yOffset);
        if (isCellFree(possibleX, possibleY)) {
            if (cellWithPiece.getPiece().getType() == KING) {
                if (!isCheck(cellWithPiece.getPiece())) {
                    possibleMoves.add(board.get(possibleY).get(possibleX));
                }
            } else {
                possibleMoves.add(board.get(possibleY).get(possibleX));
                findMovesOnDirection(depth + 1, maxDepth, direction, possibleX, possibleY, cellWithPiece, possibleMoves);
            }
        } else if (isOpponent(cellWithPiece, possibleX, possibleY)) {
            if (cellWithPiece.getPiece().getType() == KING) {
                if (!isCheck(cellWithPiece.getPiece())) {
                    possibleMoves.add(board.get(possibleY).get(possibleX));
                }
            } else {
                possibleMoves.add(board.get(possibleY).get(possibleX));
            }
        }

    }

    private boolean isCellFree(char x, byte y) {
        if (x < MIN_LETTER || MAX_LETTER < x || y < MIN_NUMBER || MAX_NUMBER < y) {
            return false;
        }
        Map<Character, Cell> column = board.get(y);
        return column != null && column.get(x) != null && column.get(x).getPiece() == null;
    }

    private boolean isOpponent(Cell cell, char x, byte y) {
        return !(x < MIN_LETTER || MAX_LETTER < x || y < MIN_NUMBER || MAX_NUMBER < y ||
                board.get(y) == null || board.get(y).get(x) == null || board.get(y).get(x).getPiece() == null) &&
                board.get(y).get(x).getPiece().getColor() != cell.getPiece().getColor();

//        if (x < MIN_LETTER || MAX_LETTER < x || y < MIN_NUMBER || MAX_NUMBER < y ||
//                board.get(y) == null || board.get(y).get(x) == null || board.get(y).get(x).getPiece() == null) {
//            return false;
//        }
//        return board.get(y).get(x).getPiece().getColor() != cell.getPiece().getColor();
    }

    private Cell findCell(Piece piece) {
        for (Byte number : board.keySet()) {
            for (Character letter : board.get(number).keySet()) {
                Cell cell = board.get(number).get(letter);
                if (piece != null && piece.equals(cell.getPiece())) {
                    return cell;
                }
            }
        }
        return null;
    }

    public void setCurrentColorMoves(Color currentColorMoves) {
        this.currentColorMoves = currentColorMoves;
    }

// northWest (-1,+1)    north (0,1)     northEast (+1,+1)
// west (-1,0)          Piece           east (+1,0)
// southWest (-1,-1)    south (0,-1)    southEast (+1,-1)

    static class Cell {
        private final char x;
        private final byte y;
        private Piece piece;

        public Cell(char x, byte y) {
            this.x = x;
            this.y = y;
        }

        public char getX() {
            return x;
        }

        public byte getY() {
            return y;
        }

        public Piece getPiece() {
            return piece;
        }

        public void setPiece(Piece piece) {
            this.piece = piece;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Cell cell = (Cell) o;

            return x == cell.x && y == cell.y;

        }

        @Override
        public int hashCode() {
            int result = (int) x;
            result = 31 * result + (int) y;
            return result;
        }
    }

    enum Direction {
        NORTH(0, 1),
        NORTH_EAST(1, 1),
        EAST(1, 0),
        SOUTH_EAST(1, -1),
        SOUTH(0, -1),
        SOUTH_WEST(-1, -1),
        WEST(-1, 0),
        NORTH_WEST(-1, 1);
        public final int xOffset;
        public final int yOffset;

        Direction(int xOffset, int yOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
}
