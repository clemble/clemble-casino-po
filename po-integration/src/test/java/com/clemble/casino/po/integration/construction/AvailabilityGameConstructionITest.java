package com.clemble.casino.po.integration.construction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.clemble.casino.integration.game.RoundGamePlayer;
import com.clemble.casino.po.PoState;
import com.clemble.casino.po.integration.emulation.PoRoundPlayer;
import com.clemble.casino.server.spring.common.SpringConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.clemble.casino.base.ExpectedEvent;
import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.game.Game;
import com.clemble.casino.game.GameSessionKey;
import com.clemble.casino.game.GameState;
import com.clemble.casino.game.construct.GameConstruction;
import com.clemble.casino.game.event.schedule.InvitationAcceptedEvent;
import com.clemble.casino.integration.game.construction.GameScenarios;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.player.PlayerAware;
import com.clemble.casino.po.spring.integration.PoTestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { PoTestConfiguration.class })
public class AvailabilityGameConstructionITest {

    @Autowired
    public PlayerScenarios playerOperations;

    @Autowired
    public GameScenarios gameScenarios;

    @Test
    public void testSimpleCreation() {
        ClembleCasinoOperations playerA = playerOperations.createPlayer();
        ClembleCasinoOperations playerB = playerOperations.createPlayer();

        RoundGamePlayer<PoState> player = gameScenarios.match(Game.pic, playerA, playerB.getPlayer());
        PoRoundPlayer sessionPlayer = new PoRoundPlayer(player);
        playerB.gameConstructionOperations().accept(sessionPlayer.getSession());

        sessionPlayer.waitForStart();
        Assert.assertTrue(sessionPlayer.isAlive());
    }

    @Test
    public void testScenarioCreation() {
        List<RoundGamePlayer<PoState>> sessionPlayers = gameScenarios.match(Game.pic);

        Assert.assertTrue(sessionPlayers.get(0).isToMove());
    }

    @Test
    public void testBusyGameInitiation() {
        // Step 1. Generating 3 players - A, B, C
        ClembleCasinoOperations playerA = playerOperations.createPlayer();
        ClembleCasinoOperations playerB = playerOperations.createPlayer();
        ClembleCasinoOperations playerC = playerOperations.createPlayer();
        // Step 2. Generating 2 instant game request A - B and A - C
        PoRoundPlayer sessionABPlayer = gameScenarios.<PoRoundPlayer> match(Game.pic, playerA, playerB.getPlayer());
        PoRoundPlayer sessionACPlayer = gameScenarios.<PoRoundPlayer> match(Game.pic, playerA, playerC.getPlayer());
        // Step 3. Accepting A - C game request to start A - C game
        PoRoundPlayer sessionCAPlayer = gameScenarios.<PoRoundPlayer> accept(sessionACPlayer.getSession(), playerC);
        sessionACPlayer.waitForStart();
        sessionACPlayer.syncWith(sessionCAPlayer);
        // Step 4. Accepting A - B game request to start A - B game, it should not be started until A - C game finishes
        PoRoundPlayer sessionBAPlayer = gameScenarios.<PoRoundPlayer> accept(sessionABPlayer.getSession(), playerB);
        // Step 4.1 Checking appropriate alive states for A - B game
        assertLivenes(sessionBAPlayer, false);
        assertLivenes(sessionABPlayer, false);
        // Step 4.1 Checking appropriate alive states for A - C game
        assertLivenes(sessionACPlayer, true);
        assertLivenes(sessionCAPlayer, true);
        // Step 5. Stopping A - C game
        sessionACPlayer.giveUp();
        sessionACPlayer.waitForEnd();
        assertLivenes(sessionACPlayer, false);
        // Step 6. Game A - B must start automatically
        sessionBAPlayer.waitForStart();
        sessionBAPlayer.syncWith(sessionABPlayer);
        // Step 7. Checking appropriate alive states for A - B game
        assertLivenes(sessionBAPlayer, true);
        assertLivenes(sessionABPlayer, true);
        // Step 8. Checking appropriate alive states for A - C game
        sessionCAPlayer.syncWith(sessionACPlayer);
        assertLivenes(sessionCAPlayer, false);
    }

    @Test
    public void testResponseExtraction() {
        ClembleCasinoOperations A = playerOperations.createPlayer();
        ClembleCasinoOperations B = playerOperations.createPlayer();

        RoundGamePlayer<PoState> playerA = gameScenarios.match(Game.pic, A, B.getPlayer());
        GameSessionKey sessionKey = playerA.getSession();
        PlayerAware AtoAaction = A.gameConstructionOperations().getResponce(sessionKey, A.getPlayer());
        PlayerAware AtoBaction = A.gameConstructionOperations().getResponce(sessionKey, B.getPlayer());

        PlayerAware BtoAaction = B.gameConstructionOperations().getResponce(sessionKey, A.getPlayer());
        PlayerAware BtoBaction = B.gameConstructionOperations().getResponce(sessionKey, B.getPlayer());

        assertEquals(AtoAaction, BtoAaction);
        assertEquals(AtoBaction, BtoBaction);
        assertTrue(AtoAaction instanceof InvitationAcceptedEvent);
        assertTrue(AtoBaction instanceof ExpectedEvent);
    }


    private <State extends GameState> void assertLivenes(RoundGamePlayer<State> player, boolean alive) {
        if ((player.isAlive() && !alive) || (!player.isAlive() && alive)) {
            String playerIdentifier = player.playerOperations().getPlayer();
            GameConstruction construction = player.playerOperations().gameConstructionOperations().getConstruct(player.getSession());
            List<String> opponents = new ArrayList<String>(construction.getResponses().fetchParticipants());
            opponents.remove(playerIdentifier);
            Assert.fail(player.getState().getVersion() + " " + playerIdentifier + " with opponents " + opponents + " expected "+ (alive ? "to be" : "not to be") + " alive in " + construction.getSession());
        }
    }
}
