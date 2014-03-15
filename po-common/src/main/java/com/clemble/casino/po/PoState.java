package com.clemble.casino.po;

import com.clemble.casino.game.*;
import com.clemble.casino.game.action.GameAction;
import com.clemble.casino.game.event.server.*;
import com.clemble.casino.game.unit.GameUnit;
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


}
