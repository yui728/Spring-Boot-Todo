package com.example.todo;

import com.example.todo.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/todo")
public class TodoController {
    @Autowired
    private TodoRepository todoRepository;

    @GetMapping("/")
    public String top(Model model) {
        // model.addAttribute("title", "Todo List");
        // model.addAttribute("message", "ここはTodoの一覧ページです");
        return "top";
    }
}