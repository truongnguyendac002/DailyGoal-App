package com.projectn.projectn.service;

import com.projectn.projectn.common.Constant;
import com.projectn.projectn.common.enums.ItemStatus;
import com.projectn.projectn.common.enums.Status;
import com.projectn.projectn.config.MessageBuilder;
import com.projectn.projectn.exception.AppException;
import com.projectn.projectn.model.ShopItem;
import com.projectn.projectn.model.User;
import com.projectn.projectn.payload.request.ShopItemRequest;
import com.projectn.projectn.payload.response.RespMessage;
import com.projectn.projectn.payload.response.ShopItemResponse;
import com.projectn.projectn.repository.ItemPurchaseRepository;
import com.projectn.projectn.repository.ShopItemRepository;
import com.projectn.projectn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final MessageBuilder messageBuilder;
    private final ShopItemRepository shopItemRepository;
    private final UserRepository userRepository;
    private final ItemPurchaseRepository itemPurchaseRepository;
    private final CloudinaryService cloudinaryService;

    public RespMessage getShopItems() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{email}, "User not found");
        }
        List<ShopItem> shopItems = shopItemRepository.findAllByUser(userOptional.get());
        List<ShopItemResponse> shopItemResponses = shopItems.stream()
                .map(this::mapToResponse)
                .toList();
        return messageBuilder.buildSuccessMessage(shopItemResponses);
    }

    public RespMessage createShopItem(ShopItemRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{email}, "User not found");
        }
        ShopItem shopItem = ShopItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .user(userOptional.get())
                .build();
        shopItemRepository.save(shopItem);
        return messageBuilder.buildSuccessMessage(mapToResponse(shopItem));
    }

    public RespMessage updateShopItem(UUID shopItemId, ShopItemRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{email}, "User not found");
        }

        Optional<ShopItem> shopItemOptional = shopItemRepository.findById(shopItemId);
        if (shopItemOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{shopItemId}, "Shop item not found");
        }

        if (!shopItemOptional.get().getUser().getId().equals(userOptional.get().getId())) {
            throw new AppException(Constant.UNAUTHORIZED, null, "Unauthorized");
        }

        ShopItem shopItem = shopItemOptional.get();
        shopItem.setName(request.getName());
        shopItem.setDescription(request.getDescription());
        shopItem.setPrice(request.getPrice());
        shopItem.setImageUrl(request.getImageUrl());
        shopItemRepository.save(shopItem);

        return messageBuilder.buildSuccessMessage(shopItem);
    }

    public RespMessage deleteShopItem(UUID shopItemId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{email}, "User not found");
        }
        Optional<ShopItem> shopItemOptional = shopItemRepository.findById(shopItemId);
        if (shopItemOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{shopItemId}, "Shop item not found");
        }
        if (!shopItemOptional.get().getUser().getId().equals(userOptional.get().getId())) {
            throw new AppException(Constant.UNAUTHORIZED, null, "Unauthorized");
        }

        ShopItem shopItem = shopItemOptional.get();
        shopItem.setStatus(ItemStatus.INACTIVE);
        shopItemRepository.save(shopItem);

        return messageBuilder.buildSuccessMessage(mapToResponse(shopItem));
    }

    public ShopItemResponse mapToResponse(ShopItem shopItem) {
        return ShopItemResponse.builder()
                .id(shopItem.getId())
                .name(shopItem.getName())
                .imageUrl(shopItem.getImageUrl())
                .description(shopItem.getDescription())
                .price(shopItem.getPrice())
                .status(shopItem.getStatus().name())
                .build();
    }


    public RespMessage updateImage(UUID id, MultipartFile file) {
        Optional<ShopItem> shopItemOptional = shopItemRepository.findById(id);
        if (shopItemOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{id}, "Shop item not found");
        }
        Map<String, Object> data = cloudinaryService.upload(file, "ShopItem");
        String url = (String) data.get("secure_url");

        ShopItem shopItem = shopItemOptional.get();
        if (shopItem.getImageUrl() != null) {
            cloudinaryService.delete(shopItem.getImageUrl());
        }
        shopItem.setImageUrl(url);

        shopItemRepository.save(shopItem);

        return messageBuilder.buildSuccessMessage(mapToResponse(shopItem));
    }

//    public RespMessage deleteImage(UUID id) {
//        Optional<ShopItem> shopItemOptional = shopItemRepository.findById(id);
//        if (shopItemOptional.isEmpty()) {
//            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{id}, "Shop item not found");
//        }
//
//        ShopItem shopItem = shopItemOptional.get();
//        cloudinaryService.delete(shopItem.getImageUrl());
//        shopItem.setImageUrl(null);
//        shopItemRepository.save(shopItem);
//
//        return messageBuilder.buildSuccessMessage(mapToResponse(shopItem));
//    }

}
