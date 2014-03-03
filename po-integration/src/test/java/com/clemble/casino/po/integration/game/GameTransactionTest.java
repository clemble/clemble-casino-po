package com.clemble.casino.po.integration.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.clemble.casino.game.event.server.RoundEndedEvent;
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

import com.clemble.casino.game.Game;
import com.clemble.casino.game.GameSessionAwareEvent;
import com.clemble.casino.game.outcome.PlayerWonOutcome;
import com.clemble.casino.game.specification.GameConfiguration;
import com.clemble.casino.integration.game.construction.GameScenarios;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.util.RedisCleaner;
import com.clemble.casino.payment.money.Currency;
import com.clemble.casino.po.spring.integration.PoTestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { PoTestConfiguration.class })
@TestExecutionListeners(listeners = { RedisCleaner.class, DependencyInjectionTestExecutionListener.class })
public class GameTransactionTest {

    @Autowired
    public GameScenarios gameScenarios;

    @Autowired
    public PlayerScenarios playerScenarios;

    @Test
    public void testSimpleScenarioObservation() {
        List<RoundGamePlayer<PoState>> players = gameScenarios.match(Game.pic);
        PoRoundPlayer A = (PoRoundPlayer) players.get(0);
        PoRoundPlayer B = (PoRoundPlayer) players.get(1);

        GameConfiguration Aconfiguration = A.playerOperations().gameConstructionOperations().getConfigurations().getConfiguration(A.getConfigurationKey());
        GameConfiguration bconfiguration = B.playerOperations().gameConstructionOperations().getConfigurations().getConfiguration(B.getConfigurationKey()); 
        
        Currency currency = Aconfiguration.getPrice().getCurrency();
        assertEquals(Aconfiguration.getPrice(), bconfiguration.getPrice());
        long gamePrice = Aconfiguration.getPrice().getAmount();
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

        List<GameSessionAwareEvent> events = A.getEvents();
        GameSessionAwareEvent lastEvent = events.get(events.size() - 1);
        assertTrue(lastEvent instanceof RoundEndedEvent);
        assertNotNull(((RoundEndedEvent) lastEvent).getTransaction());
    }

}
