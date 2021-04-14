package com.example.todo.contoller;

import com.example.todo.model.TodoRepository;
import com.example.todo.model.Todo;
import com.example.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        Iterable<Todo> todoList = todoService.findAll();
        model.addAttribute("todoList", todoList);

        return "top";
    }

    @GetMapping("/edit/{id}")
    public String editTodo(@PathVariable Integer id, Model model) {
        Optional<Todo> optionalTodo = todoService.findById(id);

        System.out.println("isPresent? = " + optionalTodo.isPresent());
        // 存在しないTodoの場合はトップページに遷移
        if(!optionalTodo.isPresent()) {
            System.out.println("Go top page.");
            return "redirect:/todo/";
        }

        Todo todo = optionalTodo.get();
        TodoEditForm todoForm = new TodoEditForm();

        todoForm.setTitle(todo.getTitle());
        todoForm.setContent(todo.getContent());
        todoForm.setArchived(todo.getArchived());
        todoForm.setCompleted(todo.getCompleted());
        todoForm.setId(todo.getId());

        System.out.println(todoForm.toString());

        model.addAttribute("todoForm", todoForm);

        return "edit";
    }

    @PostMapping("/edit")
    public String updateTodoProcess(@Valid @ModelAttribute("todoForm") TodoEditForm todoForm, BindingResult bindingResult, Model model) {
         if(bindingResult.hasErrors()) {
             return "edit";
         }
         return "redirect:/todo/";
    }

    @GetMapping("/new")
    public String registerTodo(TodoForm todoForm) {
        return "register";
    }

    @PostMapping("/new")
    public String registerTodoProcess(@Valid TodoForm todoFrom, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
//          System.out.println("registration Validation Error: " + bindingResult.getFieldError());
          return "register";
        }
        return "redirect:/todo/";
    }
}