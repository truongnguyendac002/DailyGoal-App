package com.projectn.projectn.controller;

import com.projectn.projectn.common.Constant;
import com.projectn.projectn.common.GsonUtil;
import com.projectn.projectn.config.MessageBuilder;
import com.projectn.projectn.exception.AppException;
import com.projectn.projectn.payload.request.LoginRequest;
import com.projectn.projectn.payload.request.RegisterRequest;
import com.projectn.projectn.payload.response.RespMessage;
import com.projectn.projectn.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    public final AuthService authService;
    public final MessageBuilder messageBuilder;

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            RespMessage response = authService.login(loginRequest);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(response), HttpStatus.OK);
        } catch (AppException e) {
            RespMessage resp = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.OK);
        } catch (Exception e) {
            RespMessage resp = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(resp), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        try {
            RespMessage response = authService.register(registerRequest);
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(response), HttpStatus.OK);
        } catch (AppException e){
            RespMessage response = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(response), HttpStatus.OK);
        }
        catch (Exception e) {
            RespMessage response = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResponseEntity<String> getAccount() {
        try {
            RespMessage response = authService.getProfileByToken();
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(response), HttpStatus.OK);
        } catch (AppException e) {
            RespMessage response = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(response), HttpStatus.OK);
        }
        catch (Exception e) {
            RespMessage response = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
