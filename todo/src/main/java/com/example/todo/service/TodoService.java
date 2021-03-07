package com.example.todo.service;

import com.example.todo.model.Todo;
import com.example.todo.model.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class TodoService {
    @Autowired
    private TodoRepository repository;

    public Iterable<Todo> findAll() {
        return this.repository.findAll();
    }

    public Optional<Todo> findById(Integer id) {
        if(id == 1) {
            Todo todo = new Todo();
            todo.setId(1);
            todo.setTitle("Todo 1");
            todo.setContent("Todo 1 Content");
            todo.setArchived(false);
            todo.setCompleted(false);
            todo.setCreatedDateTime(new Date());
            todo.setUpdatedDateTime(new Date());
            return Optional.of(todo);
        } else {
            return this.repository.findById(id);
        }
//        return this.repository.findById(id);
    }
}
