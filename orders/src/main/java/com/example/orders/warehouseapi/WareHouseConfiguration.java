package com.example.orders.warehouseapi;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@ConfigurationProperties("warehouse")
@Component
@Data
public class WareHouseConfiguration {

    @NotNull
    private String url;
}
