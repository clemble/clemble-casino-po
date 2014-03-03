package com.clemble.casino.po.integration.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import com.clemble.casino.integration.game.RoundGamePlayer;
import com.clemble.casino.po.PoState;
import com.clemble.casino.po.integration.emulation.PoRoundPlayer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.game.Game;
import com.clemble.casino.game.cell.CellState;
import com.clemble.casino.game.outcome.PlayerWonOutcome;
import com.clemble.casino.integration.game.construction.GameScenarios;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.util.RedisCleaner;
import com.clemble.casino.player.PlayerAwareUtils;
import com.clemble.casino.po.spring.integration.PoTestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { PoTestConfiguration.class })
@TestExecutionListeners(listeners = { RedisCleaner.class, DependencyInjectionTestExecutionListener.class })
public class CornerCases {

    @Autowired
    public GameScenarios gameScenarios;

    @Autowired
    public PlayerScenarios playerScenarios;

    @Test
    public void betEverything() {
        // Step 1. Creating game
        List<RoundGamePlayer<PoState>> players = gameScenarios.match(Game.pic);
        PoRoundPlayer pA = (PoRoundPlayer) players.get(0);
        PoRoundPlayer pB = (PoRoundPlayer) players.get(1);
        // Step 2. Making some legal moves
        pA.select(1, 1);
        pA.bet(36);
        pB.bet(1);
        // Step 3. Waiting for game to end
        pB.waitForEnd();
        assertEquals(((PlayerWonOutcome) pB.getOutcome()).getWinner(), pB.getPlayer());
    }

    @Test
    public void betTwice() {
        // Step 1. Creating game
        List<RoundGamePlayer<PoState>> players = gameScenarios.match(Game.pic);
        PoRoundPlayer pA = (PoRoundPlayer) players.get(0);
        PoRoundPlayer pB = (PoRoundPlayer) players.get(1);
        // Step 2. Making some legal moves
        pA.select(1, 1);
        pA.bet(1);
        pB.bet(1);
        // Step 3. Waiting for game to end
        pB.select(1, 1);
        pA.bet(2);
        pB.bet(3);
        CellState cellState = pB.getState().getRoot().getBoard()[1][1];
        assertEquals(cellState.getBet(pA.getPlayer()), 3);
        assertEquals(cellState.getBet(pB.getPlayer()), 4);
    }

    @Test
    public void testPlayersRematched(){
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        ClembleCasinoOperations B = playerScenarios.createPlayer();

        RoundGamePlayer<PoState> pA = gameScenarios.match(Game.pic, A);
        RoundGamePlayer<PoState> pB = gameScenarios.match(Game.pic, B);

        pA.waitForStart();
        assertTrue(pA.isAlive());

        Collection<String> ps = PlayerAwareUtils.toPlayerList(pA.getState().getContext().getPlayerContexts());
        assertTrue(ps.contains(A.getPlayer()));
        assertTrue(ps.contains(B.getPlayer()));

        pA.giveUp();

        pA.waitForEnd();
        pB.waitForEnd();

        pA = gameScenarios.match(Game.pic, A);
        pB = gameScenarios.match(Game.pic, B);

        pA.waitForStart();
        assertTrue(pA.isAlive());

        ps = PlayerAwareUtils.toPlayerList(pA.getState().getContext().getPlayerContexts());
        assertTrue(ps.contains(A.getPlayer()));
        assertTrue(ps.contains(B.getPlayer()));

        pA.giveUp();
    }
}
