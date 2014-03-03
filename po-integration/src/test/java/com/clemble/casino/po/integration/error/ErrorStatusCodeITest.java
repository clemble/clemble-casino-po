package com.clemble.casino.po.integration.error;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.clemble.casino.integration.game.RoundGamePlayer;
import com.clemble.casino.po.PoState;
import com.clemble.casino.po.integration.emulation.PoRoundPlayer;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.error.ClembleCasinoException;
import com.clemble.casino.game.Game;
import com.clemble.casino.integration.game.construction.GameScenarios;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.util.RedisCleaner;
import com.clemble.casino.po.spring.integration.PoTestConfiguration;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@TestExecutionListeners(listeners = { RedisCleaner.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(classes = { PoTestConfiguration.class })
public class ErrorStatusCodeITest {

    @Autowired
    public PlayerScenarios playerOperations;

    @Autowired
    public GameScenarios gameScenarios;

    @Test(expected = ClembleCasinoException.class)
    public void testSelectTwiceError() {
        List<RoundGamePlayer<PoState>> players = gameScenarios.match(Game.pic);
        PoRoundPlayer playerA = (PoRoundPlayer) players.get(0);

        playerA.select(0, 0);
        playerA.select(1, 1);
    }

    @Test(expected = ClembleCasinoException.class)
    public void testBetBig() {
        List<RoundGamePlayer<PoState>> players = gameScenarios.match(Game.pic);
        PoRoundPlayer A = (PoRoundPlayer) players.get(0);

        A.select(0, 0);
        A.bet(1000);
    }

    @Test
    public void testCreatingSimultaniousGames() {
        ClembleCasinoOperations A = playerOperations.createPlayer();

        RoundGamePlayer<PoState> gamePlayer = gameScenarios.match(Game.pic, A);
        RoundGamePlayer<PoState> anotherGamePlayer = gameScenarios.match(Game.pic, A);
        assertEquals(gamePlayer.getSession(), anotherGamePlayer.getSession());

        gamePlayer.close();
    }

}
