package com.projectn.projectn.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
public class UserReward {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private DailyReward dailyReward;

    private Date receivedAt;

    @PrePersist
    public void prePersist() {
        receivedAt = new Date();
    }
}
