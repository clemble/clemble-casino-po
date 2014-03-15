package com.clemble.casino.po;

import com.clemble.casino.game.Game;
import com.clemble.casino.game.GameStateFactory;
import com.clemble.casino.game.RoundGameContext;
import com.clemble.casino.game.construct.GameInitiation;


import java.util.Arrays;

public class PoStateFactory implements GameStateFactory<PoState> {

    public PoStateFactory() {
    }

    @Override
    public PoState constructState(GameInitiation initiation, RoundGameContext context) {
        // Step 1. Creating empty table of 6x6
        PoCell[][] table = new PoCell[6][6];
        for(PoCell[] row: table)
            Arrays.fill(row, PoCell.DEFAULT);
        // Step 2. Creating board
        PoBoard board = new PoBoard(table);
        // Step 3. Creating state
        return new PoState(context, board, 0);
    }

    @Override
    public Game getGame() {
        return Game.pic;
    }

}
