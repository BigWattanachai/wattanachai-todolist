package com.bot.wattanachaitodolist.controller;

import com.bot.wattanachaitodolist.exception.NotAuthorizedException;
import com.bot.wattanachaitodolist.model.ApiResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(value = NotAuthorizedException.class)
    public HttpEntity<ApiResponse> handleNotAuthorizedException() {
        return new ApiResponse("Unauthorized", null).build(HttpStatus.UNAUTHORIZED);
    }
}
