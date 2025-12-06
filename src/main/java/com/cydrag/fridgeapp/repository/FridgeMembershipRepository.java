package com.cydrag.fridgeapp.repository;

import com.cydrag.fridgeapp.model.FridgeMembership;
import com.cydrag.fridgeapp.model.FridgeMembershipId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FridgeMembershipRepository extends JpaRepository<FridgeMembership, FridgeMembershipId> {

    boolean existsByFridgeIdAndUserId(UUID fridgeId, UUID userId);
}
