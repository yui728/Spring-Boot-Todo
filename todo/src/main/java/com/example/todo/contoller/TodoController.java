package com.example.todo.contoller;

import com.example.todo.NotFoundException;
import com.example.todo.model.Todo;
import com.example.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/todo")
public class TodoController {
    @Autowired
    private TodoService todoService;

    @Value("${com.example.todo.settingType}")
    private String settingType;

    @GetMapping("/")
    public String top(Model model) {
        System.out.println("Todo App Setting Type = " + settingType);
        System.out.println("Start top Page.");
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
        todoForm.setCreatedDateTime(todo.getCreatedDateTime());
        todoForm.setUpdatedDateTime(todo.getUpdatedDateTime());

        System.out.println(todoForm.toString());

        model.addAttribute("todoForm", todoForm);

        return "edit";
    }

    @PostMapping("/edit")
    public String updateTodoProcess(@Valid @ModelAttribute("todoForm") TodoEditForm todoForm, BindingResult bindingResult, Model model) {
         if(bindingResult.hasErrors()) {
             System.out.println("Model has todoForm? " + model.containsAttribute("todoFrom"));
             return "edit";
         }
         try {
             boolean isExist = todoService.existTodo(todoForm.getId());
             if(!isExist) {
                 model.addAttribute("message", "更新対象のTodoが存在しません。");
                 return "edit";
             }
             todoService.update(todoForm);
         } catch(NotFoundException ex) {
             model.addAttribute("message", "更新対象のTodoが存在しません。");
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
          System.out.println("registration Validation Error: " + bindingResult.getFieldError());
          return "register";
        }
        todoService.registration(todoFrom);
        return "redirect:/todo/";
    }

    @PostMapping("/archive/{id}/")
    public String archiveTodoProcess(@PathVariable Integer id, Model model) {
        todoService.archiveTodo(id);

        return "redirect:/todo/";
    }

    @PostMapping("/unarchive/{id}/")
    public String unarchiveTodoProcess(@PathVariable Integer id, Model model) {
        todoService.unarchiveTodo(id);

        return "redirect:/todo/";
    }

    @PostMapping("/delete/{id}/")
    public String deleteTodoProcess(@PathVariable Integer id, Model model) {
        todoService.deleteTodo(id);

        return "redirect:/todo/";
    }
}