package com.cydrag.fridgeapp.service;

import com.cydrag.fridgeapp.model.Fridge;
import com.cydrag.fridgeapp.model.FridgeItem;
import com.cydrag.fridgeapp.repository.FridgeItemRepository;
import com.cydrag.fridgeapp.repository.FridgeMembershipRepository;
import com.cydrag.fridgeapp.repository.FridgeRepository;
import com.cydrag.fridgeapp.service.model.AddItemsCommand;
import com.cydrag.fridgeapp.service.model.ItemCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FridgeItemServiceTest {

    @Mock
    private FridgeRepository fridgeRepository;
    @Mock
    private FridgeMembershipRepository fridgeMembershipRepository;
    @Mock
    private FridgeItemRepository fridgeItemRepository;

    @InjectMocks
    private FridgeItemService fridgeItemService;

    @Test
    void addItems_ShouldThrowException_WhenUserNotMember() {
        UUID userId = UUID.randomUUID();
        UUID fridgeId = UUID.randomUUID();
        AddItemsCommand command = new AddItemsCommand(userId, fridgeId, List.of());

        when(fridgeMembershipRepository.existsByFridgeIdAndUserId(fridgeId, userId)).thenReturn(false);

        assertThatThrownBy(() -> fridgeItemService.addItems(command))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void addItems_ShouldMapCommandsToEntities_AndSaveAll() {
        UUID userId = UUID.randomUUID();
        UUID fridgeId = UUID.randomUUID();
        Fridge fridgeProxy = new Fridge();

        ItemCommand item1 = new ItemCommand("Milk", LocalDate.now().plusDays(5));
        ItemCommand item2 = new ItemCommand("Eggs", LocalDate.now().plusDays(10));

        AddItemsCommand command = new AddItemsCommand(userId, fridgeId, List.of(item1, item2));

        when(fridgeMembershipRepository.existsByFridgeIdAndUserId(fridgeId, userId)).thenReturn(true);
        when(fridgeRepository.getReferenceById(fridgeId)).thenReturn(fridgeProxy);

        fridgeItemService.addItems(command);

        ArgumentCaptor<List<FridgeItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(fridgeItemRepository).saveAll(captor.capture());

        List<FridgeItem> savedItems = captor.getValue();
        assertThat(savedItems).hasSize(2);
        assertThat(savedItems.get(0).getProductName()).isEqualTo("Milk");
        assertThat(savedItems.get(1).getProductName()).isEqualTo("Eggs");
        assertThat(savedItems.get(0).getFridge()).isEqualTo(fridgeProxy);
    }
}