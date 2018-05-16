package com.bot.wattanachaitodolist.repository;

import com.bot.wattanachaitodolist.domain.Todo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TodoRepository extends CrudRepository<Todo, String> {
    public List<Todo> findAllByOrderByUpdatedDateAsc();
}
