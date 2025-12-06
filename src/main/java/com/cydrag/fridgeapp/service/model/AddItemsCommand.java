package com.cydrag.fridgeapp.service.model;

import java.util.List;
import java.util.UUID;

public record AddItemsCommand(
        UUID userId,
        UUID fridgeId,
        List<ItemCommand> items
) {}
