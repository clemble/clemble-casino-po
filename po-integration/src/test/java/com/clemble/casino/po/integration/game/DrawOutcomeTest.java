package com.clemble.casino.po.integration.game;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.clemble.casino.po.PoState;
import com.clemble.casino.po.integration.emulation.PoRoundPlayer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.clemble.casino.game.Game;
import com.clemble.casino.integration.game.RoundGamePlayer;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.game.construction.SyncGameScenarios;
import com.clemble.casino.payment.PaymentOperation;
import com.clemble.casino.payment.PaymentTransaction;
import com.clemble.casino.payment.PaymentTransactionKey;
import com.clemble.casino.payment.money.Operation;
import com.clemble.casino.po.spring.integration.PoTestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { PoTestConfiguration.class })
public class DrawOutcomeTest {

    @Autowired
    public PlayerScenarios playerScenarios;

    @Autowired
    public SyncGameScenarios gameScenarios;

    @Test
    public void testDraw() {
        // Step 1. Generating a game
        List<RoundGamePlayer<PoState>> players = gameScenarios.match(Game.pic);
        PoRoundPlayer A = (PoRoundPlayer) players.get(0);
        PoRoundPlayer B = (PoRoundPlayer) players.get(1);
        // Step 2. Generating a draw
        // O X O
        A.select(0, 0);
        A.bet(1);
        B.bet(2);
        B.select(0, 1);
        B.bet(2);
        A.bet(3);
        A.select(0, 2);
        A.bet(4);
        B.bet(5);
        // A owns 2, spent 8, left 28
        // B owns 5, spent 9, left 27
        // X 0 X
        B.select(1, 0);
        B.bet(1);
        A.bet(2);
        A.select(1, 1);
        A.bet(2);
        B.bet(3);
        B.select(1, 2);
        B.bet(1);
        A.bet(2);
        // A owns 2, spent 6, left 22
        // B owns 2, spent 5, left 22
        // X O X
        A.select(2, 0);
        A.bet(2);
        B.bet(1);
        B.select(2, 2);
        B.bet(3);
        A.bet(4);
        A.select(2, 1);
        A.bet(2);
        B.bet(3);
        // A owns 4, spent 8, left 14
        // B owns 2, spent 7, left 15
        PaymentTransactionKey transactionKey = A.getSession().toPaymentTransactionKey();
        PaymentTransaction transaction = A.playerOperations().paymentOperations().getPaymentTransaction(transactionKey);

        PaymentOperation opA = transaction.getPaymentOperation(A.getPlayer());
        PaymentOperation opB = transaction.getPaymentOperation(B.getPlayer());

        assertEquals(opA.getOperation(), Operation.Credit);
        assertEquals(opB.getOperation(), Operation.Debit);
    }
}
