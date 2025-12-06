package com.cydrag.fridgeapp.service;

import com.cydrag.fridgeapp.exception.ResourceNotFoundException;
import com.cydrag.fridgeapp.model.Fridge;
import com.cydrag.fridgeapp.model.FridgeOwnershipType;
import com.cydrag.fridgeapp.model.User;
import com.cydrag.fridgeapp.repository.FridgeMemberRepository;
import com.cydrag.fridgeapp.repository.FridgeRepository;
import com.cydrag.fridgeapp.repository.UserRepository;
import com.cydrag.fridgeapp.service.model.CreateFridgeCommand;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class FridgeService {

    private final FridgeRepository fridgeRepository;
    private final UserRepository userRepository;
    private final FridgeMemberRepository fridgeMemberRepository;

    @Transactional
    public Fridge createFridge(CreateFridgeCommand createFridgeCommand) {
        User owner = userRepository.getReferenceById(createFridgeCommand.getOwnerId());

        FridgeOwnershipType type = createFridgeCommand.getType()
                .orElse(FridgeOwnershipType.PRIVATE);

        Fridge fridge = new Fridge(createFridgeCommand.getName(), owner, type);

        return fridgeRepository.save(fridge);
    }

    @Transactional(readOnly = true)
    public Fridge getFridgeById(UUID fridgeId, UUID userId) {
        Fridge fridge = fridgeRepository.findByIdWithItems(fridgeId)
                .orElseThrow(() -> new ResourceNotFoundException("Fridge not found"));

        if (fridge.getType() == FridgeOwnershipType.PRIVATE) {

            boolean hasAccess = fridgeMemberRepository.existsByFridgeIdAndUserId(fridgeId, userId);

            if (!hasAccess) {
                throw new AccessDeniedException("You are not a member of this fridge.");
            }
        }

        return fridge;
    }
}
