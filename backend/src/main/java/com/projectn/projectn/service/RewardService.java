package com.projectn.projectn.service;

import com.projectn.projectn.common.Constant;
import com.projectn.projectn.common.enums.Status;
import com.projectn.projectn.config.MessageBuilder;
import com.projectn.projectn.exception.AppException;
import com.projectn.projectn.model.DailyReward;
import com.projectn.projectn.model.User;
import com.projectn.projectn.payload.request.DailyRewardRequest;
import com.projectn.projectn.payload.response.DailyRewardResponse;
import com.projectn.projectn.payload.response.RespMessage;
import com.projectn.projectn.repository.RewardRepository;
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
public class RewardService {
    private final RewardRepository rewardRepository;
    private final UserRepository userRepository;
    private final MessageBuilder messageBuilder;
    private final CloudinaryService cloudinaryService;
    public RespMessage getRewards() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{email}, "User not found");
        }
        List<DailyReward> dailyRewards = rewardRepository.findByUserId(userOptional.get().getId());
        List<DailyRewardResponse> rewardResponses = dailyRewards.stream()
                .map(this::mapToResponse)
                .toList();
        return messageBuilder.buildSuccessMessage(rewardResponses);
    }

    public RespMessage createReward(DailyRewardRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{email}, "User not found");
        }
        DailyReward dailyReward = DailyReward.builder()
                .name(request.getName())
                .description(request.getDescription())
                .minPoint(request.getMinPoint())
                .user(userOptional.get())
                .build();
        rewardRepository.save(dailyReward);
        return messageBuilder.buildSuccessMessage(mapToResponse(dailyReward));
    }

    public RespMessage updateReward(UUID id, DailyRewardRequest request) {
        Optional<DailyReward> dailyRewardOptional = rewardRepository.findById(id);
        if (dailyRewardOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{id}, "Reward not found");
        }
        DailyReward dailyReward = dailyRewardOptional.get();
        dailyReward.setName(request.getName());
        dailyReward.setDescription(request.getDescription());
        dailyReward.setMinPoint(request.getMinPoint());
        rewardRepository.save(dailyReward);
        return messageBuilder.buildSuccessMessage(mapToResponse(dailyReward));
    }



    private DailyRewardResponse mapToResponse(DailyReward dailyReward) {
        return DailyRewardResponse.builder()
                .id(dailyReward.getId())
                .name(dailyReward.getName())
                .imageUrl(dailyReward.getImageUrl())
                .description(dailyReward.getDescription())
                .minPoint(dailyReward.getMinPoint())
                .status(dailyReward.getStatus().name())
                .build();
    }

    public RespMessage updateImage(UUID rewardId, MultipartFile file) {
        Optional<DailyReward> dailyRewardOptional = rewardRepository.findById(rewardId);
        if (dailyRewardOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{rewardId}, "Reward not found");
        }

        Map<String, Object> data = cloudinaryService.upload(file, "DailyReward");
        String url = (String) data.get("secure_url");

        DailyReward dailyReward = dailyRewardOptional.get();
        dailyReward.setImageUrl(url);
        rewardRepository.save(dailyReward);

        return messageBuilder.buildSuccessMessage(mapToResponse(dailyReward));
    }

    public RespMessage deleteImage(UUID id) {
        Optional<DailyReward> dailyRewardOptional = rewardRepository.findById(id);
        if (dailyRewardOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{id}, "Reward not found");
        }
        DailyReward dailyReward = dailyRewardOptional.get();
        cloudinaryService.delete(dailyReward.getImageUrl());
        dailyReward.setImageUrl(null);
        rewardRepository.save(dailyReward);
        return messageBuilder.buildSuccessMessage(mapToResponse(dailyReward));
    }

    public RespMessage deleteReward(UUID id) {
        Optional<DailyReward> dailyRewardOptional = rewardRepository.findById(id);
        if (dailyRewardOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{id}, "Reward not found");
        }
        DailyReward dailyReward = dailyRewardOptional.get();
        dailyReward.setStatus(Status.INACTIVE);
        rewardRepository.save(dailyReward);
        return messageBuilder.buildSuccessMessage(mapToResponse(dailyReward));
    }
}
