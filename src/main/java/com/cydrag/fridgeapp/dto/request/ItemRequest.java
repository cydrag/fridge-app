package com.cydrag.fridgeapp.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate bestBefore;

}
