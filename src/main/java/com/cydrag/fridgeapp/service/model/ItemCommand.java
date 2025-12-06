package com.cydrag.fridgeapp.service.model;

import java.time.LocalDate;

public record ItemCommand(
        String name,
        LocalDate bestBefore
) {
}