package com.bot.wattanachaitodolist.controller;

import com.bot.wattanachaitodolist.domain.Todo;
import com.bot.wattanachaitodolist.model.ApiResponse;
import com.bot.wattanachaitodolist.service.TodoService;
import com.bot.wattanachaitodolist.util.JsonMapper;
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

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TodoController.class)
public class TodoControllerTest {
    @MockBean
    private TodoService todoService;
    @MockBean
    private MappingMongoConverter mappingMongoConverter;

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldReturnTodosListWhenGetAllExitingTodosSuccessfully() throws Exception {
        Todo firstTodo = new Todo();
        firstTodo.setCreatedDate(new Date());
        firstTodo.setTask("firstTodo");
        Todo secondTodo = new Todo();
        secondTodo.setCreatedDate(new Date());
        secondTodo.setTask("secondTodo");

        when(todoService.getAllTodos()).thenReturn(new ApiResponse(asList(firstTodo, secondTodo))
                .build(HttpStatus.OK));

        mvc.perform(get("/api/v1/todos"))
                .andExpect(jsonPath("$.message", is("success")))
                .andExpect(jsonPath("$.data[0].task", is("firstTodo")))
                .andExpect(jsonPath("$.data[1].task", is("secondTodo")))
                .andExpect(status().isOk());

        verify(todoService).getAllTodos();
    }

    @Test
    public void shouldReturnEmptyListWhenGetNotExistingTodosSuccessfully() throws Exception {
        when(todoService.getAllTodos()).thenReturn(new ApiResponse(Collections.EMPTY_LIST)
                .build(HttpStatus.OK));

        mvc.perform(get("/api/v1/todos"))
                .andExpect(jsonPath("$.message", is("success")))
                .andExpect(jsonPath("$.data", is(Collections.EMPTY_LIST)))
                .andExpect(status().isOk());

        verify(todoService).getAllTodos();
    }

    @Test
    public void shouldReturnTodoWhenGetExitingTodoByIdSuccessfully() throws Exception {
        Todo todo = new Todo();
        todo.setId("Id");
        todo.setTask("Todo");

        when(todoService.getTodo(anyString())).thenReturn(new ApiResponse(todo).build(HttpStatus.OK));

        mvc.perform(get("/api/v1/todos/id"))
                .andExpect(jsonPath("$.message", is("success")))
                .andExpect(jsonPath("$.data.id", is("Id")))
                .andExpect(jsonPath("$.data.task", is("Todo")))
                .andExpect(status().isOk());

        verify(todoService).getTodo(anyString());
    }

    @Test
    public void shouldReturnNotFoundWhenGetNotExitingTodoById() throws Exception {
        Todo todo = new Todo();
        todo.setId("Id");
        todo.setTask("UpdateTodo");

        when(todoService.getTodo(anyString()))
                .thenReturn(new ApiResponse("not found", null).build(HttpStatus.NOT_FOUND));

        mvc.perform(get("/api/v1/todos/id"))
                .andExpect(jsonPath("$.message", is("not found")))
                .andExpect(jsonPath("$.data", is(nullValue())))
                .andExpect(status().is4xxClientError());

        verify(todoService).getTodo(anyString());

    }

    @Test
    public void shouldReturnUpdatedTodoWhenUpdateExitingTodoSuccessfully() throws Exception {
        Todo todo = new Todo();
        todo.setId("Id");
        todo.setTask("UpdateTodo");

        when(todoService.editTodo(anyString(), any(Todo.class))).thenReturn(new ApiResponse(todo).build(HttpStatus.OK));

        mvc.perform(put("/api/v1/todos/id")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonMapper.toJson(todo).orElse("")))
                .andExpect(jsonPath("$.message", is("success")))
                .andExpect(jsonPath("$.data.id", is("Id")))
                .andExpect(jsonPath("$.data.task", is("UpdateTodo")))
                .andExpect(status().isOk());

        verify(todoService).editTodo(anyString(), any(Todo.class));
    }

    @Test
    public void shouldReturnNotFoundWhenUpdateNotExitingTodo() throws Exception {
        Todo todo = new Todo();
        todo.setId("Id");
        todo.setTask("UpdateTodo");

        when(todoService.editTodo(anyString(), any(Todo.class)))
                .thenReturn(new ApiResponse("not found", null).build(HttpStatus.NOT_FOUND));

        mvc.perform(put("/api/v1/todos/id")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JsonMapper.toJson(todo).orElse("")))
                .andExpect(jsonPath("$.message", is("not found")))
                .andExpect(jsonPath("$.data", is(nullValue())))
                .andExpect(status().is4xxClientError());

        verify(todoService).editTodo(anyString(), any(Todo.class));
    }
}
