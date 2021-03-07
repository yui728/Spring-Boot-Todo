package com.example.todo.contoller;

import com.example.todo.model.TodoRepository;
import com.example.todo.model.Todo;
import com.example.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("/todo")
public class TodoController {
    @Autowired
    private TodoService todoService;

    @GetMapping("/")
    public String top(Model model) {
        System.out.println("Start top Page.");
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

    @GetMapping("/edit")
    public String editTodo(@RequestParam(value = "id", defaultValue = "-1") Integer id, Model model) {
        Optional<Todo> optionalTodo = todoService.findById(id);

        System.out.println("isPresent? = " + optionalTodo.isPresent());
        // 存在しないTodoの場合はトップページに遷移
        if(!optionalTodo.isPresent()) {
            System.out.println("Go top page.");
            return "redirect:/todo/";
        }

        Todo todo = optionalTodo.get();

        model.addAttribute("todo", todo);

        return "edit";
    }

    @PostMapping("/edit")
    public String updateTodoProcess() {
        return "editTodo";
    }

    @GetMapping("/new")
    public String registTodo() {
        return "registTodo";
    }
}