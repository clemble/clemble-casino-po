package com.clemble.casino.po.integration.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.clemble.casino.integration.game.RoundGamePlayer;
import com.clemble.casino.integration.game.construction.SyncGameScenarios;
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

import com.clemble.casino.base.ExpectedEvent;
import com.clemble.casino.game.Game;
import com.clemble.casino.game.outcome.PlayerWonOutcome;
import com.clemble.casino.integration.game.construction.GameScenarios;
import com.clemble.casino.integration.util.RedisCleaner;
import com.clemble.casino.payment.money.Currency;
import com.clemble.casino.po.spring.integration.PoTestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { PoTestConfiguration.class })
@TestExecutionListeners(listeners = { RedisCleaner.class, DependencyInjectionTestExecutionListener.class })
public class SimpleTicTacToeGameITest {

    @Autowired
    public SyncGameScenarios gameScenarios;

    @Test
    public void testSimpleScenario() {
        List<RoundGamePlayer<PoState>> players = gameScenarios.match(Game.pic);
        PoRoundPlayer A = (PoRoundPlayer) players.get(0);
        PoRoundPlayer B = (PoRoundPlayer) players.get(1);

        Currency currency = A.getConfiguration().getPrice().getCurrency();
        assertEquals(A.getConfiguration().getPrice(), B.getConfiguration().getPrice());
        long gamePrice = A.getConfiguration().getPrice().getAmount();
        long originalAmount = A.playerOperations().paymentOperations().getAccount().getMoney(currency).getAmount();

        A.syncWith(B);
        assertTrue(A.isAlive());
        assertTrue(B.isAlive());
        A.select(0, 0);

        A.bet(2);
        assertEquals(A.getMoneyLeft(), gamePrice);
        assertEquals(B.getMoneyLeft(), gamePrice);
        B.bet(1);
        B.syncWith(A);

        B.select(1, 1);

        B.bet(1);
        B.syncWith(A);
        A.bet(2);

        A.select(2, 2);

        A.bet(2);
        A.syncWith(B);
        B.bet(1);
        B.syncWith(A);

        B.waitForEnd();
        A.waitForEnd();

        assertFalse(B.isAlive());
        assertFalse(A.isAlive());

        PlayerWonOutcome wonOutcome = (PlayerWonOutcome) B.getOutcome();
        assertEquals(wonOutcome.getWinner(), A.playerOperations().getPlayer());

        assertEquals(B.playerOperations().paymentOperations().getAccount().getMoney(currency).getAmount(), originalAmount - gamePrice);
        assertEquals(A.playerOperations().paymentOperations().getAccount().getMoney(currency).getAmount(), originalAmount + gamePrice);
    }

    @Test
    public void testScenarioRow() {
        for (int row = 0; row < 3; row++) {
            List<RoundGamePlayer<PoState>> players = gameScenarios.match(Game.pic);
            PoRoundPlayer playerA = (PoRoundPlayer) players.get(0);
            PoRoundPlayer playerB = (PoRoundPlayer) players.get(1);
            playerA.select(0, row);
            playerA.bet(2);
            playerB.bet(1);

            playerB.select(1, row);
            playerB.bet(1);
            playerA.bet(2);

            playerA.select(2, row);
            playerA.bet(2);
            playerB.bet(1);

            playerA.waitForEnd();
            playerB.waitForEnd();

            assertEquals(((PlayerWonOutcome) playerB.getOutcome()).getWinner(), playerA.playerOperations().getPlayer());
        }
    }

    @Test
    public void testScenarioColumn() {
        for (int column = 0; column < 3; column++) {
            List<RoundGamePlayer<PoState>> players = gameScenarios.match(Game.pic);
            PoRoundPlayer playerA = (PoRoundPlayer) players.get(0);
            PoRoundPlayer playerB = (PoRoundPlayer) players.get(1);
            playerA.select(column, 0);
            playerA.bet(2);
            playerB.bet(1);

            playerB.select(column, 1);
            playerB.bet(1);
            playerA.bet(2);

            playerA.select(column, 2);
            playerA.bet(2);
            playerB.bet(1);

            playerA.syncWith(playerB);

            playerA.waitForEnd();
            playerB.waitForEnd();

            assertEquals(((PlayerWonOutcome) playerB.getOutcome()).getWinner(), playerA.playerOperations().getPlayer());
        }
    }

}
