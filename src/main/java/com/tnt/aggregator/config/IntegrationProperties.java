package com.tnt.aggregator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration")
@Getter
@Setter
public class IntegrationProperties {
    String endpointPricing;
    String endpointShipments;
    String endpointTracking;
    String host;
}
