package com.cydrag.fridgeapp.repository;

import com.cydrag.fridgeapp.model.Fridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface FridgeRepository extends JpaRepository<Fridge, UUID> {

    @Query("SELECT f FROM Fridge f LEFT JOIN FETCH f.items WHERE f.id = :id")
    Optional<Fridge> findByIdWithItems(@Param("id") UUID id);
}
