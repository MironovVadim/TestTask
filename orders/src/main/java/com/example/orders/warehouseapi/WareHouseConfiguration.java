package com.example.orders.warehouseapi;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * Wrapper для url к серсиву WareHouse
 */
@ConfigurationProperties("warehouse")
@Component
@Data
public class WareHouseConfiguration {

    @NotNull
    private String url;
}
