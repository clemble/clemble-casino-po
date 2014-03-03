package com.clemble.casino.po.integration.game;

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
import com.clemble.casino.error.ClembleCasinoException;
import com.clemble.casino.game.Game;
import com.clemble.casino.integration.game.construction.GameScenarios;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.util.RedisCleaner;
import com.clemble.casino.po.spring.integration.PoTestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { PoTestConfiguration.class })
@TestExecutionListeners(listeners = { RedisCleaner.class, DependencyInjectionTestExecutionListener.class })
public class WalletOperationsITest {

    @Autowired
    public PlayerScenarios playerOperations;

    @Autowired
    public GameScenarios gameOperations;

    @Test(expected = ClembleCasinoException.class)
    public void runingOutOfMoney() {
        ClembleCasinoOperations playerA = playerOperations.createPlayer();
        ClembleCasinoOperations playerB = playerOperations.createPlayer();

        do {
            PoRoundPlayer sessionAPlayer = gameOperations.<PoRoundPlayer> match(Game.pic, playerA, playerB.getPlayer());
            PoRoundPlayer sessionBPlayer = gameOperations.<PoRoundPlayer> accept(sessionAPlayer.getSession(), playerB);

            sessionAPlayer.waitForStart();
            sessionBPlayer.waitForStart();

            if (sessionAPlayer.isToMove()) {
                sessionAPlayer.select(0, 0);
                sessionAPlayer.bet(1);
                sessionBPlayer.bet(1);
            } else {
                sessionBPlayer.select(0, 0);
                sessionBPlayer.bet(1);
                sessionAPlayer.bet(1);
            }

            sessionAPlayer.giveUp();
        } while (true);
    }

}
