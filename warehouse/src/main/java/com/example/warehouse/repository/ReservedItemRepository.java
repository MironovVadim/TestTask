package com.example.warehouse.repository;

import com.example.warehouse.model.ReservedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservedItemRepository extends JpaRepository<ReservedItem, Integer> {
    List<ReservedItem> findByOrderId(int orderId);
}
