package com.clemble.casino.po;

import com.clemble.casino.ImmutablePair;
import com.clemble.casino.game.unit.Chip;
import com.clemble.casino.game.unit.GameUnit;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PoBoard implements GameUnit {

    private static final long serialVersionUID = -3282042914639667829L;

    final private Chip[][] board;
    final private QuickUnion connectivity;

    @JsonCreator
    public PoBoard(@JsonProperty("board") Chip[][] board) {
        this.board = board;
        this.connectivity = new QuickUnion(board.length * board.length);
    }

    public Chip[][] getBoard() {
        return board;
    }

    public int setCell(ImmutablePair<Integer, Integer> location, Chip chip) {
        int row = location.getKey();
        int column = location.getValue();
        board[row][column] = chip;
        int size = 0;

        if (row > 0 && board[row - 1][column] == chip)
            size = Math.max(connectivity.union(row * board.length + column, (row - 1) * board.length + column), size);
        if (row < board.length - 2 && board[row + 1][column] == chip)
            size = Math.max(connectivity.union(row * board.length + column, (row + 1) * board.length + column), size);

        if (column > 0 && board[row][column - 1] == chip)
            size = Math.max(connectivity.union(row * board.length + column, row * board.length + column - 1), size);
        if (column < board.length - 2 && board[row][column + 1] == chip)
            size = Math.max(connectivity.union(row * board.length + column, row * board.length + column + 1), size);

        return size;
    }

    public Chip getCell(ImmutablePair<Integer, Integer> cell) {
        return getCell(cell.getKey(), cell.getValue());
    }

    public Chip getCell(int row, int column) {
        return board[row][column];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PoBoard)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
