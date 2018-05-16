package com.bot.wattanachaitodolist.controller;

import com.bot.wattanachaitodolist.domain.Todo;
import com.bot.wattanachaitodolist.model.ApiResponse;
import com.bot.wattanachaitodolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/")
public class TodoController {
    private TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("{userId}/todos")
    public HttpEntity<ApiResponse> getAllTodos(@PathVariable("userId") String id) {
        return todoService.getAllTodos(id);
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
}
