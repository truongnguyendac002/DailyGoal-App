package com.projectn.projectn.service;

import com.projectn.projectn.common.Constant;
import com.projectn.projectn.config.MessageBuilder;
import com.projectn.projectn.exception.AppException;
import com.projectn.projectn.model.DailyQuest;
import com.projectn.projectn.model.DiceRoll;
import com.projectn.projectn.model.User;
import com.projectn.projectn.payload.response.DiceRollResponse;
import com.projectn.projectn.payload.response.RespMessage;
import com.projectn.projectn.repository.DiceRollRepository;
import com.projectn.projectn.repository.QuestRepository;
import com.projectn.projectn.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiceRollService {
    private final DiceRollRepository diceRollRepository;
    private final QuestRepository dailyQuestRepository;
    private final UserService userService;
    private final MessageBuilder messageBuilder;
    private final UserRepository userRepository;

    @Transactional
    public RespMessage rollDice(UUID questId, int diceNumber) {
        Optional<DailyQuest> dailyQuestOptional = dailyQuestRepository.findById(questId);
        if (dailyQuestOptional.isEmpty()) {
            throw new AppException(Constant.FIELD_NOT_VALID, new Object[]{"DailyQuest.Id"}, "Daily Quest not found");
        }
        Optional<DiceRoll> diceRollOptional = diceRollRepository.findByDailyQuestId(questId);
        if (diceRollOptional.isPresent()) {
            throw new AppException(Constant.FIELD_EXISTED, new Object[]{"Rolled Turn"}, "You have already rolled the dice for this quest!");
        }
        DiceRoll diceRoll = new DiceRoll();
        DailyQuest dailyQuest = dailyQuestOptional.get();
        diceRoll.setDailyQuest(dailyQuest);
        diceRoll.setPoints(diceNumber);
        try {
            dailyQuest.setDone(true);
            dailyQuest.setPoints(diceNumber);
            dailyQuestRepository.save(dailyQuest);
            diceRollRepository.save(diceRoll);
            userService.addPoints(dailyQuest.getUser(), diceNumber);
        } catch (Exception e) {
            throw new AppException(Constant.SYSTEM_ERROR, new Object[]{"DiceRoll"}, "Save dice roll failed");
        }
        return messageBuilder.buildSuccessMessage(mapToResponse(diceRoll));
    }

    private DiceRollResponse mapToResponse(DiceRoll diceRoll) {
        return DiceRollResponse.builder()
                .id(diceRoll.getId())
                .questId(diceRoll.getDailyQuest().getId())
                .points(diceRoll.getPoints())
                .rollAt(diceRoll.getRollAt().toString())
                .build();
    }


    public RespMessage getTotalPointsToday(String dateString) {
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
        List<DiceRoll> diceRolls = diceRollRepository.findByUserAndRollAt(userOptional.get(), date);
        int totalPoints = diceRolls.stream().mapToInt(DiceRoll::getPoints).sum();
        return messageBuilder.buildSuccessMessage(totalPoints);
    }
}
