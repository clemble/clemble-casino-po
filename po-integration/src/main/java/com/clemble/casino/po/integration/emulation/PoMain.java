package com.clemble.casino.po.integration.emulation;

import javax.inject.Singleton;

import com.clemble.casino.integration.emulator.GamePlayerEmulator;
import com.clemble.casino.integration.game.GamePlayerFactory;
import com.clemble.casino.integration.game.construction.GameScenarios;
import com.clemble.casino.po.PoState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.server.spring.common.SpringConfiguration;
import com.clemble.casino.po.spring.integration.PoTestConfiguration;

public class PoMain {

    @SuppressWarnings({ "resource", "unchecked" })
    public static void main(String[] arguments) {
        // Step 1. Reading application context configuration
        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.getEnvironment().setActiveProfiles(SpringConfiguration.INTEGRATION_TEST);
        applicationContext.register(PoTestConfiguration.class);
        applicationContext.register(MainConfiguration.class);
        applicationContext.refresh();
        applicationContext.start();

        // Step 2. Starting game emulator
        GamePlayerEmulator<PoState> emulator = applicationContext.getBean(GamePlayerEmulator.class);
        emulator.emulate();

        // Step 3. To guarantee proper resource release, registering shutdown hook
        applicationContext.registerShutdownHook();
    }

    @Configuration
    public static class MainConfiguration {

        @Autowired
        public GamePlayerFactory gamePlayerFactory;

        @Autowired
        public GameScenarios scenarios;

        @Autowired
        public PlayerScenarios playerOperations;

        @Bean
        @Singleton
        public PoActor actor() {
            return new PoActor();
        }

        @Bean
        @Singleton
        public GamePlayerEmulator<PoState> emulator() {
            new GamePlayerEmulator<>(playerOperations, scenarios, actor(), gamePlayerFactory);
            return new GamePlayerEmulator<>(playerOperations, scenarios, actor(), gamePlayerFactory);
        }

    }

}
