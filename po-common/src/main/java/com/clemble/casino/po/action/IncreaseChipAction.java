package com.clemble.casino.po.action;

import com.clemble.casino.ImmutablePair;
import com.clemble.casino.game.unit.GameUnit;
import com.clemble.casino.po.PoCard;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created by mavarazy on 15/03/14.
 */
@JsonTypeName("increase")
public class IncreaseChipAction implements PoCardAction, LocationAwareAction {

    final private String player;
    final private ImmutablePair<Integer, Integer> from;

    @JsonCreator
    public IncreaseChipAction(
        @JsonProperty("player") String player,
        @JsonProperty("from") ImmutablePair<Integer, Integer> from) {
        this.from = from;
        this.player = player;
    }

    @Override
    public GameUnit getUnit() {
        return PoCard.increase;
    }

    @Override
    public String getPlayer() {
        return player;
    }

    @Override
    public ImmutablePair<Integer, Integer> getFrom() {
        return from;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IncreaseChipAction)) return false;

        IncreaseChipAction that = (IncreaseChipAction) o;

        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (player != null ? !player.equals(that.player) : that.player != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = player != null ? player.hashCode() : 0;
        result = 31 * result + (from != null ? from.hashCode() : 0);
        return result;
    }
}
