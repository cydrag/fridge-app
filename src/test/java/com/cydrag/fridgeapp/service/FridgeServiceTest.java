package com.cydrag.fridgeapp.service;

import com.cydrag.fridgeapp.model.Fridge;
import com.cydrag.fridgeapp.model.FridgeOwnershipType;
import com.cydrag.fridgeapp.model.User;
import com.cydrag.fridgeapp.repository.FridgeMembershipRepository;
import com.cydrag.fridgeapp.repository.FridgeRepository;
import com.cydrag.fridgeapp.repository.UserRepository;
import com.cydrag.fridgeapp.service.model.CreateFridgeCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FridgeServiceTest {

    @Mock
    private FridgeRepository fridgeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FridgeMembershipRepository fridgeMembershipRepository;

    @InjectMocks
    private FridgeService fridgeService;

    @Test
    void createFridge_ShouldSaveAndReturnFridge() {
        UUID ownerId = UUID.randomUUID();
        User owner = new User();
        Field idField = ReflectionUtils.findField(User.class, "id");

        assertThat(idField).isNotNull();

        ReflectionUtils.makeAccessible(idField);
        ReflectionUtils.setField(idField, owner, ownerId);

        CreateFridgeCommand command = new CreateFridgeCommand("My Fridge", ownerId, FridgeOwnershipType.PRIVATE);

        when(userRepository.getReferenceById(ownerId)).thenReturn(owner);
        when(fridgeRepository.save(any(Fridge.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Fridge result = fridgeService.createFridge(command);

        assertThat(result.getName()).isEqualTo("My Fridge");
        assertThat(result.getType()).isEqualTo(FridgeOwnershipType.PRIVATE);
        assertThat(result.getMembers()).hasSize(1);
        verify(fridgeRepository).save(any(Fridge.class));
    }

    @Test
    void getFridgeById_ShouldThrowException_WhenUserNotMemberOfPrivateFridge() {
        UUID fridgeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Fridge privateFridge = new Fridge();

        when(fridgeRepository.findByIdWithItems(fridgeId)).thenReturn(Optional.of(privateFridge));
        when(fridgeMembershipRepository.existsByFridgeIdAndUserId(fridgeId, userId)).thenReturn(false);

        assertThatThrownBy(() -> fridgeService.getFridgeById(fridgeId, userId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You are not a member");
    }

    @Test
    void getFridgeById_ShouldReturnFridge_WhenUserIsMember() {
        UUID fridgeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Fridge privateFridge = new Fridge();

        when(fridgeRepository.findByIdWithItems(fridgeId)).thenReturn(Optional.of(privateFridge));
        when(fridgeMembershipRepository.existsByFridgeIdAndUserId(fridgeId, userId)).thenReturn(true);

        Fridge result = fridgeService.getFridgeById(fridgeId, userId);

        assertThat(result).isNotNull();
    }

    @Test
    void getFridgeById_ShouldReturnPublicFridge_NoMemberCheckNeeded() {
        UUID fridgeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Fridge publicFridge = new Fridge();
        publicFridge.setType(FridgeOwnershipType.PUBLIC);

        when(fridgeRepository.findByIdWithItems(fridgeId)).thenReturn(Optional.of(publicFridge));

        Fridge result = fridgeService.getFridgeById(fridgeId, userId);

        assertThat(result).isNotNull();
        verify(fridgeMembershipRepository, never()).existsByFridgeIdAndUserId(any(UUID.class), any(UUID.class));
    }
}
