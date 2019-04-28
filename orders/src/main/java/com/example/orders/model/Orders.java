package com.example.orders.model;

import com.example.orders.model.dto.ReservedItemDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
@SQLDelete(sql = "update orders o set o.state = 'CANCELED' where o.id = ?")
@Where(clause = "state != 'CANCELED'")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private String userName;
    private double price;
    @OneToMany(mappedBy="orderId")
    private List<ReservedItem> reservedItems;
    @Enumerated(EnumType.STRING)
    private State state = State.NEW;
    @CreationTimestamp
    private LocalDate createDate;
    @UpdateTimestamp
    private LocalDate modifiedDate;

    public enum State {
        NEW, ACTIVE, CANCELED
    }

    public void extractAndSetReservedItemsIds(List<ReservedItemDto> reservedItems) {
        List<ReservedItem> reservedItemIds = reservedItems
                .stream()
                .map(ReservedItemDto::convertToReservedItem)
                .collect(Collectors.toList());
        this.setReservedItems(reservedItemIds);
    }
}