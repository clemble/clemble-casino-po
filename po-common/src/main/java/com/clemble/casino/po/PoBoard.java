package com.clemble.casino.po;

import java.util.Arrays;

import com.clemble.casino.game.cell.Cell;
import com.clemble.casino.game.cell.CellBoard;
import com.clemble.casino.game.cell.CellState;
import com.clemble.casino.game.outcome.DrawOutcome;
import com.clemble.casino.game.outcome.GameOutcome;
import com.clemble.casino.game.outcome.PlayerWonOutcome;
import com.clemble.casino.game.unit.AbstractGameUnit;
import com.clemble.casino.game.unit.GameUnitUtils;
import com.clemble.casino.player.PlayerAware;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PoBoard extends AbstractGameUnit implements CellBoard {

    private static final long serialVersionUID = -3282042914639667829L;

    private CellState[][] board;

    private Cell selected;

    public PoBoard() {
        this(CellState.emptyBoard(3, 3), null);
    }

    @JsonCreator
    public PoBoard(@JsonProperty("board") CellState[][] board, @JsonProperty("selected") Cell selected) {
        super(GameUnitUtils.toList(board, selected));
        this.selected = selected;
        this.board = board;
    }

    @Override
    public CellState[][] getBoard() {
        return board;
    }

    public Cell firstFree() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; i < board.length; j++) {
                if (!board[i][j].owned()) {
                    return Cell.create(i, j);
                }
            }
        }
        return null;
    }

    public void markEmpty(String owner) {
        CellState ownedCell = new CellState(owner);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (!board[i][j].owned()) {
                    board[i][j] = ownedCell;
                }
            }
        }
    }

    @Override
    public boolean owned(int row, int column) {
        return board[row][column].owned();
    }

    public Cell getSelected() {
        return selected;
    }

    public void setSelected(Cell cell) {
        selected = cell;
    }

    public void setSelectedState(CellState cellState) {
        if (selected != null) {
            board[selected.getRow()][selected.getColumn()] = 
                    board[selected.getRow()][selected.getColumn()].merge(cellState);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(board);
        result = prime * result + ((selected == null) ? 0 : selected.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PoBoard other = (PoBoard) obj;
        if (!Arrays.deepEquals(board, other.board))
            return false;
        if (selected == null) {
            if (other.selected != null)
                return false;
        } else if (!selected.equals(other.selected))
            return false;
        return true;
    }

    public static PoBoard create() {
        // Step 1. Creating default board
        CellState[][] board = new CellState[3][3];
        for (CellState[] row : board)
            Arrays.fill(row, CellState.DEFAULT);
        // Step 2. Returning new state
        return new PoBoard(board, Cell.DEFAULT);
    }

    @JsonCreator
    public static PoBoard create(@JsonProperty("board") CellState[][] board, @JsonProperty("selected") Cell selected) {
        return new PoBoard(board, selected);
    }

    public static GameOutcome fetchOutcome(PoBoard state) {
        // Step 1. Checking if there is a single winner
        String winner = fetchWinner(state);
        if (!winner.equals(PlayerAware.DEFAULT_PLAYER)) {
            return new PlayerWonOutcome(winner);
        }
        // Step 2. Checking if game ended in draw
        if (!canHaveWinner(state)) {
            return new DrawOutcome();
        }
        return null;
    }

    public static String fetchWinner(PoBoard state) {
        CellState[][] board = state.board;
        String winner = fetcSingleOwner(board[0][0], board[0][1], board[0][2]); // Checking rows
        winner = winner != PlayerAware.DEFAULT_PLAYER ? winner : fetcSingleOwner(board[1][0], board[1][1], board[1][2]);
        winner = winner != PlayerAware.DEFAULT_PLAYER ? winner : fetcSingleOwner(board[2][0], board[2][1], board[2][2]);
        winner = winner != PlayerAware.DEFAULT_PLAYER ? winner : fetcSingleOwner(board[0][0], board[1][0], board[2][0]); // Checking columns
        winner = winner != PlayerAware.DEFAULT_PLAYER ? winner : fetcSingleOwner(board[0][1], board[1][1], board[2][1]);
        winner = winner != PlayerAware.DEFAULT_PLAYER ? winner : fetcSingleOwner(board[0][2], board[1][2], board[2][2]);
        winner = winner != PlayerAware.DEFAULT_PLAYER ? winner : fetcSingleOwner(board[0][0], board[1][1], board[2][2]); // Checking diagonals
        winner = winner != PlayerAware.DEFAULT_PLAYER ? winner : fetcSingleOwner(board[0][2], board[1][1], board[2][0]);
        return winner;
    }

    private static String fetcSingleOwner(CellState firstCell, CellState secondCell, CellState therdCell) {
        return firstCell.getOwner().equals(secondCell.getOwner()) && secondCell.getOwner().equals(therdCell.getOwner()) ? firstCell.getOwner()
                : PlayerAware.DEFAULT_PLAYER;
    }

    public static boolean canHaveWinner(PoBoard state) {
        CellState[][] board = state.board;
        return canHaveSingleOwner(board[0][0], board[0][1], board[0][2]) || canHaveSingleOwner(board[1][0], board[1][1], board[1][2])
                || canHaveSingleOwner(board[2][0], board[2][1], board[2][2])
                // Checking columns
                || canHaveSingleOwner(board[0][0], board[1][0], board[2][0]) || canHaveSingleOwner(board[0][1], board[1][1], board[2][1])
                || canHaveSingleOwner(board[0][2], board[1][2], board[2][2])
                // Checking diagonals
                || canHaveSingleOwner(board[0][0], board[1][1], board[2][2]) || canHaveSingleOwner(board[0][2], board[1][1], board[2][0]);
    }

    private static boolean canHaveSingleOwner(CellState firstCell, CellState secondCell, CellState therdCell) {
        // In arbitrary line XOX if X is free, it can be owned by O, if O is free it also can be owned by X
        return (firstCell.getOwner() == secondCell.getOwner() && (!firstCell.owned() || !therdCell.owned()))
                || (secondCell.getOwner() == therdCell.getOwner() && (!secondCell.owned() || !firstCell.owned()))
                || (therdCell.getOwner() == firstCell.getOwner() && (!therdCell.owned() || !secondCell.owned()));
    }

}
