package com.cydrag.fridgeapp.dto.response;

import com.cydrag.fridgeapp.model.Fridge;
import com.cydrag.fridgeapp.model.FridgeOwnershipType;

import java.util.List;
import java.util.UUID;

public record FridgeDetailsResponse(
        UUID id,
        String name,
        UUID ownerId,
        FridgeOwnershipType type,
        List<FridgeItemResponse> items
) {
    public FridgeDetailsResponse(Fridge fridge) {
        this(
                fridge.getId(),
                fridge.getName(),
                fridge.getOwner().getId(),
                fridge.getType(),
                fridge.getItems().stream()
                        .map(FridgeItemResponse::new)
                        .toList()
        );
    }
}
