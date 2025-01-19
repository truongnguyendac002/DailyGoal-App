package com.projectn.projectn.repository;

import com.projectn.projectn.model.DailyQuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface QuestRepository extends JpaRepository<DailyQuest, UUID> {
    List<DailyQuest> findByUserIdAndForDate(UUID userId, Date forDate);
}
