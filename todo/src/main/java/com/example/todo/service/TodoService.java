package com.example.todo.service;

import com.example.todo.model.Todo;
import com.example.todo.model.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
public class TodoService {
    @Autowired
    private TodoRepository repository;

    public Iterable<Todo> findAll() {
        ArrayList<Todo> todoList = new ArrayList<>();
        int todoCount = 3;
        for(int i = 0; i < todoCount; i++) {
            Todo todo = new Todo();
            todo.setId(i + 1);
            todo.setTitle("Todo " + (i + 1));
            todo.setContent("Todo " + (i + 1) + " content");
            todo.setArchived(false);
            todo.setCompleted(false);

            ZoneId zoneId = ZoneId.systemDefault();
            LocalDateTime createdDatetimeBase = LocalDateTime.now().minusDays((i + 2));
            ZonedDateTime createdDatetimeZoned = ZonedDateTime.of(createdDatetimeBase, zoneId);
            Instant createdDatetimeInstant = createdDatetimeZoned.toInstant();
            Date createdDatetime = Date.from(createdDatetimeInstant);

            LocalDateTime updatedDatetimeBase = LocalDateTime.now().minusMinutes(30 + (i * 10));
            ZonedDateTime updatedDatetimeZoned = ZonedDateTime.of(updatedDatetimeBase, zoneId);
            Instant updatedDatetimeInstant = updatedDatetimeZoned.toInstant();
            Date updatedDatetime = Date.from(updatedDatetimeInstant);

            todo.setCreatedDateTime(createdDatetime);
            todo.setUpdatedDateTime(updatedDatetime);
            todoList.add(todo);
        }

        return todoList;
        // return this.repository.findAll();
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
