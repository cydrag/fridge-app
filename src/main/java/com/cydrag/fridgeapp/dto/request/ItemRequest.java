package com.cydrag.fridgeapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class ItemRequest {

    @NotBlank
    private String productName;
    private LocalDate bestBefore;

}
