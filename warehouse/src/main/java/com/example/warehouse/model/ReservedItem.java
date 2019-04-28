package com.example.warehouse.model;

import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@SQLDelete(sql = "UPDATE reserved_item SET is_canceled = true WHERE id = ?")
@Where(clause = "is_canceled = false")
public class ReservedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private int count;
    @NotNull
    @JoinColumn(name = "id", table = "item")
    private Integer itemId;
    @NotNull
    private Integer orderId;
    @NotNull
    private boolean isCanceled = false;
}
