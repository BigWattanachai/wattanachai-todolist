package com.bot.wattanachaitodolist.controller;

import com.bot.wattanachaitodolist.domain.Todo;
import com.bot.wattanachaitodolist.domain.TodoOrder;
import com.bot.wattanachaitodolist.exception.NotAuthorizedException;
import com.bot.wattanachaitodolist.infra.line.api.v2.response.AccessToken;
import com.bot.wattanachaitodolist.infra.line.api.v2.response.IdToken;
import com.bot.wattanachaitodolist.model.ApiResponse;
import com.bot.wattanachaitodolist.service.LineAPIService;
import com.bot.wattanachaitodolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

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

    @GetMapping("/todos")
    public HttpEntity<ApiResponse> getAllTodos(HttpSession httpSession) {
        IdToken idToken = getIdToken(httpSession);
        return todoService.getAllTodos(idToken.sub);
    }


    @PatchMapping("todos/{todoId}")
    public HttpEntity<ApiResponse> editTodo(HttpSession httpSession,
                                            @PathVariable("todoId") String id,
                                            @RequestBody Todo todo) {
        getIdToken(httpSession);
        return todoService.editTodo(id, todo);
    }

    @PutMapping("todos/order")
    public HttpEntity<ApiResponse> saveTodoOrder(HttpSession httpSession, @RequestBody TodoOrder todoOrder) {
        IdToken idToken = getIdToken(httpSession);
        return todoService.updateTodoOrder(idToken.sub, todoOrder);
    }

    private IdToken getIdToken(HttpSession httpSession) {
        AccessToken accessToken = Optional.ofNullable(getAccessToken(httpSession))
                .orElseThrow(NotAuthorizedException::new);
        return lineAPIService.idToken(accessToken.id_token);
    }

    private AccessToken getAccessToken(HttpSession httpSession) {
        return (AccessToken) httpSession.getAttribute(TodoWebController.ACCESS_TOKEN);
    }
}
