package com.projectn.projectn.model;

import com.projectn.projectn.common.enums.ItemStatus;
import com.projectn.projectn.common.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;


    private String imageUrl;

    private String description;

    private int price;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @PrePersist
    public void prePersist() {
        this.status = ItemStatus.ACTIVE;
    }
}
