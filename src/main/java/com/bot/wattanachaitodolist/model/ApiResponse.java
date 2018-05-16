package com.bot.wattanachaitodolist.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private String message;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Object data;

    public ApiResponse(Object data) {
        this.data = data;
        this.message = "success";
    }

    public HttpEntity<ApiResponse> build(HttpStatus status) {
        return new ResponseEntity<>(this, status);
    }
}
