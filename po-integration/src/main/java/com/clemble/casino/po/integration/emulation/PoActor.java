package com.clemble.casino.po.integration.emulation;

import com.clemble.casino.event.Event;
import com.clemble.casino.game.Game;
import com.clemble.casino.game.specification.GameConfiguration;
import com.clemble.casino.integration.emulator.RoundGamePlayerActor;
import com.clemble.casino.integration.game.RoundGamePlayer;
import com.clemble.casino.po.PoState;

public class PoActor extends RoundGamePlayerActor<PoState> {

    /**
     * Generated 13/07/13
     */
    private static final long serialVersionUID = 8438692609799609799L;

    @Override
    public boolean canPlay(GameConfiguration configuration) {
        return Game.pic.equals(configuration.getConfigurationKey().getGame());
    }

    @Override
    public void doMove(RoundGamePlayer playerToMove) {
        PoRoundPlayer player = playerToMove instanceof PoRoundPlayer ? (PoRoundPlayer) playerToMove : new PoRoundPlayer(playerToMove);
        // Step 1. Checking next move
        Event nextMove = playerToMove.getNextMove();
    }

}
