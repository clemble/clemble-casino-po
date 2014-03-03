package com.clemble.casino.po.spring.integration;

import com.clemble.casino.integration.game.RoundGamePlayerFactory;
import com.clemble.casino.po.PoState;
import com.clemble.casino.po.integration.emulation.PoRoundGamePlayerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.clemble.casino.android.AndroidCasinoRegistrationTemplate;
import com.clemble.casino.android.player.AndroidPlayerRegistrationService;
import com.clemble.casino.client.ClembleCasinoRegistrationOperations;
import com.clemble.casino.client.error.ClembleCasinoResponseErrorHandler;
import com.clemble.casino.integration.spring.BaseTestSpringConfiguration;
import com.clemble.casino.integration.spring.TestSpringConfiguration;
import com.clemble.casino.player.service.PlayerRegistrationService;
import com.clemble.casino.server.spring.common.SpringConfiguration;
import com.clemble.casino.server.spring.web.ClientRestCommonSpringConfiguration;
import com.clemble.casino.server.spring.web.payment.PaymentWebSpringConfiguration;
import com.clemble.casino.server.spring.web.player.PlayerWebSpringConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.clemble.casino.po.spring.web.game.PoWebSpringConfiguration;
import com.clemble.casino.po.spring.web.management.PoManagementWebSpringConfiguration;

@Configuration
@Import(value = { BaseTestSpringConfiguration.class, PoTestConfiguration.LocalTestConfiguration.class, PoTestConfiguration.IntegrationTestConfiguration.class })
public class PoTestConfiguration implements TestSpringConfiguration {

    @Bean
    public RoundGamePlayerFactory<PoState> picPacPoeSessionPlayerFactory(RoundGamePlayerFactory<PoState> sessionPlayerFactory) {
        return new PoRoundGamePlayerFactory(sessionPlayerFactory);
    }

    @Configuration
    @Profile(value = SpringConfiguration.DEFAULT)
    @Import(value = { PoWebSpringConfiguration.class, PoManagementWebSpringConfiguration.class, PlayerWebSpringConfiguration.class, PaymentWebSpringConfiguration.class })
    public static class LocalTestConfiguration {

    }

    @Configuration
    @Profile({ INTEGRATION_TEST, INTEGRATION_CLOUD, INTEGRATION_DEFAULT })
    @Import(ClientRestCommonSpringConfiguration.class)
    public static class IntegrationTestConfiguration {

        @Autowired
        public ObjectMapper objectMapper;

        //@Value("#{systemProperties['clemble.casino.management.url'] ?: 'http://54.194.182.252:8080/management/'}")
        //@Value("#{systemProperties['clemble.casino.management.url'] ?: 'http://54.72.2.225:8080/management/'}")
        @Value("#{systemProperties['clemble.casino.management.url'] ?: 'http://localhost:8080/management/'}")
        public String baseUrl;

        public String getBaseUrl() {
            return baseUrl.substring(0, baseUrl.substring(0, baseUrl.length() - 1).lastIndexOf("/") + 1);
        }

        @Bean
        public RestTemplate restTemplate() {
            RestTemplate restTemplate = new RestTemplate();

            for (HttpMessageConverter<?> messageConverter : restTemplate.getMessageConverters()) {
                if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                    ((MappingJackson2HttpMessageConverter) messageConverter).setObjectMapper(objectMapper);
                }
            }

            restTemplate.setErrorHandler(new ClembleCasinoResponseErrorHandler(objectMapper));
            return restTemplate;
        }

        @Bean
        public PlayerRegistrationService playerRegistrationService() {
            return new AndroidPlayerRegistrationService(baseUrl);
        }

        @Bean
        public ClembleCasinoRegistrationOperations clembleCasinoRegistrationOperations() {
            return new AndroidCasinoRegistrationTemplate(baseUrl);
        }

    }
}
