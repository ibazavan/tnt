package com.tnt.aggregator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(IntegrationProperties.class)
public class AggregatorConfig {

    @Bean()
    public WebClient webClient(@Value("${integration.host}") String url) {
        {
            return WebClient.create(url);
        }

    }
}
