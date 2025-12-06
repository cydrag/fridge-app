package com.cydrag.fridgeapp.controller;

import com.cydrag.fridgeapp.dto.request.CreateFridgeRequest;
import com.cydrag.fridgeapp.dto.response.FridgeDetailsResponse;
import com.cydrag.fridgeapp.dto.response.FridgeResponse;
import com.cydrag.fridgeapp.model.Fridge;
import com.cydrag.fridgeapp.security.CurrentUser;
import com.cydrag.fridgeapp.security.FridgeUserDetails;
import com.cydrag.fridgeapp.service.FridgeService;
import com.cydrag.fridgeapp.service.model.CreateFridgeCommand;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/fridges")
@AllArgsConstructor
public class FridgeController {

    private final FridgeService fridgeService;

    @PostMapping
    public ResponseEntity<FridgeResponse> createFridge(@RequestBody @Valid CreateFridgeRequest request,
                                                       @CurrentUser FridgeUserDetails userDetails) {
        CreateFridgeCommand createFridgeCommand = new CreateFridgeCommand(request.getName(), userDetails.getId(), request.getType());

        Fridge fridge = fridgeService.createFridge(createFridgeCommand);

        FridgeResponse response = new FridgeResponse(fridge);

        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(response);
    }

    @GetMapping("/{fridgeId}")
    public ResponseEntity<FridgeDetailsResponse> getFridgeById(@PathVariable UUID fridgeId,
                                                               @CurrentUser FridgeUserDetails userDetails) {
        Fridge fridge = fridgeService.getFridgeById(fridgeId, userDetails.getId());

        FridgeDetailsResponse response = new FridgeDetailsResponse(fridge);

        return ResponseEntity.ok(response);
    }
}
