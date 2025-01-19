package com.projectn.projectn.repository;

import com.projectn.projectn.model.ItemPurchase;
import com.projectn.projectn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemPurchaseRepository extends JpaRepository<ItemPurchase, UUID> {
    @Query("SELECT ip FROM ItemPurchase ip WHERE ip.shopItem.user.id = :userId")
    List<ItemPurchase> findAllByUserId(UUID userId);
}
