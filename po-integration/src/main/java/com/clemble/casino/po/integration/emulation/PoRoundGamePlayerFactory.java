package com.clemble.casino.po.integration.emulation;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.game.Game;
import com.clemble.casino.game.GameSessionKey;
import com.clemble.casino.game.construct.GameConstruction;
import com.clemble.casino.game.specification.GameConfigurationKey;
import com.clemble.casino.integration.game.RoundGamePlayer;
import com.clemble.casino.integration.game.RoundGamePlayerFactory;
import com.clemble.casino.po.PoState;

public class PoRoundGamePlayerFactory implements RoundGamePlayerFactory<PoState> {

    /**
     * Generated 19/11/13
     */
    private static final long serialVersionUID = -7233942016220846337L;
    final private RoundGamePlayerFactory<PoState> sessionPlayerFactory;

    public PoRoundGamePlayerFactory(RoundGamePlayerFactory<PoState> gameSessionPlayerFactory) {
        this.sessionPlayerFactory = gameSessionPlayerFactory;
    }

    @Override
    public RoundGamePlayer<PoState> construct(ClembleCasinoOperations player, GameConstruction construction) {
        return new PoRoundPlayer(sessionPlayerFactory.construct(player, construction));
    }

    @Override
    public RoundGamePlayer<PoState> construct(ClembleCasinoOperations player, GameSessionKey construction) {
        return new PoRoundPlayer(sessionPlayerFactory.construct(player, construction));
    }

    @Override
    public Game getGame() {
        return Game.pic;
    }

    @Override
    public RoundGamePlayer<PoState> construct(ClembleCasinoOperations player, GameSessionKey sessionKey, GameConfigurationKey configurationKey) {
        return new PoRoundPlayer(sessionPlayerFactory.construct(player, sessionKey, configurationKey));
    }

}
