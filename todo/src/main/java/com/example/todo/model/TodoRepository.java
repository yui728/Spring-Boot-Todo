package com.example.todo.model;

import org.springframework.data.repository.CrudRepository;
import com.example.todo.model.Todo;

public interface TodoRepository extends CrudRepository<Todo, Integer> {
    Iterable<Todo> findAllByOrderByCreatedDateTimeDesc();
}