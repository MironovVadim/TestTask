package com.example.orders.repository;

import com.example.orders.model.ReservedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservedItemRepository extends JpaRepository<ReservedItem, Integer> {
}
