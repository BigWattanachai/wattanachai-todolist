package com.bot.wattanachaitodolist.controller;

import com.bot.wattanachaitodolist.domain.Todo;
import com.bot.wattanachaitodolist.infra.line.api.v2.response.AccessToken;
import com.bot.wattanachaitodolist.infra.line.api.v2.response.IdToken;
import com.bot.wattanachaitodolist.model.ApiResponse;
import com.bot.wattanachaitodolist.service.LineAPIService;
import com.bot.wattanachaitodolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("api/v1/")
public class TodoController {
    private TodoService todoService;
    private LineAPIService lineAPIService;

    @Autowired
    public TodoController(TodoService todoService, LineAPIService lineAPIService) {
        this.todoService = todoService;
        this.lineAPIService = lineAPIService;
    }

    @GetMapping("{userId}/todos")
    public HttpEntity<ApiResponse> getAllTodosByUserId(@PathVariable("userId") String id) {
        return todoService.getAllTodos(id);
    }

    @GetMapping("/todos")
    public HttpEntity<ApiResponse> getAllTodos(HttpSession httpSession) {
        AccessToken accessToken = getAccessToken(httpSession);
        if (accessToken != null) {
            IdToken idToken = lineAPIService.idToken(accessToken.id_token);
            return todoService.getAllTodos(idToken.aud);
        } else {
            return new ApiResponse("Unauthorized", null).build(HttpStatus.UNAUTHORIZED);
        }
    }


    @PutMapping("todos/{todoId}")
    public HttpEntity<ApiResponse> editTodo(@PathVariable("todoId") String id,
                                            @RequestBody Todo todo) {
        return todoService.editTodo(id, todo);
    }

    @GetMapping("todos/{todoId}")
    public HttpEntity<ApiResponse> getTodo(@PathVariable("todoId") String id) {
        return todoService.getTodo(id);
    }

    private AccessToken getAccessToken(HttpSession httpSession) {
        return (AccessToken) httpSession.getAttribute(TodoWebController.ACCESS_TOKEN);
    }

    private void setAccessToken(HttpSession httpSession, AccessToken accessToken) {
        httpSession.setAttribute(TodoWebController.ACCESS_TOKEN, accessToken);
    }

}
