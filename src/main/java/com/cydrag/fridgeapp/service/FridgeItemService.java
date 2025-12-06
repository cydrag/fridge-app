package com.cydrag.fridgeapp.service;

import com.cydrag.fridgeapp.model.Fridge;
import com.cydrag.fridgeapp.model.FridgeItem;
import com.cydrag.fridgeapp.repository.FridgeItemRepository;
import com.cydrag.fridgeapp.repository.FridgeMemberRepository;
import com.cydrag.fridgeapp.repository.FridgeRepository;
import com.cydrag.fridgeapp.service.model.AddItemsCommand;
import com.cydrag.fridgeapp.service.model.ItemCommand;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FridgeItemService {

    private final FridgeRepository fridgeRepository;
    private final FridgeMemberRepository fridgeMemberRepository;
    private final FridgeItemRepository fridgeItemRepository;

    @Transactional
    public List<FridgeItem> addItems(AddItemsCommand command) {
        UUID fridgeId = command.fridgeId();
        UUID userId = command.userId();

        if (!fridgeMemberRepository.existsByFridgeIdAndUserId(fridgeId, userId)) {
            throw new AccessDeniedException("You don't have permission to access this fridge.");
        }

        Fridge fridgeProxy = fridgeRepository.getReferenceById(fridgeId);

        List<FridgeItem> entities = new ArrayList<>();

        for (ItemCommand itemCmd : command.items()) {
            entities.add(new FridgeItem(
                    itemCmd.name(),
                    itemCmd.bestBefore(),
                    fridgeProxy
            ));
        }

        return fridgeItemRepository.saveAll(entities);
    }
}
