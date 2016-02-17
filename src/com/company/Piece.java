package com.company;

public class Piece {
    protected final Type type;
    protected final Color color;

    public Piece(Type type, Color color) {
        this.type = type;
        this.color = color;
    }

    enum Type {
        KING("K"), QUEEN("Q"), BISHOP("B"), KNIGHT("N"), ROOK("R"), PAWN("P");

        public final String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }

    public Type getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }
}
