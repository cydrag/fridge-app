package com.cydrag.fridgeapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "fridge_item")
@Getter
@Setter
@NoArgsConstructor
public class FridgeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.PRIVATE)
    private UUID id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fridge_id", nullable = false)
    private Fridge fridge;

    @CreationTimestamp
    @Column(name = "stored_at", nullable = false, updatable = false)
    private Instant storedAt;

    @Column(name = "best_before", nullable = false)
    private LocalDate bestBefore;

    public FridgeItem(String productName, LocalDate bestBefore, Fridge fridge) {
        this.productName = productName;
        this.bestBefore = bestBefore;
        this.fridge = fridge;
    }
}
