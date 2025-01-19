package com.projectn.projectn.repository;

import com.projectn.projectn.model.DiceRoll;
import com.projectn.projectn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface DiceRollRepository extends JpaRepository<DiceRoll, UUID> {
    Optional<DiceRoll> findByDailyQuestId(UUID dailyQuestId);

    @Query("SELECT d FROM DiceRoll d WHERE d.dailyQuest.user = :user AND DATE(d.rollAt) = DATE(:rollAt)")
    List<DiceRoll> findByUserAndRollAt(User user, Date rollAt);

}
