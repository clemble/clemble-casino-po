package com.clemble.casino.json;

import com.clemble.casino.po.PoState;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ExtenstionJsonModule implements ClembleJsonModule {

    @Override
    public Module construct() {
        SimpleModule module = new SimpleModule("Booma");
        module.registerSubtypes(new NamedType(PoState.class, "picPacPoe"));
        return module;
    }

}
