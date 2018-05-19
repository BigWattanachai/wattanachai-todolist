package com.bot.wattanachaitodolist.service;

import com.bot.wattanachaitodolist.domain.Todo;
import com.bot.wattanachaitodolist.domain.TodoOrder;
import com.bot.wattanachaitodolist.domain.User;
import com.bot.wattanachaitodolist.exception.NotFoundException;
import com.bot.wattanachaitodolist.model.ApiResponse;
import com.bot.wattanachaitodolist.repository.TodoRepository;
import com.bot.wattanachaitodolist.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;

import java.util.Date;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TodoServiceTest {
    @InjectMocks
    private TodoService todoService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TodoRepository todoRepository;

    @Test
    public void shouldReturnAllTodoWithPrioritySortedWhenGetExistingTodoInDatabase() {
        User user = new User();
        user.setId("user1");
        Todo todo1 = new Todo();
        todo1.setImportant(false);
        todo1.setTodoId("todo1");
        Todo todo2 = new Todo();
        todo2.setImportant(true);
        todo2.setTodoId("todo2");
        user.setTodoList(asList(todo1, todo2));

        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));

        HttpEntity<ApiResponse> responseHttpEntity = todoService.getAllTodos("user1");
        ApiResponse apiResponse = responseHttpEntity.getBody();
        assertThat(apiResponse.getMessage()).isEqualTo("success");
        assertThat(apiResponse.getData()).isEqualTo(asList(todo2, todo1));

        verify(userRepository).findByUserId(anyString());
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowExceptionWhenGetAllTodosWithNotExistingUserInDatabase() {
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        todoService.getAllTodos("user1");

        verify(userRepository).findByUserId(anyString());
    }

    @Test
    public void shouldReturnTodoWhenUpdateExistingTodoWithAllFieldUpdatedSuccessfully() {
        Todo todo1 = new Todo();
        todo1.setImportant(false);
        todo1.setTodoId("todo1Update");
        todo1.setTask("task");
        todo1.setCompleted(true);
        todo1.setImportant(true);
        todo1.setDate(new Date());

        when(todoRepository.findOne(anyString())).thenReturn(Optional.of(todo1));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo1);

        HttpEntity<ApiResponse> responseHttpEntity = todoService.editTodo("todo1", todo1);
        ApiResponse apiResponse = responseHttpEntity.getBody();
        assertThat(apiResponse.getMessage()).isEqualTo("success");
        assertThat(apiResponse.getData()).isEqualTo(todo1);

        verify(todoRepository).findOne(anyString());
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    public void shouldReturnTodoWhenUpdateExistingTodoUpdatedSuccessfully() {
        Todo todo1 = new Todo();
        todo1.setTodoId("todo1Update");

        when(todoRepository.findOne(anyString())).thenReturn(Optional.of(todo1));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo1);

        HttpEntity<ApiResponse> responseHttpEntity = todoService.editTodo("todo1", todo1);

        ApiResponse apiResponse = responseHttpEntity.getBody();
        assertThat(apiResponse.getMessage()).isEqualTo("success");
        assertThat(apiResponse.getData()).isEqualTo(todo1);

        verify(todoRepository).findOne(anyString());
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    public void shouldReturnTodoWhenGetExistingTodoInDatabase() {
        Todo todo1 = new Todo();
        todo1.setTodoId("todo1");
        when(todoRepository.findOne(anyString())).thenReturn(Optional.of(todo1));

        HttpEntity<ApiResponse> responseHttpEntity = todoService.getTodo("todo1");
        ApiResponse apiResponse = responseHttpEntity.getBody();
        assertThat(apiResponse.getMessage()).isEqualTo("success");
        assertThat(apiResponse.getData()).isEqualTo(todo1);

        verify(todoRepository).findOne(anyString());
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundExceptionWhenUpdateNotExistingTodoInDatabase() {
        when(todoRepository.findOne(anyString())).thenReturn(Optional.empty());

        todoService.editTodo("todo1", new Todo());

        verify(todoRepository).findOne(anyString());
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundExceptionWhenGetNotExistingTodoInDatabase() {
        when(todoRepository.findOne(anyString())).thenReturn(Optional.empty());

        todoService.getTodo("todo1");

        verify(todoRepository).findOne(anyString());
    }

    @Test
    public void shouldReturnTodoListWhenUpdateTodosListOderSuccessfully() {
        User user = new User();
        user.setId("user1");
        Todo todo1 = new Todo();
        todo1.setTodoId("todo1");
        Todo todo2 = new Todo();
        todo2.setTodoId("todo2");
        user.setTodoList(asList(todo1, todo2));
        TodoOrder todoOrder = new TodoOrder();
        todoOrder.setTodoOrder(asList("todo2", "todo1"));

        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));
        user.setTodoList(asList(todo2, todo1));
        when(userRepository.save(any(User.class))).thenReturn(user);

        HttpEntity<ApiResponse> responseHttpEntity = todoService.updateTodoOrder("user1", todoOrder);
        ApiResponse apiResponse = responseHttpEntity.getBody();
        assertThat(apiResponse.getMessage()).isEqualTo("success");
        assertThat(apiResponse.getData()).isEqualTo(asList(todo2, todo1));

        verify(userRepository).findByUserId(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFounfExceptionWhenUpdateTodosListOrderWithNotExistingUserInDatabase() {
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        todoService.updateTodoOrder("user1", new TodoOrder());

        verify(userRepository).findByUserId(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
