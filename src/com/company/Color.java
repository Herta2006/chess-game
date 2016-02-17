package com.company;

public enum Color {
    WHITE("White"), BLACK("Black");
    public final String displayName;

    Color(String displayName) {
        this.displayName = displayName;
    }
}
