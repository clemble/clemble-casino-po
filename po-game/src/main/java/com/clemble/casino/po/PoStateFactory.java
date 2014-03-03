package com.clemble.casino.po;

import com.clemble.casino.game.Game;
import com.clemble.casino.game.RoundGameContext;
import com.clemble.casino.game.construct.GameInitiation;
import com.clemble.casino.server.game.action.GameStateFactory;

public class PoStateFactory implements GameStateFactory<PoState>{

    public PoStateFactory() {
    }

    @Override
    public PoState constructState(GameInitiation initiation, RoundGameContext context) {
        // Step 1. Generating state
        return new PoState(context);
    }

    @Override
    public Game getGame() {
        return Game.pic;
    }

}
