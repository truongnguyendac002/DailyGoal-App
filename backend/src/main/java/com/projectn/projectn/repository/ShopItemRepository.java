package com.projectn.projectn.repository;

import com.projectn.projectn.model.ShopItem;
import com.projectn.projectn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ShopItemRepository extends JpaRepository<ShopItem, UUID> {
    @Query("SELECT s FROM ShopItem s WHERE s.user = ?1 AND s.status != 'INACTIVE'")
    List<ShopItem> findAllByUser(User user);
}
