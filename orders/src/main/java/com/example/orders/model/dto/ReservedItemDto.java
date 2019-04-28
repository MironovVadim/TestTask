package com.example.orders.model.dto;

import com.example.orders.model.ReservedItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservedItemDto {
    @JsonProperty("itemId")
    private Integer id;
    @ApiModelProperty(hidden = true)
    private Integer orderId;
    private int count;

    public ReservedItem convertToReservedItem() {
        ReservedItem reservedItem = new ReservedItem();
        reservedItem.setOrderId(orderId);
        reservedItem.setId(id);
        return reservedItem;
    }
}