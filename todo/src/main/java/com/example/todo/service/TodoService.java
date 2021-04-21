package com.example.todo.service;

import com.example.todo.NotFoundException;
import com.example.todo.contoller.TodoEditForm;
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

    public Todo registration(TodoForm form) {
        Todo todo = new Todo();
        todo.setTitle(form.getTitle());
        todo.setContent(form.getContent());
        todo.setArchived(false);
        todo.setCompleted(false);
        todo.setCreatedDateTime(new Date());
        todo.setUpdatedDateTime(new Date());

        return repository.save(todo);
    }

    public Todo update(TodoEditForm form) throws NotFoundException {
        Optional<Todo> todoData = repository.findById(form.getId());

        if(!todoData.isPresent()) {
            throw new NotFoundException();
        }

        Todo updateData = todoData.get();

        updateData.setTitle(form.getTitle());
        updateData.setContent(form.getContent());
        updateData.setArchived(form.getArchived());
        updateData.setCompleted(form.getCompleted());
        updateData.setUpdatedDateTime(new Date());

        return repository.save(updateData);
    }

    public boolean existTodo(Integer id) {
        Optional<Todo> existData = repository.findById(id);

        return existData.isPresent();
    }
}
