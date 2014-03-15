package com.clemble.casino.po;

import com.clemble.casino.game.unit.GameUnit;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created by mavarazy on 15/03/14.
 */
@JsonTypeName("card")
public enum PoCard implements GameUnit {
    move,
    increase,
    decrease;
}
