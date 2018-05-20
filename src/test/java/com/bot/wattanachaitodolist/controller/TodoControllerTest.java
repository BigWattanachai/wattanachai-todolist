package com.bot.wattanachaitodolist.controller;

import com.bot.wattanachaitodolist.domain.Todo;
import com.bot.wattanachaitodolist.model.TodoOrder;
import com.bot.wattanachaitodolist.exception.NotFoundException;
import com.bot.wattanachaitodolist.infra.line.api.v2.response.AccessToken;
import com.bot.wattanachaitodolist.infra.line.api.v2.response.IdToken;
import com.bot.wattanachaitodolist.model.ApiResponse;
import com.bot.wattanachaitodolist.service.LineAPIService;
import com.bot.wattanachaitodolist.service.TodoService;
import com.bot.wattanachaitodolist.util.JsonMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TodoController.class)
public class TodoControllerTest {
    @MockBean
    private TodoService todoService;

    @MockBean
    private MappingMongoConverter mappingMongoConverter;

    @MockBean
    private LineAPIService lineAPIService;


    @Autowired
    private MockMvc mvc;

    private Map<String, Object> sessionAttrsMock;

    @Before
    public void beforeEach() {
        sessionAttrsMock = ImmutableMap.of(TodoWebController.ACCESS_TOKEN,
                AccessToken.builder().id_token("id_token").build());
        when(lineAPIService.idToken(anyString())).thenReturn(IdToken.builder().sub("userId").build());
    }

    @Test
    public void shouldReturnNotAuthorizedWhenGetTodoListWithUnAuthorizedUser() throws Exception {

        mvc.perform(get("/api/v1/todos"))
                .andExpect(jsonPath("$.message", is("Unauthorized")))
                .andExpect(status().isUnauthorized());

        verify(todoService, never()).getTodo(anyString());
        verify(lineAPIService, never()).idToken(anyString());
    }

    @Test
    public void shouldReturnTodosListWhenGetAllExitingTodosSuccessfully() throws Exception {
        Todo firstTodo = new Todo();
        firstTodo.setCreatedDate(new Date());
        firstTodo.setTask("firstTodo");
        Todo secondTodo = new Todo();
        secondTodo.setCreatedDate(new Date());
        secondTodo.setTask("secondTodo");
        when(todoService.getAllTodos(anyString()))
                .thenReturn(new ApiResponse(asList(firstTodo, secondTodo)).build(HttpStatus.OK));

        mvc.perform(get("/api/v1/todos")
                .sessionAttrs(sessionAttrsMock))
                .andExpect(jsonPath("$.message", is("success")))
                .andExpect(jsonPath("$.data[0].task", is("firstTodo")))
                .andExpect(jsonPath("$.data[1].task", is("secondTodo")))
                .andExpect(status().isOk());

        verify(todoService).getAllTodos(anyString());
        verify(lineAPIService).idToken(anyString());
    }

    @Test
    public void shouldReturnEmptyListWhenGetNotExistingTodosSuccessfully() throws Exception {
        when(todoService.getAllTodos(anyString())).thenReturn(new ApiResponse(Collections.EMPTY_LIST)
                .build(HttpStatus.OK));

        mvc.perform(get("/api/v1/todos")
                .sessionAttrs(sessionAttrsMock))
                .andExpect(jsonPath("$.message", is("success")))
                .andExpect(jsonPath("$.data", is(Collections.EMPTY_LIST)))
                .andExpect(status().isOk());

        verify(todoService).getAllTodos(anyString());
        verify(lineAPIService).idToken(anyString());

    }

    @Test
    public void shouldReturnTodosListWhenUpdateTodosOrderSuccessfully() throws Exception {
        TodoOrder todoOrder = new TodoOrder();
        todoOrder.setTodoOrder(asList("id1", "id2"));
        when(todoService.updateTodoOrder(anyString(), any(TodoOrder.class)))
                .thenReturn(new ApiResponse("success", todoOrder).build(HttpStatus.OK));

        mvc.perform(put("/api/v1/todos/order")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonMapper.toJson(todoOrder).orElse(""))
                .sessionAttrs(sessionAttrsMock))
                .andExpect(jsonPath("$.message", is("success")))
                .andExpect(status().isOk());

        verify(todoService).updateTodoOrder(anyString(), any(TodoOrder.class));
        verify(lineAPIService).idToken(anyString());
    }

    @Test
    public void shouldReturnUpdatedTodoWhenUpdateExitingTodoSuccessfully() throws Exception {
        Todo todo = new Todo();
        todo.setTodoId("Id");
        todo.setTask("UpdateTodo");

        when(todoService.editTodo(anyString(), any(Todo.class))).thenReturn(new ApiResponse(todo).build(HttpStatus.OK));

        mvc.perform(patch("/api/v1/todos/id")
                .sessionAttrs(sessionAttrsMock)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonMapper.toJson(todo).orElse("")))
                .andExpect(jsonPath("$.message", is("success")))
                .andExpect(jsonPath("$.data.todoId", is("Id")))
                .andExpect(jsonPath("$.data.task", is("UpdateTodo")))
                .andExpect(status().isOk());

        verify(todoService).editTodo(anyString(), any(Todo.class));
        verify(lineAPIService).idToken(anyString());
    }

    @Test
    public void shouldReturnNotFoundWhenUpdateNotExitingTodo() throws Exception {
        Todo todo = new Todo();
        todo.setTodoId("Id");
        todo.setTask("UpdateTodo");

        when(todoService.editTodo(anyString(), any(Todo.class)))
                .thenReturn(new ApiResponse("not found", null).build(HttpStatus.NOT_FOUND));

        mvc.perform(patch("/api/v1/todos/id")
                .sessionAttrs(sessionAttrsMock)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonMapper.toJson(todo).orElse("")))
                .andExpect(jsonPath("$.message", is("not found")))
                .andExpect(jsonPath("$.data", is(nullValue())))
                .andExpect(status().is4xxClientError());

        verify(todoService).editTodo(anyString(), any(Todo.class));
        verify(lineAPIService).idToken(anyString());
    }

    @Test
    public void shouldReturnNotFoundWhenUpdateTodosOrderWithNotExistingUser() throws Exception {
        TodoOrder todoOrder = new TodoOrder();
        todoOrder.setTodoOrder(asList("id1", "id2"));
        doThrow(new NotFoundException("User not found"))
                .when(todoService).updateTodoOrder(anyString(), any(TodoOrder.class));

        mvc.perform(put("/api/v1/todos/order")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonMapper.toJson(todoOrder).orElse(""))
                .sessionAttrs(sessionAttrsMock))
                .andExpect(jsonPath("$.message", is("User not found")))
                .andExpect(status().is4xxClientError());

        verify(todoService).updateTodoOrder(anyString(), any(TodoOrder.class));
        verify(lineAPIService).idToken(anyString());
    }
}
