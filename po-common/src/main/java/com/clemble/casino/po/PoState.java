package com.clemble.casino.po;

import com.clemble.casino.ImmutablePair;
import com.clemble.casino.error.ClembleCasinoError;
import com.clemble.casino.error.ClembleCasinoException;
import com.clemble.casino.game.*;
import com.clemble.casino.game.action.GameAction;
import com.clemble.casino.game.event.server.*;
import com.clemble.casino.game.unit.Chip;
import com.clemble.casino.game.unit.GameUnit;
import com.clemble.casino.po.action.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("po")
public class PoState implements RoundGameState {

    /**
     * Generated 28/11/13
     */
    private static final long serialVersionUID = -755717572685487667L;

    final private RoundGameContext context;
    final private PoBoard board;
    final private int version;

    @JsonCreator
    public PoState(
            @JsonProperty("context") RoundGameContext context,
            @JsonProperty("root") PoBoard board,
            @JsonProperty("version") int version) {
        this.context = context;
        this.board = board;
        this.version = version;
    }

    @Override
    public GameManagementEvent process(GameAction action) {
        GamePlayerContext playerContext = context.getPlayerContext(action.getPlayer());
        ImmutablePair<Integer, Integer> affectedCell = null;
        // Step 1. Processing shift action
        if (action instanceof ShiftChipAction) {
            Chip fromCell = board.getCell(((ShiftChipAction) action).getFrom());
            Chip toCell = board.getCell(((ShiftChipAction) action).getTo());
            if (fromCell == Chip.zero && toCell == Chip.zero)
                throw ClembleCasinoException.fromError(ClembleCasinoError.GamePlayMoveInvalid);
            if (!playerContext.getUnits().use(((ShiftChipAction) action).getUnit()))
                throw ClembleCasinoException.fromError(ClembleCasinoError.GamePlayUnitMissing);
            board.setCell(((ShiftChipAction) action).getFrom(), toCell);
            board.setCell(((ShiftChipAction) action).getTo(), fromCell);
        }
        // Step 2. Processing increase action
        if (action instanceof IncreaseChipAction) {
            ImmutablePair<Integer, Integer> location = ((IncreaseChipAction) action).getFrom();
            Chip chip = board.getCell(location);
            if (chip == Chip.zero || chip == Chip.twenty)
                throw ClembleCasinoException.fromError(ClembleCasinoError.GamePlayMoveInvalid);;
            board.setCell(location, Chip.values()[chip.ordinal() + 1]);
        }
        // Step 3. Processing decrease action
        if (action instanceof DecreaseChipAction) {
            ImmutablePair<Integer, Integer> location = ((IncreaseChipAction) action).getFrom();
            Chip cell = board.getCell(location);
            if (cell == Chip.zero)
                throw ClembleCasinoException.fromError(ClembleCasinoError.GamePlayMoveInvalid);
            board.setCell(location, Chip.values()[cell.ordinal() - 1]);
        }
        // Step 4. Placing chip on the board
        if (action instanceof PlaceChipAction) {
            ImmutablePair<Integer, Integer> location = ((IncreaseChipAction) action).getFrom();
            Chip cell = board.getCell(location);
            if (cell == Chip.zero)
                throw ClembleCasinoException.fromError(ClembleCasinoError.GamePlayMoveInvalid);
            board.setCell(location, ((PlaceChipAction) action).getUnit());
        }
        // Step 5. Checking if there is 3 sequential values
        if (action instanceof CombineChipAction) {
            ImmutablePair<Integer, Integer> location = ((CombineChipAction) action).getFrom();

        }
        return null;
    }

    @Override
    public RoundGameContext getContext() {
        return context;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public GameUnit getState() {
        return board;
    }

    private boolean check(ImmutablePair<Integer, Integer> location) {

    }

}
