package com.projectn.projectn.repository;

import com.projectn.projectn.model.DailyReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RewardRepository extends JpaRepository<DailyReward, UUID> {
    @Query("SELECT dr FROM DailyReward dr WHERE dr.user.id = :userId AND dr.status = 'ACTIVE'")
    List<DailyReward> findByUserId(UUID userId);
}
