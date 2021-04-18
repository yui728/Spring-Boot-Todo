package com.example.todo.service;

import com.example.todo.contoller.TodoForm;
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
        return this.repository.findAllByOrderByCreatedDateTimeDesc();
    }

    public Optional<Todo> findById(Integer id) {
        return this.repository.findById(id);
    }

    public Optional<Todo> registration(TodoForm form) {
        Todo todo = new Todo();
        todo.setTitle(form.getTitle());
        todo.setContent(form.getContent());
        todo.setArchived(false);
        todo.setCompleted(false);
        todo.setCreatedDateTime(new Date());
        todo.setUpdatedDateTime(new Date());
        Optional<Todo> result = Optional.of(repository.save(todo));

        return result;
    }
}
