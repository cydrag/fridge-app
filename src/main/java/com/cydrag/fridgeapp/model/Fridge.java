package com.cydrag.fridgeapp.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "fridge")
@NoArgsConstructor
@Getter
@Setter
public class Fridge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.PRIVATE)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private FridgeOwnershipType type = FridgeOwnershipType.PRIVATE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "fridge", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FridgeMember> members = new HashSet<>();

    @OneToMany(mappedBy = "fridge", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FridgeItem> items = new HashSet<>();

    public Fridge(String name, User owner, FridgeOwnershipType type) {
        this.name = name;
        this.owner = owner;
        this.type = type;
        this.addMember(owner);
    }

    public void addMember(User user) {
        FridgeMember membership = new FridgeMember(this, user);
        members.add(membership);
    }

    public void removeMember(User user) {
        members.removeIf(member -> member.getUser().equals(user));
    }

    public void addItem(FridgeItem item) {
        items.add(item);
        item.setFridge(this);
    }

    public void removeItem(FridgeItem item) {
        items.remove(item);
    }

    @Override
    public final boolean equals(Object o) {
        // 1. Optimization: Are they literally the same object in memory?
        if (this == o) return true;

        // 2. Type Check: use 'instanceof' to handle Hibernate Proxies
        if (!(o instanceof Fridge other)) return false;

        // 3. THE STRATEGY:
        // If IDs are populated, compare them.
        // If ANY ID is null, we assume they are different objects (return false).
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public final int hashCode() {
        // 4. THE CONSTANT:
        // This ensures the object never "moves buckets" in a HashSet,
        // even if the ID changes from null to UUID after saving.
        return this.getClass().hashCode();
    }
}
