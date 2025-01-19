package com.projectn.projectn.service;

import com.projectn.projectn.common.Constant;
import com.projectn.projectn.common.enums.ItemStatus;
import com.projectn.projectn.common.enums.Status;
import com.projectn.projectn.config.MessageBuilder;
import com.projectn.projectn.exception.AppException;
import com.projectn.projectn.model.ItemPurchase;
import com.projectn.projectn.model.ShopItem;
import com.projectn.projectn.model.User;
import com.projectn.projectn.payload.response.ItemPurchaseResponse;
import com.projectn.projectn.payload.response.RespMessage;
import com.projectn.projectn.repository.ItemPurchaseRepository;
import com.projectn.projectn.repository.ShopItemRepository;
import com.projectn.projectn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemPurchaseService {
    private final MessageBuilder messageBuilder;
    private final ShopItemRepository shopItemRepository;
    private final UserRepository userRepository;
    private final ItemPurchaseRepository itemPurchaseRepository;
    public RespMessage purchaseItem(UUID id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{email}, "User not found");
        }

        Optional<ShopItem> shopItemOptional = shopItemRepository.findById(id);
        if (shopItemOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{id}, "Shop item not found");
        }

        if (!shopItemOptional.get().getUser().getId().equals(userOptional.get().getId())) {
            throw new AppException(Constant.UNAUTHORIZED, null, "Unauthorized");
        }

        if (shopItemOptional.get().getStatus() == ItemStatus.INACTIVE) {
            throw new AppException(Constant.FIELD_NOT_VALID, new Object[]{id}, "Shop item is inactive");
        }

        if (shopItemOptional.get().getStatus() == ItemStatus.SOLD_OUT) {
            throw new AppException(Constant.FIELD_NOT_VALID, new Object[]{id}, "Shop item is sold out");
        }

        ShopItem shopItem = shopItemOptional.get();
        if (shopItem.getPrice() > userOptional.get().getWallet()) {
            throw new AppException(Constant.FIELD_NOT_VALID, new Object[]{shopItem.getPrice()}, "Insufficient balance");
        }

        userOptional.get().setWallet(userOptional.get().getWallet() - shopItem.getPrice());
        userRepository.save(userOptional.get());

        ItemPurchase itemPurchase = ItemPurchase.builder()
                .shopItem(shopItem)
                .build();
        itemPurchaseRepository.save(itemPurchase);

        shopItem.setStatus(ItemStatus.SOLD_OUT);
        shopItemRepository.save(shopItem);
        return messageBuilder.buildSuccessMessage(mapToResponse(itemPurchase));
    }

    public RespMessage getPurchaseHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{email}, "User not found");
        }
        List<ItemPurchase> itemPurchases =itemPurchaseRepository.findAllByUserId(userOptional.get().getId());
        List<ItemPurchaseResponse> itemPurchaseResponses = itemPurchases.stream()
                .map(this::mapToResponse)
                .toList();
        return messageBuilder.buildSuccessMessage(itemPurchaseResponses);
    }

    private ItemPurchaseResponse mapToResponse(ItemPurchase itemPurchase) {
        return ItemPurchaseResponse.builder()
                .id(itemPurchase.getId())
                .shopItemId(itemPurchase.getShopItem().getId())
                .purchaseAt(itemPurchase.getPurchaseAt().toString())
                .build();
    }
}
