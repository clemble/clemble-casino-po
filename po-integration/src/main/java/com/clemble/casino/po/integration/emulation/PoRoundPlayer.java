package com.clemble.casino.po.integration.emulation;

import java.util.concurrent.atomic.AtomicInteger;

import com.clemble.casino.game.action.BetAction;
import com.clemble.casino.game.action.SelectAction;
import com.clemble.casino.game.cell.Cell;
import com.clemble.casino.integration.game.GenericRoundGamePlayer;
import com.clemble.casino.integration.game.RoundGamePlayer;
import com.clemble.casino.po.PoState;

public class PoRoundPlayer extends GenericRoundGamePlayer<PoState> {

    /**
     * Generated 04/07/13
     */
    private static final long serialVersionUID = -2100664121282462477L;

    final private AtomicInteger moneySpent = new AtomicInteger();

    public PoRoundPlayer(final RoundGamePlayer<PoState> delegate) {
        super(delegate);
    }

    public void select(int row, int column) {
        perform(new SelectAction<Cell>(actualPlayer.playerOperations().getPlayer(), Cell.create(row, column)));
    }

    public void bet(int amount) {
        perform(new BetAction(actualPlayer.playerOperations().getPlayer(), amount));
        moneySpent.getAndAdd(-amount);
    }

    public long getMoneyLeft() {
        return getState().getContext().getPlayerContext(playerOperations().getPlayer()).getAccount().getLeft();
    }

    public int getMoneySpent() {
        return moneySpent.get();
    }

}
