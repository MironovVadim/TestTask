package com.example.warehouse.repository;

import com.example.warehouse.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    int countByIdIn(List<Integer> itemIds);
}
