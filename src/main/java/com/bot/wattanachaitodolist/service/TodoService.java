package com.bot.wattanachaitodolist.service;

import com.bot.wattanachaitodolist.domain.Todo;
import com.bot.wattanachaitodolist.model.ApiResponse;
import com.bot.wattanachaitodolist.repository.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class TodoService {
    private TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo createTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    public HttpEntity<ApiResponse> getAllTodos() {
        return new ApiResponse(todoRepository.findAllByOrderByUpdatedDateAsc()).build(HttpStatus.OK);
    }

    public HttpEntity<ApiResponse> editTodo(String id, Todo todo) {
        return Optional.ofNullable(todoRepository.findOne(id))
                .map(it -> {
                    it.setTask(todo.getTask());
                    it.setDate(todo.getDate());
                    it.setCompleted(todo.isCompleted());
                    it.setImportant(todo.isImportant());
                    return todoRepository.save(it);
                })
                .map(updatedTodo -> new ApiResponse(updatedTodo).build(HttpStatus.OK))
                .orElse(new ApiResponse("not found", null).build(HttpStatus.NOT_FOUND));
    }

    public HttpEntity<ApiResponse> getTodo(String id) {
        return Optional.ofNullable(todoRepository.findOne(id))
                .map(updatedTodo -> new ApiResponse(updatedTodo).build(HttpStatus.OK))
                .orElse(new ApiResponse("not found", null).build(HttpStatus.NOT_FOUND));
    }
}
