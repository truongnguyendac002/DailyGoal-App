package com.projectn.projectn.controller;

import com.projectn.projectn.exception.AppException;
import com.projectn.projectn.payload.request.QuestRequest;
import com.projectn.projectn.payload.response.RespMessage;
import com.projectn.projectn.service.QuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/quest")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class QuestController {
    private final QuestService questService;
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<RespMessage> getQuests(@RequestParam("date") String date ) {
        try {
            RespMessage response = questService.getQuests(date);
            return ResponseEntity.ok(response);
        } catch (AppException e) {
            RespMessage resp = new RespMessage(e.getCode(), e.getMessage(), null);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            RespMessage resp = new RespMessage("error", "An unexpected error occurred", null);
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<RespMessage> createQuest(@RequestBody QuestRequest questRequest) {
        try {
            RespMessage response = questService.createQuest(questRequest);
            return ResponseEntity.ok(response);
        } catch (AppException e) {
            RespMessage resp = new RespMessage(e.getCode(), e.getMessage(), null);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            RespMessage resp = new RespMessage("error", "An unexpected error occurred", null);
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "{questId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<RespMessage> updateQuest(@PathVariable UUID questId, @RequestBody QuestRequest questRequest) {
        try {
            RespMessage response = questService.updateQuest(questId, questRequest);
            return ResponseEntity.ok(response);
        } catch (AppException e) {
            RespMessage resp = new RespMessage(e.getCode(), e.getMessage(), null);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            RespMessage resp = new RespMessage("error", "An unexpected error occurred", null);
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "{questId}", method = RequestMethod.DELETE)
    public ResponseEntity<RespMessage> deleteQuest(@PathVariable UUID questId) {
        try {
            RespMessage response = questService.deleteQuest(questId);
            return ResponseEntity.ok(response);
        } catch (AppException e) {
            RespMessage resp = new RespMessage(e.getCode(), e.getMessage(), null);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            RespMessage resp = new RespMessage("error", "An unexpected error occurred", null);
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
