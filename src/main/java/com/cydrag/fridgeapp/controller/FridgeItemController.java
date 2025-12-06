package com.cydrag.fridgeapp.controller;

import com.cydrag.fridgeapp.dto.request.AddItemsRequest;
import com.cydrag.fridgeapp.dto.response.AddItemsResponse;
import com.cydrag.fridgeapp.dto.response.CreatedItemResponse;
import com.cydrag.fridgeapp.model.FridgeItem;
import com.cydrag.fridgeapp.security.CurrentUser;
import com.cydrag.fridgeapp.security.FridgeUserDetails;
import com.cydrag.fridgeapp.service.FridgeItemService;
import com.cydrag.fridgeapp.service.model.AddItemsCommand;
import com.cydrag.fridgeapp.service.model.ItemCommand;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fridges/{fridgeId}/items")
@AllArgsConstructor
public class FridgeItemController {

    private final FridgeItemService fridgeItemService;

    @PostMapping
    public ResponseEntity<AddItemsResponse> addItems(
            @PathVariable UUID fridgeId,
            @RequestBody @Valid AddItemsRequest request,
            @CurrentUser FridgeUserDetails user
    ) {
        List<ItemCommand> itemCommands = request.getItems().stream()
                .map(item -> new ItemCommand(item.getProductName(), item.getBestBefore()))
                .toList();

        AddItemsCommand command = new AddItemsCommand(
                user.getId(),
                fridgeId,
                itemCommands
        );

        List<FridgeItem> savedItems = fridgeItemService.addItems(command);

        List<CreatedItemResponse> responseItems = savedItems.stream()
                .map(CreatedItemResponse::new)
                .toList();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AddItemsResponse(responseItems));
    }
}
