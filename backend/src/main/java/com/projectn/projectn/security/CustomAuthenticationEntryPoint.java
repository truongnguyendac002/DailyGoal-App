package com.projectn.projectn.security;

import com.projectn.projectn.common.Constant;
import com.projectn.projectn.common.GsonUtil;
import com.projectn.projectn.config.MessageBuilder;
import com.projectn.projectn.payload.response.RespMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    public final MessageBuilder messageBuilder;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        RespMessage respMessage = request.getAttribute("exception") != null ? (RespMessage) request.getAttribute("exception") : messageBuilder.buildFailureMessage(Constant.UNAUTHORIZED, null, "Authentication error: " + authException);
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(GsonUtil.getInstance().toJson(respMessage));
    }
}
