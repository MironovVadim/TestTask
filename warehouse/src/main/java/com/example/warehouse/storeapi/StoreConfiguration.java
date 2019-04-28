package com.example.warehouse.storeapi;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("store")
@Data
public class StoreConfiguration {
    private String url;
}
