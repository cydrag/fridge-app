package com.cydrag.fridgeapp.dto.response;

import com.cydrag.fridgeapp.model.FridgeItem;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record FridgeItemResponse(
        UUID id,
        String name,
        LocalDate expiryDate,
        Instant storedAt
) {
    public FridgeItemResponse(FridgeItem item) {
        this(item.getId(), item.getProductName(), item.getBestBefore(), item.getStoredAt());
    }
}
