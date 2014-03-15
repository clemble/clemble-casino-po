package com.clemble.casino.po.action;

import com.clemble.casino.ImmutablePair;
import com.clemble.casino.game.action.UseGameUnitAction;
import com.clemble.casino.game.unit.Chip;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created by mavarazy on 09/03/14.
 */
@JsonTypeName("place")
public class PlaceChipAction implements UseGameUnitAction, LocationAwareAction {

    final private Chip chip;
    final private String player;
    final private ImmutablePair<Integer, Integer> from;

    @JsonCreator
    public PlaceChipAction(
        @JsonProperty("player") String player,
        @JsonProperty("unit") Chip chip,
        @JsonProperty("from") ImmutablePair<Integer, Integer> from) {
        this.chip = chip;
        this.from = from;
        this.player = player;
    }

    public Chip getUnit() {
        return chip;
    }

    @Override
    public ImmutablePair<Integer, Integer> getFrom() {
        return from;
    }

    @Override
    public String getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlaceChipAction)) return false;

        PlaceChipAction that = (PlaceChipAction) o;

        if (chip != null ? !chip.equals(that.chip) : that.chip != null) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (player != null ? !player.equals(that.player) : that.player != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = chip != null ? chip.hashCode() : 0;
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (player != null ? player.hashCode() : 0);
        return result;
    }
}
