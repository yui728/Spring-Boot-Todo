package com.example.todo.service;

import com.example.todo.model.Todo;
import com.example.todo.model.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TodoService {
    @Autowired
    private TodoRepository repository;

    public Iterable<Todo> findAll() {
        return this.repository.findAll();
    }

    public Optional<Todo> findById(Integer id) {
        return this.repository.findById(id);
    }
}
