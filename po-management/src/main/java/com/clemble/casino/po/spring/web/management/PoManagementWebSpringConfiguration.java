package com.clemble.casino.po.spring.web.management;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.clemble.casino.configuration.ResourceLocationService;
import com.clemble.casino.configuration.ServerRegistryConfiguration;
import com.clemble.casino.server.configuration.SimpleNotificationConfigurationService;
import com.clemble.casino.server.configuration.SimpleResourceLocationService;
import com.clemble.casino.server.spring.common.CommonSpringConfiguration;
import com.clemble.casino.server.spring.payment.PaymentCommonSpringConfiguration;
import com.clemble.casino.server.spring.player.PlayerCommonSpringConfiguration;
import com.clemble.casino.server.spring.web.WebCommonSpringConfiguration;
import com.clemble.casino.server.spring.web.management.AbstractManagementWebSpringConfiguration;

@Configuration
@Import(value = { WebCommonSpringConfiguration.class, CommonSpringConfiguration.class, PlayerCommonSpringConfiguration.class,
        PaymentCommonSpringConfiguration.class, PoManagementWebSpringConfiguration.Cloud.class, PoManagementWebSpringConfiguration.Integration.class })
public class PoManagementWebSpringConfiguration extends AbstractManagementWebSpringConfiguration {

    @Configuration
    @Profile({INTEGRATION_TEST, TEST, DEFAULT, INTEGRATION_DEFAULT })
    public static class Integration {

        @Bean
        public ServerRegistryConfiguration serverRegistryConfiguration() {
            return new ServerRegistryConfiguration("localhost", "http://localhost:8080/player/", "http://localhost:8080/payment/", "http://localhost:8080/game/");
        }

        @Bean
        public ResourceLocationService resourceLocationService() {
            ServerRegistryConfiguration serverRegistryConfiguration = serverRegistryConfiguration();
            SimpleNotificationConfigurationService configurationService = new SimpleNotificationConfigurationService("guest", "guest", serverRegistryConfiguration.getPlayerNotificationRegistry());
            return new SimpleResourceLocationService(configurationService, serverRegistryConfiguration);
        }

    }

    @Configuration
    @Profile({CLOUD, INTEGRATION_CLOUD})
    public static class Cloud {

        @Bean
        public ServerRegistryConfiguration serverRegistryConfiguration() {
            return new ServerRegistryConfiguration("54.201.45.95", "http://54.201.45.95:8080/player/", "http://54.201.45.95:8080/payment/", "http://54.201.45.95:8080/game/");
        }

        @Bean
        public ResourceLocationService resourceLocationService() {
            ServerRegistryConfiguration serverRegistryConfiguration = serverRegistryConfiguration();
            SimpleNotificationConfigurationService configurationService = new SimpleNotificationConfigurationService("guest", "guest", serverRegistryConfiguration.getPlayerNotificationRegistry());
            return new SimpleResourceLocationService(configurationService, serverRegistryConfiguration);
        }

    }

}
