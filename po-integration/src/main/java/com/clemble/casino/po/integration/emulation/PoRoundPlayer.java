package com.clemble.casino.po.integration.emulation;

import java.util.concurrent.atomic.AtomicInteger;

import com.clemble.casino.ImmutablePair;
import com.clemble.casino.game.unit.Chip;
import com.clemble.casino.integration.game.GenericRoundGamePlayer;
import com.clemble.casino.integration.game.RoundGamePlayer;
import com.clemble.casino.po.PoState;
import com.clemble.casino.po.action.*;

public class PoRoundPlayer extends GenericRoundGamePlayer<PoState> {

    /**
     * Generated 04/07/13
     */
    private static final long serialVersionUID = -2100664121282462477L;

    final private AtomicInteger moneySpent = new AtomicInteger();

    public PoRoundPlayer(final RoundGamePlayer<PoState> delegate) {
        super(delegate);
    }

    public void move(int fromRow, int fromColumn, int toRow, int toColumn) {
        // Step 1. Generating from and to
        ImmutablePair<Integer, Integer> from = new ImmutablePair<>(fromRow, fromColumn);
        ImmutablePair<Integer, Integer> to = new ImmutablePair<>(toRow, toColumn);
        // Step 2. Generating move action
        MoveChipAction moveAction = new MoveChipAction(getPlayer(), from, to);
        // Step 3. Actually performing action
        perform(moveAction);
    }

    public void increase(int row, int column) {
        // Step 1. Generating immutable pair to represent row and column
        ImmutablePair<Integer, Integer> from = new ImmutablePair<Integer, Integer>(row, column);
        // Step 2. Generating increase event
        IncreaseChipAction action = new IncreaseChipAction(getPlayer(), from);
        // Step 3. Actually performing action
        perform(action);
    }

    public void decrease(int row, int column) {
        // Step 1. Generating immutable pair to represent row and column
        ImmutablePair<Integer, Integer> from = new ImmutablePair<Integer, Integer>(row, column);
        // Step 2. Generating increase event
        DecreaseChipAction action = new DecreaseChipAction(getPlayer(), from);
        // Step 3. Actually performing action
        perform(action);
    }

    public void place(Chip chip, int row, int column) {
        // Step 1. Generating immutable pair to represent row and column
        ImmutablePair<Integer, Integer> from = new ImmutablePair<Integer, Integer>(row, column);
        // Step 2. Generating appropriate processing event
        PlaceChipAction action = new PlaceChipAction(getPlayer(), chip, from);
        // Step 3. Actually performing action
        perform(action);
    }

    public void combine(int row, int column) {
        // Step 1. Generating immutable pair to represent row and column
        ImmutablePair<Integer, Integer> from = new ImmutablePair<Integer, Integer>(row, column);
        // Step 2. Generating appropriate processing event
        CombineChipAction action = new CombineChipAction(getPlayer(), from);
        // Step 3. Actually performing action
        perform(action);
    }

    public long getMoneyLeft() {
        return getState().getContext().getPlayerContext(playerOperations().getPlayer()).getAccount().getLeft();
    }

    public int getMoneySpent() {
        return moneySpent.get();
    }

}
