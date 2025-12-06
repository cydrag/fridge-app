package com.cydrag.fridgeapp.dto.response;

import com.cydrag.fridgeapp.model.FridgeItem;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public record CreatedItemResponse(
        UUID id,
        String name,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate bestBefore
) {
    public CreatedItemResponse(FridgeItem item) {
        this(item.getId(), item.getProductName(), item.getBestBefore());
    }
}
