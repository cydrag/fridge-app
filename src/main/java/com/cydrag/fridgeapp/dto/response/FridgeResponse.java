package com.cydrag.fridgeapp.dto.response;

import com.cydrag.fridgeapp.model.Fridge;
import com.cydrag.fridgeapp.model.FridgeOwnershipType;

import java.time.Instant;
import java.util.UUID;

public record FridgeResponse(UUID id, String name, UUID ownerId, FridgeOwnershipType type, Instant createdAt) {

    public FridgeResponse(Fridge fridge) {
        this(
                fridge.getId(),
                fridge.getName(),
                fridge.getOwner().getId(),
                fridge.getType(),
                fridge.getCreatedAt()
        );
    }
}
