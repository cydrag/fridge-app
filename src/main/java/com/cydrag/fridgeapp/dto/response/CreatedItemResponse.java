package com.cydrag.fridgeapp.dto.response;

import com.cydrag.fridgeapp.model.FridgeItem;

import java.time.LocalDate;
import java.util.UUID;

public record CreatedItemResponse(
        UUID id,
        String name,
        LocalDate bestBefore
) {
    public CreatedItemResponse(FridgeItem item) {
        this(item.getId(), item.getProductName(), item.getBestBefore());
    }
}
