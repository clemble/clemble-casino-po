package com.clemble.casino.po;

import com.clemble.casino.game.GameTerminalChecker;
import com.clemble.casino.game.cell.CellState;
import com.clemble.casino.game.outcome.DrawOutcome;
import com.clemble.casino.game.outcome.GameOutcome;
import com.clemble.casino.game.outcome.PlayerWonOutcome;
import com.clemble.casino.player.PlayerAware;

/**
 * Created by mavarazy on 22/12/13.
 */
public class PoTerminalChecker implements GameTerminalChecker<PoBoard> {

    @Override
    public GameOutcome toOutcome(PoBoard state) {
        // Step 1. Checking if there is a single winner
        String winner = fetchWinner(state);
        if (!winner.equals(PlayerAware.DEFAULT_PLAYER)) {
            return new PlayerWonOutcome(winner);
        }
        // Step 2. Checking if game ended in draw
        if (!outcomePossible(state)) {
            return new DrawOutcome();
        }
        return null;
    }

    public String fetchWinner(PoBoard state) {
        CellState[][] board = state.getBoard();
        String winner = PlayerAware.DEFAULT_PLAYER;
        // Checking rows
        winner = winner != PlayerAware.DEFAULT_PLAYER ? winner : fetcSingleOwner(board[1][0], board[1][1], board[1][2]);
        winner = winner != PlayerAware.DEFAULT_PLAYER ? winner : fetcSingleOwner(board[2][0], board[2][1], board[2][2]);
        winner = winner != PlayerAware.DEFAULT_PLAYER ? winner : fetcSingleOwner(board[0][0], board[1][0], board[2][0]);
        // Checking columns
        winner = winner != PlayerAware.DEFAULT_PLAYER ? winner : fetcSingleOwner(board[0][1], board[1][1], board[2][1]);
        winner = winner != PlayerAware.DEFAULT_PLAYER ? winner : fetcSingleOwner(board[0][2], board[1][2], board[2][2]);
        winner = winner != PlayerAware.DEFAULT_PLAYER ? winner : fetcSingleOwner(board[0][0], board[1][1], board[2][2]);
        // Checking diagonals
        winner = winner != PlayerAware.DEFAULT_PLAYER ? winner : fetcSingleOwner(board[0][2], board[1][1], board[2][0]);
        winner = winner != PlayerAware.DEFAULT_PLAYER ? winner : fetcSingleOwner(board[0][0], board[1][1], board[2][2]);
        return winner;
    }

    private String fetcSingleOwner(CellState firstCell, CellState secondCell, CellState therdCell) {
        return firstCell.getOwner().equals(secondCell.getOwner()) && secondCell.getOwner().equals(therdCell.getOwner()) ? firstCell.getOwner() : PlayerAware.DEFAULT_PLAYER;
    }

    @Override
    public boolean outcomePossible(PoBoard state) {
        CellState[][] board = state.getBoard();
        return canHaveSingleOwner(board[0][0], board[0][1], board[0][2])
                || canHaveSingleOwner(board[1][0], board[1][1], board[1][2])
                || canHaveSingleOwner(board[2][0], board[2][1], board[2][2])
                // Checking columns
                || canHaveSingleOwner(board[0][0], board[1][0], board[2][0])
                || canHaveSingleOwner(board[0][1], board[1][1], board[2][1])
                || canHaveSingleOwner(board[0][2], board[1][2], board[2][2])
                // Checking diagonals
                || canHaveSingleOwner(board[0][0], board[1][1], board[2][2])
                || canHaveSingleOwner(board[0][2], board[1][1], board[2][0]);
    }

    private boolean canHaveSingleOwner(CellState firstCell, CellState secondCell, CellState therdCell) {
        // In arbitrary line XOX if X is free, it can be owned by O, if O is free it also can be owned by X
        return (firstCell.getOwner() == secondCell.getOwner() && (!firstCell.owned() || !therdCell.owned()))
                || (secondCell.getOwner() == therdCell.getOwner() && (!secondCell.owned() || !firstCell.owned()))
                || (therdCell.getOwner() == firstCell.getOwner() && (!therdCell.owned() || !secondCell.owned()));
    }
}
