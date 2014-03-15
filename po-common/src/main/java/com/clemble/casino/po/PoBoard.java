package com.clemble.casino.po;

import com.clemble.casino.game.unit.GameUnit;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PoBoard implements GameUnit {

    private static final long serialVersionUID = -3282042914639667829L;

    final private PoCell[][] board;

    @JsonCreator
    public PoBoard(@JsonProperty("board") PoCell[][] board) {
        this.board = board;
    }

    public PoCell[][] getBoard() {
        return board;
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
