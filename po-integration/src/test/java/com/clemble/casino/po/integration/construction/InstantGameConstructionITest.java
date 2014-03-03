package com.clemble.casino.po.integration.construction;

import static org.junit.Assert.assertEquals;

import com.clemble.casino.game.specification.RoundGameConfiguration;
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
import com.clemble.casino.client.game.GameConstructionOperations;
import com.clemble.casino.game.construct.GameConstruction;
import com.clemble.casino.integration.game.construction.GameScenarios;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.util.RedisCleaner;
import com.clemble.casino.po.spring.integration.PoTestConfiguration;

@Ignore // TODO fix construction status issue
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@TestExecutionListeners(listeners = { RedisCleaner.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(classes = { PoTestConfiguration.class })
public class InstantGameConstructionITest {

    @Autowired
    public GameScenarios gameScenarios;

    @Autowired
    public PlayerScenarios playerOperations;

    @Test
    public void testTwoPlayersInstantMatch() {
        ClembleCasinoOperations A = playerOperations.createPlayer();
        ClembleCasinoOperations B = playerOperations.createPlayer();

        GameConstructionOperations constructionOperations = A.gameConstructionOperations();
        RoundGameConfiguration specification = constructionOperations.getConfigurations().matchConfigurations().get(0);
        GameConstruction constA = constructionOperations.constructAutomatch(specification);

        GameConstruction constB = B.gameConstructionOperations().constructAutomatch(specification);
        assertEquals(constA.getSession(), constB.getSession());
    }

}
