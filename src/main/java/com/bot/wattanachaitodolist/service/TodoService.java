package com.bot.wattanachaitodolist.service;

import com.bot.wattanachaitodolist.domain.Todo;
import com.bot.wattanachaitodolist.domain.TodoOrder;
import com.bot.wattanachaitodolist.domain.User;
import com.bot.wattanachaitodolist.model.ApiResponse;
import com.bot.wattanachaitodolist.repository.TodoRepository;
import com.bot.wattanachaitodolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {
    private UserRepository userRepository;
    private TodoRepository todoRepository;

    @Autowired
    public TodoService(UserRepository userRepository, TodoRepository todoRepository) {
        this.userRepository = userRepository;
        this.todoRepository = todoRepository;
    }

    public HttpEntity<ApiResponse> getAllTodos(String userId) {
        return userRepository.findByUserId(userId).map(it ->
                new ApiResponse(it.getTodoList()).build(HttpStatus.OK))
                .orElse(new ApiResponse("User not found", null).build(HttpStatus.NOT_FOUND));
    }

    public HttpEntity<ApiResponse> editTodo(String todoId, Todo todo) {
        return todoRepository.findOne(todoId)
                .map(it -> updateTodo(todo, it))
                .orElse(new ApiResponse("Todo not found", null).build(HttpStatus.NOT_FOUND));
    }

    private HttpEntity<ApiResponse> updateTodo(Todo todo, Todo it) {
        if (todo.getTask() != null) {
            it.setTask(todo.getTask());
        }
        if (todo.getDate() != null) {
            it.setDate(todo.getDate());
        }
        if (todo.getCompleted() != null) {
            it.setCompleted(todo.getCompleted());
        }
        if (todo.getImportant() != null) {
            it.setImportant(todo.getImportant());
        }
        return new ApiResponse(todoRepository.save(it)).build(HttpStatus.OK);
    }

    public HttpEntity<ApiResponse> getTodo(String todoId) {
        return todoRepository.findOne(todoId)
                .map(updatedTodo -> new ApiResponse(updatedTodo).build(HttpStatus.OK))
                .orElse(new ApiResponse("Todo not found", null).build(HttpStatus.NOT_FOUND));
    }

    public HttpEntity<ApiResponse> updateTodoOrder(String userId, TodoOrder todoOrder) {
        return userRepository.findByUserId(userId).map
                (it -> {
                    List<Todo> todoList = getNewOderTodoList(todoOrder.getTodoOrder());
                    it.setTodoList(todoList);
                    User user = userRepository.save(it);
                    return new ApiResponse(user.getTodoList()).build(HttpStatus.OK);
                }).orElse(new ApiResponse("User not found", null).build(HttpStatus.NOT_FOUND));

    }

    private List<Todo> getNewOderTodoList(List<String> idsList) {
        return idsList.stream().map(todoId -> {
            Todo todo = new Todo();
            todo.setTodoId(todoId);
            return todo;
        }).collect(Collectors.toList());
    }
}
