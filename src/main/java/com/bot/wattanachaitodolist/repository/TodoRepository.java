package com.bot.wattanachaitodolist.repository;

import com.bot.wattanachaitodolist.domain.Todo;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends Repository<Todo, String> {
    Optional<Todo> findOne(String id);

    Todo save(Todo todo);

    Optional<List<Todo>> save(List<Todo> todoList);
}
