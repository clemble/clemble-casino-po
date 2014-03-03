package com.clemble.casino.po;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.clemble.casino.error.ClembleCasinoError;
import com.clemble.casino.error.ClembleCasinoException;
import com.clemble.casino.event.Event;
import com.clemble.casino.game.*;
import com.clemble.casino.game.action.BetAction;
import com.clemble.casino.game.action.DefaultGameAction;
import com.clemble.casino.game.action.GameAction;
import com.clemble.casino.game.action.SelectAction;
import com.clemble.casino.game.action.surrender.SurrenderAction;
import com.clemble.casino.game.cell.Cell;
import com.clemble.casino.game.cell.CellState;
import com.clemble.casino.game.event.server.*;
import com.clemble.casino.game.outcome.DrawOutcome;
import com.clemble.casino.game.outcome.GameOutcome;
import com.clemble.casino.game.outcome.NoOutcome;
import com.clemble.casino.game.outcome.PlayerWonOutcome;
import com.clemble.casino.player.PlayerAware;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("po")
public class PoState implements GameState {

    /**
     * Generated 28/11/13
     */
    private static final long serialVersionUID = -755717572685487667L;

    private RoundGameContext context;
    private int version;

    private PoBoard board;

    public PoState(RoundGameContext context) {
        this(context, new PoBoard(), 0);
        context.getActionLatch().expectNext(context.getPlayerIterator().next(), "select", SelectAction.class);
    }

    @JsonCreator
    public PoState(
            @JsonProperty("context") RoundGameContext context,
            @JsonProperty("root") PoBoard picPacPoeState,
            @JsonProperty("version") int version) {
        this.context = context;
        this.board = picPacPoeState;
        this.version = version;
    }

    @Override
    public PoBoard getRoot() {
        return board;
    }

    @Override
    public GameManagementEvent  process(RoundGameRecord session, Event event) {
        GameAction clientEvent = (GameAction) event;
        if (clientEvent instanceof DefaultGameAction) {
            String player = clientEvent.getPlayer();
            if (context.getActionLatch().acted(player))
                return null;
            Class<?> expectedClass = context.getActionLatch().expectedClass();
            if (SelectAction.class.isAssignableFrom(expectedClass)) {
                clientEvent = new SelectAction<Cell>(player, board.firstFree());
            } else {
                clientEvent = new BetAction(player, 1);
            }
        }

        GameManagementEvent resEvent = null;
        // Step 1. Processing Select cell move
        if (clientEvent instanceof SelectAction) {
            context.getActionLatch().put(clientEvent);
            Cell cellToSelect = ((SelectAction<Cell>) clientEvent).getSelect();
            // Step 1. Sanity check
            if (board.getBoard()[cellToSelect.getRow()][cellToSelect.getColumn()].owned()) {
                throw ClembleCasinoException.fromError(ClembleCasinoError.CellOwned);
            }
            // Step 2. Generating next moves
            board.setSelected(cellToSelect);
            context.getActionLatch().expectNext(context.getPlayerIterator().getPlayers(), "bet", BetAction.class);
            // Step 3. Returning result
            resEvent = new RoundStateChangedEvent(session, Collections.singletonList(clientEvent));
        } else if (clientEvent instanceof BetAction) {
            // Step 1. Populating made moves
            context.getActionLatch().put((BetAction) clientEvent);
            // Step 2. Checking if everybody already made their bets
            if (context.getActionLatch().complete()) {
                // Step 1. Reducing account amounts
                Collection<BetAction> bets = this.context.getActionLatch().getActions();
                CellState cellState = new CellState(bets);
                Collection<String> emptyAccounts = new ArrayList<String>(2);
                for (BetAction bet : bets) {
                    GamePlayerAccount playerAccount = this.context.getPlayerContext(bet.getPlayer()).getAccount();
                    playerAccount.subLeft(bet.getBet());
                    if (cellState.getOwner() == PlayerAware.DEFAULT_PLAYER) {
                        playerAccount.addOwned(bet.getBet());
                    } else {
                        context.getPlayerContext(cellState.getOwner()).getAccount().addOwned(bet.getBet());
                    }
                    if (playerAccount.getLeft() == 0)
                        emptyAccounts.add(bet.getPlayer());
                }
                // Step 2. Setting exposed cell state
                board.setSelectedState(cellState);
                // Step 3. Checking if PicPacPoe is over
                GameOutcome outcome = PoBoard.fetchOutcome(board);
                if (outcome == null && emptyAccounts.size() != 0) {
                    if (emptyAccounts.size() == 2) {
                        resEvent = new RoundEndedEvent(session, new DrawOutcome());
                    } else {
                        String owner = context.getPlayerIterator().whoIsOpponents(emptyAccounts.iterator().next()).iterator().next();
                        board.markEmpty(owner);
                        outcome = PoBoard.fetchOutcome(board);
                        if (outcome == null)
                            outcome = new DrawOutcome();
                    }
                }
                if (outcome != null)
                    resEvent = new RoundEndedEvent(session, outcome);
                if (resEvent == null) {
                    context.getActionLatch().expectNext(context.getPlayerIterator().next(), "select", SelectAction.class);
                    resEvent = new RoundStateChangedEvent(session, bets);
                }
            } else {
                resEvent = new PlayerMovedEvent(session.getSession(), clientEvent.getPlayer());
            }
        } else if (clientEvent instanceof SurrenderAction) {
            // Step 1. Fetching player identifier
            String looser = ((SurrenderAction) clientEvent).getPlayer();
            Collection<String> opponents = context.getPlayerIterator().whoIsOpponents(looser);
            if (opponents.size() == 0 || version == 1) {
                // Step 2. No game started just live the table
                resEvent = new RoundEndedEvent(session, new NoOutcome());
            } else {
                String winner = opponents.iterator().next();
                // Step 2. Player gave up, consists of 2 parts - Gave up, and Ended since there is no players involved
                resEvent = new RoundEndedEvent(session, new PlayerWonOutcome(winner));
            }
        } else {
            throw ClembleCasinoException.fromError(ClembleCasinoError.GamePlayMoveNotSupported);
        }
        if (!(resEvent instanceof PlayerMovedEvent))
            version++;
        return resEvent;
    }

    @Override
    public RoundGameContext getContext() {
        return context;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PoState that = (PoState) o;

        if (version != that.version)
            return false;
        if (board != null ? !board.equals(that.board) : that.board != null)
            return false;
        if (context != null ? !context.equals(that.context) : that.context != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = context != null ? context.hashCode() : 0;
        result = 31 * result + version;
        result = 31 * result + (board != null ? board.hashCode() : 0);
        return result;
    }

}
