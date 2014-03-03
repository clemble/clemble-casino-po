package com.clemble.casino.po.integration.emulation;

import com.clemble.casino.base.ExpectedEvent;
import com.clemble.casino.event.Event;
import com.clemble.casino.game.Game;
import com.clemble.casino.game.action.BetAction;
import com.clemble.casino.game.action.SelectAction;
import com.clemble.casino.game.specification.GameConfiguration;
import com.clemble.casino.integration.emulator.RoundGamePlayerActor;
import com.clemble.casino.integration.game.RoundGamePlayer;
import com.clemble.casino.po.PoBoard;
import com.clemble.casino.po.PoState;
import com.clemble.casino.po.integration.emulation.PoRoundPlayer;

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
        if (nextMove instanceof SelectAction || (nextMove instanceof ExpectedEvent && ((ExpectedEvent) nextMove).getAction().equalsIgnoreCase("select"))) {
            selectSquare(player);
        }
        if (nextMove instanceof BetAction || (nextMove instanceof ExpectedEvent && ((ExpectedEvent) nextMove).getAction().equalsIgnoreCase("bet"))) {
            bet(player);
        }
    }

    private void selectSquare(PoRoundPlayer player) {
        // Step 1.1 Select move to be made
        PoBoard board = player.getState().getRoot();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!board.owned(i, j)) {
                    player.select(i, j);
                    return;
                }
            }
        }
        throw new IllegalArgumentException("This action can't be performed");
    }

    private void bet(PoRoundPlayer player) {
        // Step 1.2 Bet move to be made
        if (player.getMoneyLeft() > 0) {
            player.bet(2);
        } else {
            player.bet(0);
        }
    }

}
