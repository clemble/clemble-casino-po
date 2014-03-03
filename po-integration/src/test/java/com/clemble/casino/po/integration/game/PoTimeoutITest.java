package com.clemble.casino.po.integration.game;

import java.util.List;

import com.clemble.casino.integration.game.RoundGamePlayer;
import com.clemble.casino.po.PoState;
import com.clemble.casino.po.integration.emulation.PoRoundPlayer;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.clemble.casino.error.ClembleCasinoError;
import com.clemble.casino.error.ClembleCasinoException;
import com.clemble.casino.game.Game;
import com.clemble.casino.game.specification.GameConfiguration;
import com.clemble.casino.integration.game.construction.GameScenarios;
import com.clemble.casino.integration.util.RedisCleaner;
import com.clemble.casino.po.spring.integration.PoTestConfiguration;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { PoTestConfiguration.class })
@TestExecutionListeners(listeners = { RedisCleaner.class, DependencyInjectionTestExecutionListener.class })
public class PoTimeoutITest {

    @Autowired
    public GameScenarios gameOperations;

    @Test
    public void testMoveTimeout() {
        List<RoundGamePlayer<PoState>> players = gameOperations.match(Game.pic);
        PoRoundPlayer playerA = (PoRoundPlayer) players.get(0);
        PoRoundPlayer playerB = (PoRoundPlayer) players.get(1);
        ClembleCasinoException gogomayaException = null;
        try {

            Assert.assertNotNull(players);
            Assert.assertEquals(playerA.getSession(), playerB.getSession());
            Assert.assertEquals(players.size(), 2);

            playerA.select(0, 0);

            sleep(3000);

            playerA.bet(1);
        } catch (ClembleCasinoException exception) {
            gogomayaException = exception;
        } finally {
            playerA.close();
            playerB.close();
        }

        assertGogomayaFailure(gogomayaException, ClembleCasinoError.GamePlayGameEnded);
    }

    @Test
    public void testTotalTimeout() {
        List<RoundGamePlayer<PoState>> players = gameOperations.match(Game.pic);
        PoRoundPlayer playerA = (PoRoundPlayer) players.get(0);
        PoRoundPlayer playerB = (PoRoundPlayer) players.get(1);
        ClembleCasinoException gogomayaException = null;

        GameConfiguration configuration =  playerA.playerOperations().gameConstructionOperations().getConfigurations().getConfiguration(playerA.getConfigurationKey());
        
        Assert.assertTrue(configuration.getTotalTimeRule().getLimit() > 0);
        Assert.assertTrue(configuration.getMoveTimeRule().getLimit() > 0);

        long stepWaitTimeout = 300 + configuration.getTotalTimeRule().getLimit() / 4;

        try {

            Assert.assertNotNull(players);
            Assert.assertEquals(playerA.getSession(), playerB.getSession());
            Assert.assertEquals(players.size(), 2);

            playerA.select(0, 0);

            sleep(stepWaitTimeout);

            playerA.bet(1);
            playerB.bet(1);

            sleep(stepWaitTimeout);

            playerB.select(1, 0);

            sleep(stepWaitTimeout);

            playerA.bet(1);
            playerB.bet(1);

            playerA.select(0, 1);

            sleep(stepWaitTimeout);

            playerA.bet(1);
            playerB.bet(1);

        } catch (ClembleCasinoException exception) {
            gogomayaException = exception;
        } finally {
            playerA.close();
            playerB.close();
        }

        Assert.assertNotNull(gogomayaException);
        assertGogomayaFailure(gogomayaException, ClembleCasinoError.GamePlayGameEnded);
    }

    public void assertGogomayaFailure(ClembleCasinoException gogomayaException, ClembleCasinoError error) {
        Assert.assertNotNull(gogomayaException);
        Assert.assertNotNull(gogomayaException.getFailureDescription());
        Assert.assertNotNull(gogomayaException.getFailureDescription().getProblems());
        Assert.assertEquals(gogomayaException.getFailureDescription().getProblems().size(), 1);
        Assert.assertEquals(gogomayaException.getFailureDescription().getProblems().iterator().next().getError(), error);

    }

    private void sleep(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
