package com.clemble.casino.json;

import com.clemble.casino.po.action.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.clemble.casino.po.action.ShiftChipAction;
import com.clemble.casino.po.PoCard;
import com.clemble.casino.po.PoState;

public class ExtenstionJsonModule implements ClembleJsonModule {

    @Override
    public Module construct() {
        SimpleModule module = new SimpleModule("Po");
        module.registerSubtypes(new NamedType(CombineChipAction.class, CombineChipAction.class.getAnnotation(JsonTypeName.class).value()));
        module.registerSubtypes(new NamedType(DecreaseChipAction.class, DecreaseChipAction.class.getAnnotation(JsonTypeName.class).value()));
        module.registerSubtypes(new NamedType(IncreaseChipAction.class, IncreaseChipAction.class.getAnnotation(JsonTypeName.class).value()));
        module.registerSubtypes(new NamedType(ShiftChipAction.class, ShiftChipAction.class.getAnnotation(JsonTypeName.class).value()));
        module.registerSubtypes(new NamedType(PlaceChipAction.class, PlaceChipAction.class.getAnnotation(JsonTypeName.class).value()));
        module.registerSubtypes(new NamedType(PoCard.class, PoCard.class.getAnnotation(JsonTypeName.class).value()));
        module.registerSubtypes(new NamedType(PoState.class, PoState.class.getAnnotation(JsonTypeName.class).value()));
        return module;
    }

}
