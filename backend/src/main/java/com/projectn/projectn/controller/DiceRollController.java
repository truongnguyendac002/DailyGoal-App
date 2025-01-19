package com.projectn.projectn.controller;

import com.projectn.projectn.common.Constant;
import com.projectn.projectn.common.GsonUtil;
import com.projectn.projectn.config.MessageBuilder;
import com.projectn.projectn.exception.AppException;
import com.projectn.projectn.payload.response.RespMessage;
import com.projectn.projectn.service.DiceRollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/diceRoll")
@RequiredArgsConstructor
public class DiceRollController {
    private final DiceRollService diceRollService;
    private final MessageBuilder messageBuilder;

    @RequestMapping(value = "{questId}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<RespMessage> rollDice(@PathVariable UUID questId, @RequestParam int diceNumber) {
        try {
            RespMessage response = diceRollService.rollDice(questId,diceNumber);
            return ResponseEntity.ok(response);
        } catch (AppException e) {
            RespMessage resp = new RespMessage(e.getCode(), e.getMessage(), null);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            RespMessage resp = new RespMessage("error", "An unexpected error occurred", null);
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "total", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getTotalPointsToday(@RequestParam("date") String date) {
        try {
            RespMessage response = diceRollService.getTotalPointsToday(date);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(response), HttpStatus.OK);
        } catch (AppException e) {
            RespMessage resp = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.OK);
        } catch (Exception e) {
            RespMessage resp = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
