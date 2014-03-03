package com.clemble.casino.po.spring.web.game;

import com.clemble.casino.po.PoState;
import com.clemble.casino.po.PoStateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.clemble.casino.server.game.action.GameStateFactory;
import com.clemble.casino.server.spring.game.GameManagementSpringConfiguration;
import com.clemble.casino.server.spring.web.WebCommonSpringConfiguration;
import com.clemble.casino.server.spring.web.game.AbstractGameSpringConfiguration;

@Configuration
@Import({ GameManagementSpringConfiguration.class, WebCommonSpringConfiguration.class })
public class PoWebSpringConfiguration extends AbstractGameSpringConfiguration<PoState> {

    @Bean
    public GameStateFactory<PoState> ticTacToeStateFactory() {
        return new PoStateFactory();
    }

}
