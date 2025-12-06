package com.cydrag.fridgeapp.repository;

import com.cydrag.fridgeapp.model.FridgeMember;
import com.cydrag.fridgeapp.model.FridgeMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FridgeMemberRepository extends JpaRepository<FridgeMember, FridgeMemberId> {

    boolean existsByFridgeIdAndUserId(UUID fridgeId, UUID userId);
}
