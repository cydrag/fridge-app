package com.cydrag.fridgeapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "fridge_member")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = { "fridge", "user" })
public class FridgeMember {

    @EmbeddedId
    private FridgeMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("fridgeId")
    @JoinColumn(name = "fridge_id")
    private Fridge fridge;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    public FridgeMember(Fridge fridge, User user) {
        this.id = new FridgeMemberId(fridge.getId(), user.getId());
        this.fridge = fridge;
        this.user = user;
    }

}
