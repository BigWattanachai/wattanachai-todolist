package com.bot.wattanachaitodolist.controller;

import com.bot.wattanachaitodolist.domain.Todo;
import com.bot.wattanachaitodolist.model.ApiResponse;
import com.bot.wattanachaitodolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("api/v1/")
public class TodoController {
    private TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("todos")
    public HttpEntity<ApiResponse> getAllTodos() {
        return todoService.getAllTodos();
    }

    @PutMapping("todos/{id}")
    public HttpEntity<ApiResponse> editTodo(@PathVariable("id") String id, @RequestBody Todo todo) {
        return todoService.editTodo(id, todo);
    }

    @GetMapping("todos/{id}")
    public HttpEntity<ApiResponse> getTodo(@PathVariable("id") String id) {
        return todoService.getTodo(id);
    }

    @GetMapping("create")
    public Todo createTodo() {
        Todo todo = new Todo();
        todo.setTask("task");
        todo.setCompleted(false);
        todo.setImportant(true);
        todo.setDate(new Date());
        return todoService.createTodo(todo);
    }
}
