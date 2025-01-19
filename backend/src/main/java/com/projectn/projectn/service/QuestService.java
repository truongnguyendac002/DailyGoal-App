package com.projectn.projectn.service;

import com.projectn.projectn.common.Constant;
import com.projectn.projectn.exception.AppException;
import com.projectn.projectn.model.DailyQuest;
import com.projectn.projectn.model.User;
import com.projectn.projectn.payload.request.QuestRequest;
import com.projectn.projectn.payload.response.QuestResponse;
import com.projectn.projectn.payload.response.RespMessage;
import com.projectn.projectn.repository.QuestRepository;
import com.projectn.projectn.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Data
@RequiredArgsConstructor
public class QuestService {
    private final QuestRepository questRepository;
    private final UserRepository userRepository;

    public void validateRequest(QuestRequest questRequest) {
        String target = questRequest.getTarget();
        if (target == null || target.isEmpty()) {
            throw new AppException(Constant.FIELD_NOT_NULL, new Object[]{"target"}, "Target is required");
        }
        String description = questRequest.getDescription();
        if (description == null || description.isEmpty()) {
            throw new AppException(Constant.FIELD_NOT_NULL, new Object[]{"description"}, "Description is required");
        }
    }
    public RespMessage getQuests(String dateString) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{email}, "User not found");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date;
        try {
            date = dateFormat.parse(dateString);
        } catch (Exception e) {
            throw new AppException(Constant.FIELD_NOT_VALID, new Object[]{dateString}, "Invalid date format");
        }
        List<DailyQuest> dailyQuests = questRepository.findByUserIdAndForDate(userOptional.get().getId(), date);
        List<QuestResponse> questResponses = dailyQuests.stream()
                .map(this::mapToResponse)
                .toList();
        return new RespMessage(Constant.SUCCESS, "Quests retrieved successfully", questResponses);
    }

    @Transactional
    public RespMessage createQuest(QuestRequest questRequest) {
        validateRequest(questRequest);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{email}, "User not found");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date forDate;
        try {
            forDate = dateFormat.parse(questRequest.getForDate());
        } catch (Exception e) {
            throw new AppException(Constant.FIELD_NOT_VALID, new Object[]{questRequest.getForDate()}, "Invalid date format");
        }
        DailyQuest dailyQuest = DailyQuest.builder()
                .target(questRequest.getTarget() )
                .description(questRequest.getDescription())
                .user(userOptional.get())
                .forDate(forDate)
                .done(false)
                .build();
        try{
            DailyQuest quest = questRepository.save(dailyQuest);
            return new RespMessage(Constant.SUCCESS, "Quest created successfully", mapToResponse(quest));

        } catch (Exception e) {
            throw new AppException(Constant.SAVE_ERROR, new Object[]{e.getMessage()}, "An unexpected error occurred");
        }
    }

    public RespMessage updateQuest(UUID questId, QuestRequest questRequest) {
        validateRequest(questRequest);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{email}, "User not found");
        }

        Optional<DailyQuest> dailyQuestOptional = questRepository.findById(questId);
        if (dailyQuestOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{questId}, "Quest not found");
        }
        DailyQuest dailyQuest = dailyQuestOptional.get();
        if (dailyQuest.getUser().getId() != userOptional.get().getId()) {
            throw new AppException(Constant.UNAUTHORIZED, new Object[]{}, "Unauthorized");
        }

        dailyQuest.setTarget(questRequest.getTarget());
        dailyQuest.setDescription(questRequest.getDescription());
        dailyQuest.setDone(questRequest.isDone());

        try {
            questRepository.save(dailyQuest);
        } catch (Exception e) {
            throw new AppException(Constant.SAVE_ERROR, new Object[]{e.getMessage()}, "An unexpected error occurred");
        }

        return new RespMessage(Constant.SUCCESS, "Quest updated successfully", mapToResponse(dailyQuest));
    }

    public RespMessage deleteQuest(UUID questId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{email}, "User not found");
        }

        Optional<DailyQuest> dailyQuestOptional = questRepository.findById(questId);
        if (dailyQuestOptional.isEmpty()) {
            throw new AppException(Constant.ENTITY_NOT_FOUND, new Object[]{questId}, "Quest not found");
        }
        DailyQuest dailyQuest = dailyQuestOptional.get();
        if (dailyQuest.getUser().getId() != userOptional.get().getId()) {
            throw new AppException(Constant.UNAUTHORIZED, new Object[]{}, "Unauthorized");
        }
        try {
            questRepository.delete(dailyQuest);
        } catch (Exception e) {
            throw new AppException(Constant.DELETE_ERROR, new Object[]{e.getMessage()}, "An unexpected error occurred");
        }
        return new RespMessage(Constant.SUCCESS, "Quest deleted successfully", null);
    }

    private QuestResponse mapToResponse(DailyQuest dailyQuest) {
        return QuestResponse.builder()
                .id(dailyQuest.getId())
                .target(dailyQuest.getTarget())
                .description(dailyQuest.getDescription())
                .forDate(dailyQuest.getForDate().toString())
                .isDone(dailyQuest.isDone())
                .points(dailyQuest.getPoints())
                .build();
    }
}
