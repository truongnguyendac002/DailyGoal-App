package com.projectn.projectn.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyQuest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String target;

    private String description;

    private boolean done;

    private int points;

    private Date forDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "dailyQuest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiceRoll> diceRolls;

}
