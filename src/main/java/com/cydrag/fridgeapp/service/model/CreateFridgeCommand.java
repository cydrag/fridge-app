package com.cydrag.fridgeapp.service.model;

import com.cydrag.fridgeapp.model.FridgeOwnershipType;
import jakarta.annotation.Nullable;
import lombok.Getter;

import java.util.Optional;
import java.util.UUID;

public class CreateFridgeCommand {

    @Getter
    private final String name;
    @Getter
    private final UUID ownerId;
    private final FridgeOwnershipType type;

    public CreateFridgeCommand(String name, UUID ownerId, @Nullable FridgeOwnershipType type) {
        this.name = name;
        this.ownerId = ownerId;
        this.type = type;
    }

    public Optional<FridgeOwnershipType> getType() {
        return Optional.ofNullable(type);
    }
}
