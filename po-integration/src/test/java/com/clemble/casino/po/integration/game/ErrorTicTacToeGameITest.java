package com.clemble.casino.po.integration.game;

import java.util.List;

import com.clemble.casino.integration.game.RoundGamePlayer;
import com.clemble.casino.po.PoState;
import com.clemble.casino.po.integration.emulation.PoRoundPlayer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.clemble.casino.error.ClembleCasinoError;
import com.clemble.casino.game.Game;
import com.clemble.casino.integration.game.construction.GameScenarios;
import com.clemble.casino.integration.util.ClembleCasinoExceptionMatcherFactory;
import com.clemble.casino.integration.util.RedisCleaner;
import com.clemble.casino.po.spring.integration.PoTestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { PoTestConfiguration.class })
@TestExecutionListeners(listeners = { RedisCleaner.class, DependencyInjectionTestExecutionListener.class })
public class ErrorTicTacToeGameITest {

    @Autowired
    public GameScenarios gameScenarios;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void wrongMove() {
        // Step 1. Creating game
        List<RoundGamePlayer<PoState>> players = gameScenarios.match(Game.pic);
        PoRoundPlayer pA = (PoRoundPlayer) players.get(0);
        PoRoundPlayer pB = (PoRoundPlayer) players.get(1);
        // Step 2. Making some legal moves
        pA.select(1, 1);
        pA.bet(3);
        pB.bet(3);
        // Step 3. Making illegal move
        expectedException.expect(ClembleCasinoExceptionMatcherFactory.fromErrors(ClembleCasinoError.GamePlayWrongMoveType));
        pB.bet(4);
    }

    @Test
    public void noMoveExpected() {
        // Step 1. Creating game
        List<RoundGamePlayer<PoState>> players = gameScenarios.match(Game.pic);
        PoRoundPlayer pA = (PoRoundPlayer) players.get(0);
        PoRoundPlayer pB = (PoRoundPlayer) players.get(1);
        // Step 2. Making some legal moves
        pA.select(1, 1);
        pA.bet(3);
        pB.bet(3);
        // Step 3. Making illegal move
        expectedException.expect(ClembleCasinoExceptionMatcherFactory.fromErrors(ClembleCasinoError.GamePlayNoMoveExpected));
        pA.select(1, 2);
    }

}
