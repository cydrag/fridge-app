package com.cydrag.fridgeapp.repository;

import com.cydrag.fridgeapp.model.FridgeItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FridgeItemRepository extends JpaRepository<FridgeItem, UUID> {
}
