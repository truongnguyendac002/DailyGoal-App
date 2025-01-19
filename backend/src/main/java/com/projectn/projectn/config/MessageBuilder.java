package com.projectn.projectn.config;

import com.projectn.projectn.common.Constant;
import com.projectn.projectn.payload.response.RespMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageBuilder {
    private final MessageSource messageSource;

    @Autowired()
    public MessageBuilder(final @Qualifier("com.projectn.projectn.config.messageSource") MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public RespMessage buildFailureMessage(String code, Object[] objects, Object data) {
        String desc = messageSource.getMessage(code, objects, Constant.UNDEFINED, LocaleContextHolder.getLocale());
        return RespMessage.builder()
                .respCode(code)
                .respDesc(desc)
                .data(data)
                .build();
    }

    public RespMessage buildSuccessMessage(Object data) {
        String desc = messageSource.getMessage(Constant.SUCCESS, null, Constant.UNDEFINED, LocaleContextHolder.getLocale());
        return RespMessage.builder()
                .respCode(Constant.SUCCESS)
                .respDesc(desc)
                .data(data)
                .build();
    }
}
