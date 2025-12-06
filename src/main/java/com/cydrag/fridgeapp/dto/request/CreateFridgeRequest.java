package com.cydrag.fridgeapp.dto.request;

import com.cydrag.fridgeapp.model.FridgeOwnershipType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CreateFridgeRequest {

    @NotBlank
    private String name;
    private FridgeOwnershipType type;

}
