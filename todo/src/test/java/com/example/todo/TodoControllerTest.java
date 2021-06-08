package com.example.todo;

import com.example.todo.contoller.TodoForm;
import com.example.todo.model.Todo;
import com.example.todo.service.TodoService;
import jdk.vm.ci.meta.Local;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService service;

    private static int OVERSIZE_TITLE_LENGTH = 51;
    private static int OVERSIZE_CONTENT_LENGTH = 501;

    @Test
    public void topResultDefaultTest() throws Exception {
        Iterable<Todo> todoList = createDummyTodoList(3);
        when(service.findAll()).thenReturn(todoList);
        this.mockMvc.perform(get("/todo/"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(view().name(containsString("top")))
                .andExpect(model().attribute("todoList", todoList));
    }

    @Test
    public void topResultZeroListTest() throws Exception {
        Iterable<Todo> todoList = createDummyTodoList(0);
        when(service.findAll()).thenReturn(todoList);
        this.mockMvc.perform(get("/todo/"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(view().name(containsString("top")))
                .andExpect(model().attribute("todoList", todoList));
    }

    @Test
    public void registerPageShowTest() throws Exception {
        this.mockMvc.perform(get("/todo/new/"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("todoForm", IsInstanceOf.instanceOf(TodoForm.class)));
    }

    @Test
    public void registerPagePostNoParamTest() throws Exception {
        MockHttpServletRequestBuilder  createMessage =  post("/todo/new/");
        this.mockMvc.perform(createMessage)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("todoForm", IsInstanceOf.instanceOf(TodoForm.class)))
                .andExpect(model().attributeHasErrors("todoForm"))
                .andExpect(model().attributeHasFieldErrors("todoForm", "title", "content"));
    }

    @Test
    public void registerPagePostNoTitleTest() throws Exception {
        String title = "";
        String content = "ABC";
        MockHttpServletRequestBuilder  createMessage =  post("/todo/new/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", title)
                .param("content", content);
        this.mockMvc.perform(createMessage)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("todoForm", IsInstanceOf.instanceOf(TodoForm.class)))
                .andExpect(model().attributeHasErrors("todoForm"))
                .andExpect(model().attributeHasFieldErrors("todoForm", "title"))
                .andExpect(model().attribute("todoForm", hasProperty("content", is(content))));
    }

    @Test
    public void registerPagePostNoContentTest() throws Exception {
        String title = "Todo Title";

        String content = "";
        MockHttpServletRequestBuilder  createMessage =  post("/todo/new/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", title)
                .param("content", content);
        this.mockMvc.perform(createMessage)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("todoForm", IsInstanceOf.instanceOf(TodoForm.class)))
                .andExpect(model().attributeHasErrors("todoForm"))
                .andExpect(model().attributeHasFieldErrors("todoForm", "content"))
                .andExpect(model().attribute("todoForm", hasProperty("title", is(title))));
    }

    @Test
    public void registerPagePostOverSizeTitleTest() throws Exception {
        String title = TestUtility.repeatString("A", OVERSIZE_TITLE_LENGTH, "Test Title");
        String content = "Test Content";
        MockHttpServletRequestBuilder  createMessage =  post("/todo/new/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", title)
                .param("content", content);
        this.mockMvc.perform(createMessage)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("todoForm", IsInstanceOf.instanceOf(TodoForm.class)))
                .andExpect(model().attributeHasErrors("todoForm"))
                .andExpect(model().attribute("todoForm", hasProperty("title", is(title))))
                .andExpect(model().attributeHasFieldErrors("todoForm", "title"))
                .andExpect(model().attribute("todoForm", hasProperty("content", is(content))));
    }

    @Test
    public void registerPagePostOverSizeContentTest() throws Exception {
        String title = "Test Title";
        String content = TestUtility.repeatString("B", OVERSIZE_CONTENT_LENGTH, "Test Content");
        MockHttpServletRequestBuilder  createMessage =  post("/todo/new/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", title)
                .param("content", content);
        this.mockMvc.perform(createMessage)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("todoForm", IsInstanceOf.instanceOf(TodoForm.class)))
                .andExpect(model().attributeHasErrors("todoForm"))
                .andExpect(model().attribute("todoForm", hasProperty("title", is(title))))
                .andExpect(model().attributeHasFieldErrors("todoForm", "content"))
                .andExpect(model().attribute("todoForm", hasProperty("content", is(content))));
    }

    @Test
    public void showEditTodoPageTest() throws Exception {
        MockHttpServletRequestBuilder  request =  get("/todo/edit/1/");
        Todo todo = createDummyTodo(1);
        Optional<Todo> serviceResult = Optional.of(todo);

        when(service.findById(1)).thenReturn(serviceResult);
        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attributeExists("todoForm"))
                .andExpect(model().attribute("todoForm", IsInstanceOf.instanceOf(TodoForm.class)))
                .andExpect(model().attributeHasNoErrors("todoForm"))
                .andExpect(model().attribute("todoForm", hasProperty("id", is(todo.getId()))))
                .andExpect(model().attribute("todoForm", hasProperty("title", is(todo.getTitle()))))
                .andExpect(model().attribute("todoForm", hasProperty("content", is(todo.getContent()))))
                .andExpect(model().attribute("todoForm", hasProperty("createdDateTime", is(todo.getCreatedDateTime()))))
                .andExpect(model().attribute("todoForm", hasProperty("updatedDateTime", is(todo.getUpdatedDateTime()))))
                .andExpect(model().attribute("todoForm", hasProperty("completed", is(todo.getCompleted()))))
                .andExpect(model().attribute("todoForm", hasProperty("archived", is(todo.getArchived()))));
    }

    @Test
    public void showEditPageNotFoundTodoTest() throws Exception {
        MockHttpServletRequestBuilder  request =  get("/todo/edit/1/");
        Optional<Todo> serviceResult = Optional.empty();

        when(service.findById(1)).thenReturn(serviceResult);
        this.mockMvc.perform(request)
                .andExpect(redirectedUrl("/todo/"));
    }

    private Todo createDummyTodo(int id) {
        Todo result = new Todo();
        result.setId(id);
        result.setTitle("Todo Title " + id);
        result.setContent("Todo Content " + id);
        LocalDateTime createdDateTime = LocalDateTime.now().minusDays(1);
        result.setCreatedDateTime(Date.from(createdDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        LocalDateTime updatedDateTime = LocalDateTime.now().minusHours(1);
        result.setUpdatedDateTime(Date.from(updatedDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        result.setCompleted(false);
        result.setArchived(false);
        return  result;
    }

    private Iterable<Todo> createDummyTodoList(int itemQuantity) {
        List<Todo> result = new ArrayList<>();

        for(int i = 0; i < itemQuantity; i++) {
            Todo item = new Todo();
            int num = i + 1;
            item.setId(num);
            item.setTitle("Todo " + num);
            item.setContent("Todo Content No." + num);
            item.setCompleted(false);
            item.setArchived(false);
            item.setCreatedDateTime(new Date());
            item.setUpdatedDateTime(new Date());
            result.add(item);
        }

        return result;
    }
}
