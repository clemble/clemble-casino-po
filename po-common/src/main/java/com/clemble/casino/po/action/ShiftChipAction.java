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
@JsonTypeName("shift")
public class ShiftChipAction implements PoCardAction {

    final private String player;
    final private ImmutablePair<Integer, Integer> from;

    final private ImmutablePair<Integer, Integer> to;

    @JsonCreator
    public ShiftChipAction(
            @JsonProperty("player") String player,
            @JsonProperty("from") ImmutablePair<Integer, Integer> from,
            @JsonProperty("to") ImmutablePair<Integer, Integer> to) {
        this.player = player;
        this.from = from;
        this.to = to;
    }

    public ImmutablePair<Integer, Integer> getTo() {
        return to;
    }

    public ImmutablePair<Integer, Integer> getFrom() {
        return from;
    }

    @Override
    public GameUnit getUnit() {
        return PoCard.move;
    }

    @Override
    public String getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShiftChipAction)) return false;

        ShiftChipAction that = (ShiftChipAction) o;

        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (player != null ? !player.equals(that.player) : that.player != null) return false;
        if (to != null ? !to.equals(that.to) : that.to != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = player != null ? player.hashCode() : 0;
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }
}
