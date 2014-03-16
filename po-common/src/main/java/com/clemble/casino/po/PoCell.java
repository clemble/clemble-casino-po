package com.clemble.casino.po;

import com.clemble.casino.game.unit.Chip;
import com.clemble.casino.player.PlayerAware;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by mavarazy on 08/03/14.
 */
public class PoCell implements PlayerAware {

    final public static PoCell DEFAULT = new PoCell(PlayerAware.DEFAULT_PLAYER, null);

    final private String player;
    final private Chip chip;

    @JsonCreator
    public PoCell(@JsonProperty("player") String player, @JsonProperty("chips") Chip chip) {
        this.player = player;
        this.chip = chip;
    }

    @Override
    public String getPlayer() {
        return player;
    }

    public Chip getChip() {
        return chip;
    }

    public boolean owned() {
        return player != PlayerAware.DEFAULT_PLAYER;
    }
}
