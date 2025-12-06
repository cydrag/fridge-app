package com.cydrag.fridgeapp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class AddItemsRequest {

    @NotEmpty(message = "Item list cannot be empty")
    @Valid
    private List<ItemRequest> items;


}
