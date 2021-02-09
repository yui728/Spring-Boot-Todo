package com.example.todo;

import org.springframework.data.repository.CrudRepository;
import com.example.todo.Todo;

public interface TodoRepository extends CrudRepository<Todo, Integer> {
}