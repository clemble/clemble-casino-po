package com.clemble.casino.po.action;

import com.clemble.casino.ImmutablePair;
import com.clemble.casino.game.action.GameAction;

/**
 * Created by mavarazy on 15/03/14.
 */
public interface LocationAwareAction extends GameAction {

    public ImmutablePair<Integer, Integer> getFrom();

}
