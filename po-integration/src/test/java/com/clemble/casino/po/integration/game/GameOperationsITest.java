package com.clemble.casino.po.integration.game;

import com.clemble.casino.game.specification.RoundGameConfiguration;
import com.clemble.casino.integration.game.RoundGamePlayer;
import com.clemble.casino.po.PoState;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.integration.game.construction.GameScenarios;
import com.clemble.casino.integration.game.construction.GameScenariosUtils;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.util.RedisCleaner;
import com.clemble.casino.po.spring.integration.PoTestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { PoTestConfiguration.class })
@TestExecutionListeners(listeners = { RedisCleaner.class, DependencyInjectionTestExecutionListener.class })
public class GameOperationsITest {

    @Autowired
    public PlayerScenarios playerOperations;

    @Autowired
    public GameScenarios gameScenarios;

    @Test
    public void createWithMatchGameConfiguration() {
        // Step 1. Creating player
        ClembleCasinoOperations player = playerOperations.createPlayer();
        RoundGameConfiguration specification = GameScenariosUtils.random(player.gameConstructionOperations().getConfigurations().matchConfigurations());
        // Step 2. Creating game table
        RoundGamePlayer<PoState> gameTable = gameScenarios.match(specification, player);
        Assert.assertNotNull(gameTable);
    }

}
