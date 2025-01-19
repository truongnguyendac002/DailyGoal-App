package com.projectn.projectn.controller;

import com.projectn.projectn.common.Constant;
import com.projectn.projectn.common.GsonUtil;
import com.projectn.projectn.config.MessageBuilder;
import com.projectn.projectn.exception.AppException;
import com.projectn.projectn.payload.request.DailyRewardRequest;
import com.projectn.projectn.payload.response.RespMessage;
import com.projectn.projectn.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/reward")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class RewardController {
    private final RewardService rewardService;
    private final MessageBuilder messageBuilder;
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getRewards() {
        try {
            RespMessage response = rewardService.getRewards();
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(response), HttpStatus.OK);
        } catch (AppException e) {
            RespMessage resp = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.OK);
        } catch (Exception e) {
            RespMessage resp = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> newReward(@RequestBody DailyRewardRequest request) {
        try {
            RespMessage response = rewardService.createReward(request);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(response), HttpStatus.OK);
        } catch (AppException e) {
            RespMessage resp = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.OK);
        } catch (Exception e) {
            RespMessage resp = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<String> updateReward(@PathVariable("id") UUID id, @RequestBody DailyRewardRequest request) {
        try {
            RespMessage response = rewardService.updateReward(id, request);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(response), HttpStatus.OK);
        } catch (AppException e) {
            RespMessage resp = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.OK);
        } catch (Exception e) {
            RespMessage resp = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}/image", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> updateImage(@PathVariable("id") UUID id,@RequestParam("file") MultipartFile file) {
        try {
            RespMessage resp = rewardService.updateImage(id,file);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.OK);
        } catch (AppException e) {
            RespMessage resp = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.OK);
        } catch (Exception e) {
            RespMessage resp = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}/image", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<String> deleteImage(@PathVariable("id") UUID id) {
        try {
            RespMessage resp = rewardService.deleteImage(id);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.OK);
        } catch (AppException e) {
            RespMessage resp = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.OK);
        } catch (Exception e) {
            RespMessage resp = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<String> deleteReward(@PathVariable("id") UUID id) {
        try {
            RespMessage resp = rewardService.deleteReward(id);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.OK);
        } catch (AppException e) {
            RespMessage resp = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.OK);
        } catch (Exception e) {
            RespMessage resp = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
