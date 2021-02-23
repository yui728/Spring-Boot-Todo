package com.example.todo;

import com.example.todo.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;

@Controller
@RequestMapping("/todo")
public class TodoController {
    @Autowired
    private TodoRepository todoRepository;

    @GetMapping("/")
    public String top(Model model) {
        model.addAttribute("title", "Todo List");
        model.addAttribute("message", "ここはTodoの一覧ページです");
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
        model.addAttribute("todoList", todoList);

        return "top";
    }
}