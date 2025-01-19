package com.projectn.projectn.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;
@Entity
@Data
public class DiceRoll {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "daily_quest_id")
    private DailyQuest dailyQuest;

    private int points;

    private Date rollAt;

    @PrePersist
    public void prePersist() {
        this.rollAt = new Date();
    }
}
