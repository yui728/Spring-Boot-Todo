package com.example.todo;

import com.example.todo.contoller.TodoForm;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoRepository;
import com.example.todo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TodoServiceTests {
    @MockBean
    private TodoRepository repository;

    @Autowired
    private TodoService service;

    @Test
    public void registrationTest() throws Exception {
        TodoForm todoForm = new TodoForm();
        todoForm.setTitle("Add Todo Title");
        todoForm.setContent("Add Todo Content");

        LocalDateTime localDateTime = LocalDateTime.of(2021, 7, 8, 17, 0, 30);
        Date expectDate = Date.from(LocalDateTime.of(2021, 7, 8, 17, 0, 30).atZone(ZoneId.systemDefault()).toInstant());

        try(MockedStatic mockedLocalDateTime =  mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(localDateTime);
            Todo resultTodo = new Todo();
            resultTodo.setId(1);
            resultTodo.setTitle("Add Todo Title");
            resultTodo.setContent("Add Todo Content");
            resultTodo.setArchived(false);
            resultTodo.setCompleted(false);
            resultTodo.setCreatedDateTime(expectDate);
            resultTodo.setUpdatedDateTime(expectDate);
            when(repository.save(any(Todo.class))).thenReturn(resultTodo);

            service.registration(todoForm);

            ArgumentCaptor<Todo> todoArgumentCaptor = ArgumentCaptor.forClass(Todo.class);
            verify(repository, times(1)).save(todoArgumentCaptor.capture());
            mockedLocalDateTime.verify(LocalDateTime::now);
            Todo captoredTodo = todoArgumentCaptor.getValue();
            assertEquals(expectDate,captoredTodo.getCreatedDateTime());
            assertEquals(expectDate, captoredTodo.getUpdatedDateTime());
            assertEquals("Add Todo Title", captoredTodo.getTitle());
            assertEquals("Add Todo Content", captoredTodo.getContent());
            assertFalse(captoredTodo.getArchived());
            assertFalse(captoredTodo.getCompleted());
        }
    }

}
