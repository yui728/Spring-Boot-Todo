package com.example.todo;

import com.example.todo.contoller.TodoEditForm;
import com.example.todo.contoller.TodoForm;
import com.example.todo.model.Todo;
import com.example.todo.service.TodoService;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
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

    @Test
    public void postEditPageCompleteUpdateTodoTest() throws Exception {
        String title = "Updated Test Title";
        String content = "Updated Test content";
        MockHttpServletRequestBuilder  request =  post("/todo/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", String.valueOf(1))
                .param("title", title)
                .param("content", content)
                .param("completed", String.valueOf(false))
                .param("archived", String.valueOf(false));

        when(service.existTodo(1)).thenReturn(true);

        this.mockMvc.perform(request)
                .andExpect(redirectedUrl("/todo/"));

        ArgumentCaptor<Integer> idArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<TodoEditForm> formArgumentCaptor = ArgumentCaptor.forClass(TodoEditForm.class);
        // メソッド呼出回数の確認と引数のキャプチャ
        verify(service, times(1)).existTodo(idArgumentCaptor.capture());
        verify(service, times(1)).update(formArgumentCaptor.capture());
        // 呼び出し時の引数の確認
        assertEquals(1, idArgumentCaptor.getValue());
        TodoEditForm captoredFormValue = formArgumentCaptor.getValue();
        assertEquals(1, captoredFormValue.getId());
        assertEquals(title, captoredFormValue.getTitle());
        assertEquals(content, captoredFormValue.getContent());
        assertEquals(false, captoredFormValue.getArchived());
        assertEquals(false, captoredFormValue.getCompleted());

    }

    @Test
    public void postEditPageNotExistTodoTest() throws Exception {
        String title = "Updated Test Title";
        String content = "Updated Test content";
        MockHttpServletRequestBuilder  request =  post("/todo/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", String.valueOf(1))
                .param("title", title)
                .param("content", content)
                .param("completed", String.valueOf(false))
                .param("archived", String.valueOf(false));

        when(service.existTodo(1)).thenReturn(false);

        this.mockMvc.perform(request)
                .andExpect(view().name("edit"))
                .andExpect(model().attribute("message", "更新対象のTodoが存在しません。"));

        ArgumentCaptor<Integer> idArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        // メソッド呼出回数の確認と引数のキャプチャ
        verify(service, times(1)).existTodo(idArgumentCaptor.capture());
        verify(service, never()).update(any());
        // 実行時の引数チェック
        assertEquals(1, idArgumentCaptor.getValue());
    }

    @Test
    public void postEditPageNotExistTodoOnUpdateTest() throws Exception {
        String title = "Updated Test Title";
        String content = "Updated Test content";
        MockHttpServletRequestBuilder  request =  post("/todo/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", String.valueOf(1))
                .param("title", title)
                .param("content", content)
                .param("completed", String.valueOf(false))
                .param("archived", String.valueOf(false));

        when(service.existTodo(1)).thenReturn(true);
        when(service.update(any())).thenThrow(NotFoundException.class);

        this.mockMvc.perform(request)
                .andExpect(view().name("edit"))
                .andExpect(model().attribute("message", "更新対象のTodoが存在しません。"));

        ArgumentCaptor<Integer> idArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<TodoEditForm> editFormArgumentCaptor = ArgumentCaptor.forClass(TodoEditForm.class);
        // メソッド呼出回数の確認と引数のキャプチャ
        verify(service, times(1)).existTodo(idArgumentCaptor.capture());
        verify(service, times(1)).update(editFormArgumentCaptor.capture());
        // 実行時の引数チェック
        assertEquals(1, idArgumentCaptor.getValue());
        TodoEditForm captoredTodoEditForm = editFormArgumentCaptor.getValue();
        assertEquals(1, captoredTodoEditForm.getId());
        assertEquals(title, captoredTodoEditForm.getTitle());
        assertEquals(content, captoredTodoEditForm.getContent());
        assertEquals(false, captoredTodoEditForm.getCompleted());
        assertEquals(false, captoredTodoEditForm.getArchived());
    }

    @Test
    public void postEditPageTitleValidationOverLengthErrorTest() throws Exception {
        String title = TestUtility.repeatString("A", OVERSIZE_TITLE_LENGTH, "Updated Test Title");
        String content = "Updated Test content";
        MockHttpServletRequestBuilder  request =  post("/todo/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", String.valueOf(1))
                .param("title", title)
                .param("content", content)
                .param("completed", String.valueOf(false))
                .param("archived", String.valueOf(false));

        this.mockMvc.perform(request)
                .andExpect(view().name("edit"))
                .andExpect(model().attributeHasErrors("todoForm"))
                .andExpect(model().attributeHasFieldErrors("todoForm", "title"))
                .andExpect(model().attribute("todoForm", hasProperty("id", is(1))))
                .andExpect(model().attribute("todoForm", hasProperty("title", is(title))))
                .andExpect(model().attribute("todoForm", hasProperty("content", is(content))))
                .andExpect(model().attribute("todoForm", hasProperty("completed", is(false))))
                .andExpect(model().attribute("todoForm", hasProperty("archived", is(false))));

        // メソッド呼出回数の確認
        verify(service, never()).existTodo(anyInt());
        verify(service, never()).update(any());
    }

    @Test
    public void postEditPageTitleValidationNoInputErrorTest() throws Exception {
        String title = "";
        String content = "Updated Test content";
        MockHttpServletRequestBuilder  request =  post("/todo/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", String.valueOf(1))
                .param("title", title)
                .param("content", content)
                .param("completed", String.valueOf(false))
                .param("archived", String.valueOf(false));

        this.mockMvc.perform(request)
                .andExpect(view().name("edit"))
                .andExpect(model().attributeHasErrors("todoForm"))
                .andExpect(model().attributeHasFieldErrors("todoForm", "title"))
                .andExpect(model().attribute("todoForm", hasProperty("id", is(1))))
                .andExpect(model().attribute("todoForm", hasProperty("title", is(title))))
                .andExpect(model().attribute("todoForm", hasProperty("content", is(content))))
                .andExpect(model().attribute("todoForm", hasProperty("completed", is(false))))
                .andExpect(model().attribute("todoForm", hasProperty("archived", is(false))));

        // メソッド呼出回数の確認
        verify(service, never()).existTodo(anyInt());
        verify(service, never()).update(any());
    }

    @Test
    public void postEditPageContentValidationOverLengthErrorTest() throws Exception {
        String title = "Updated Test title";
        String content = TestUtility.repeatString("A", OVERSIZE_CONTENT_LENGTH, "Updated Test content ");
        MockHttpServletRequestBuilder  request =  post("/todo/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", String.valueOf(1))
                .param("title", title)
                .param("content", content)
                .param("completed", String.valueOf(false))
                .param("archived", String.valueOf(false));

        this.mockMvc.perform(request)
                .andExpect(view().name("edit"))
                .andExpect(model().attributeHasErrors("todoForm"))
                .andExpect(model().attributeHasFieldErrors("todoForm", "content"))
                .andExpect(model().attribute("todoForm", hasProperty("id", is(1))))
                .andExpect(model().attribute("todoForm", hasProperty("title", is(title))))
                .andExpect(model().attribute("todoForm", hasProperty("content", is(content))))
                .andExpect(model().attribute("todoForm", hasProperty("completed", is(false))))
                .andExpect(model().attribute("todoForm", hasProperty("archived", is(false))));

        // メソッド呼出回数の確認
        verify(service, never()).existTodo(anyInt());
        verify(service, never()).update(any());
    }

    @Test
    public void postEditPageContentValidationNoInputErrorTest() throws Exception {
        String title = "Updated Test title";
        String content = "";
        MockHttpServletRequestBuilder  request =  post("/todo/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", String.valueOf(1))
                .param("title", title)
                .param("content", content)
                .param("completed", String.valueOf(false))
                .param("archived", String.valueOf(false));

        this.mockMvc.perform(request)
                .andExpect(view().name("edit"))
                .andExpect(model().attributeHasErrors("todoForm"))
                .andExpect(model().attributeHasFieldErrors("todoForm", "content"))
                .andExpect(model().attribute("todoForm", hasProperty("id", is(1))))
                .andExpect(model().attribute("todoForm", hasProperty("title", is(title))))
                .andExpect(model().attribute("todoForm", hasProperty("content", is(content))))
                .andExpect(model().attribute("todoForm", hasProperty("completed", is(false))))
                .andExpect(model().attribute("todoForm", hasProperty("archived", is(false))));

        // メソッド呼出回数の確認
        verify(service, never()).existTodo(anyInt());
        verify(service, never()).update(any());
    }

    @Test
    public void postEditPageCompletedNotBooleanErrorTest() throws Exception {
        String title = "Updated Test title";
        String content = "Updated Test Content";
        MockHttpServletRequestBuilder  request =  post("/todo/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", String.valueOf(1))
                .param("title", title)
                .param("content", content)
                .param("completed", "AAA")
                .param("archived", String.valueOf(false));

        this.mockMvc.perform(request)
                .andExpect(view().name("edit"))
                .andExpect(model().attributeHasErrors("todoForm"))
                .andExpect(model().attributeHasFieldErrors("todoForm", "completed"))
                .andExpect(model().attribute("todoForm", hasProperty("id", is(1))))
                .andExpect(model().attribute("todoForm", hasProperty("title", is(title))))
                .andExpect(model().attribute("todoForm", hasProperty("content", is(content))))
                .andExpect(model().attribute("todoForm", hasProperty("completed", nullValue())))
                .andExpect(model().attribute("todoForm", hasProperty("archived", is(false))));

        // メソッド呼出回数の確認
        verify(service, never()).existTodo(anyInt());
        verify(service, never()).update(any());
    }

    @Test
    public void postEditPageCompletedNoInputErrorTest() throws Exception {
        String title = "Updated Test title";
        String content = "Updated Test Content";
        MockHttpServletRequestBuilder  request =  post("/todo/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", String.valueOf(1))
                .param("title", title)
                .param("content", content)
                .param("completed", "")
                .param("archived", String.valueOf(false));

        this.mockMvc.perform(request)
                .andExpect(view().name("edit"))
                .andExpect(model().attributeHasErrors("todoForm"))
                .andExpect(model().attributeHasFieldErrors("todoForm", "completed"))
                .andExpect(model().attribute("todoForm", hasProperty("id", is(1))))
                .andExpect(model().attribute("todoForm", hasProperty("title", is(title))))
                .andExpect(model().attribute("todoForm", hasProperty("content", is(content))))
                .andExpect(model().attribute("todoForm", hasProperty("completed", nullValue())))
                .andExpect(model().attribute("todoForm", hasProperty("archived", is(false))));

        // メソッド呼出回数の確認
        verify(service, never()).existTodo(anyInt());
        verify(service, never()).update(any());
    }

    @Test
    public void postEditPageCompletedTrueTest() throws Exception {
        String title = "Updated Test title";
        String content = "Updated Test Content";
        MockHttpServletRequestBuilder  request =  post("/todo/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", String.valueOf(1))
                .param("title", title)
                .param("content", content)
                .param("completed", String.valueOf(true))
                .param("archived", String.valueOf(false));

        when(service.existTodo(1)).thenReturn(true);

        this.mockMvc.perform(request)
                .andExpect(redirectedUrl("/todo/"));

        ArgumentCaptor<Integer> idArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<TodoEditForm> editFormArgumentCaptor = ArgumentCaptor.forClass(TodoEditForm.class);
        // メソッド呼出回数の確認と引数のキャプチャ
        verify(service, times(1)).existTodo(idArgumentCaptor.capture());
        verify(service, times(1)).update(editFormArgumentCaptor.capture());
        // 実行時の引数チェック
        assertEquals(1, idArgumentCaptor.getValue());
        TodoEditForm captoredTodoEditForm = editFormArgumentCaptor.getValue();
        assertEquals(1, captoredTodoEditForm.getId());
        assertEquals(title, captoredTodoEditForm.getTitle());
        assertEquals(content, captoredTodoEditForm.getContent());
        assertEquals(true, captoredTodoEditForm.getCompleted());
        assertEquals(false, captoredTodoEditForm.getArchived());
    }

    @Test
    public void postEditPageArchivedNotBooleanErrorTest() throws Exception {
        String title = "Updated Test title";
        String content = "Updated Test Content";
        MockHttpServletRequestBuilder  request =  post("/todo/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", String.valueOf(1))
                .param("title", title)
                .param("content", content)
                .param("completed", String.valueOf(false))
                .param("archived", "BBB");

        this.mockMvc.perform(request)
                .andExpect(view().name("edit"))
                .andExpect(model().attributeHasErrors("todoForm"))
                .andExpect(model().attributeHasFieldErrors("todoForm", "archived"))
                .andExpect(model().attribute("todoForm", hasProperty("id", is(1))))
                .andExpect(model().attribute("todoForm", hasProperty("title", is(title))))
                .andExpect(model().attribute("todoForm", hasProperty("content", is(content))))
                .andExpect(model().attribute("todoForm", hasProperty("completed", is(false))))
                .andExpect(model().attribute("todoForm", hasProperty("archived", nullValue())));

        // メソッド呼出回数の確認
        verify(service, never()).existTodo(anyInt());
        verify(service, never()).update(any());
    }

    @Test
    public void postEditPageArchivedNoInputErrorTest() throws Exception {
        String title = "Updated Test title";
        String content = "Updated Test Content";
        MockHttpServletRequestBuilder  request =  post("/todo/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", String.valueOf(1))
                .param("title", title)
                .param("content", content)
                .param("completed", String.valueOf(false))
                .param("archived", "");

        this.mockMvc.perform(request)
                .andExpect(view().name("edit"))
                .andExpect(model().attributeHasErrors("todoForm"))
                .andExpect(model().attributeHasFieldErrors("todoForm", "archived"))
                .andExpect(model().attribute("todoForm", hasProperty("id", is(1))))
                .andExpect(model().attribute("todoForm", hasProperty("title", is(title))))
                .andExpect(model().attribute("todoForm", hasProperty("content", is(content))))
                .andExpect(model().attribute("todoForm", hasProperty("completed", is(false))))
                .andExpect(model().attribute("todoForm", hasProperty("archived", nullValue())));

        // メソッド呼出回数の確認
        verify(service, never()).existTodo(anyInt());
        verify(service, never()).update(any());
    }

    @Test
    public void postEditPageArchivedTrueTest() throws Exception {
        String title = "Updated Test title";
        String content = "Updated Test Content";
        MockHttpServletRequestBuilder  request =  post("/todo/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", String.valueOf(1))
                .param("title", title)
                .param("content", content)
                .param("completed", String.valueOf(false))
                .param("archived", String.valueOf(true));

        when(service.existTodo(1)).thenReturn(true);

        this.mockMvc.perform(request)
                .andExpect(redirectedUrl("/todo/"));

        ArgumentCaptor<Integer> idArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<TodoEditForm> editFormArgumentCaptor = ArgumentCaptor.forClass(TodoEditForm.class);
        // メソッド呼出回数の確認と引数のキャプチャ
        verify(service, times(1)).existTodo(idArgumentCaptor.capture());
        verify(service, times(1)).update(editFormArgumentCaptor.capture());
        // 実行時の引数チェック
        assertEquals(1, idArgumentCaptor.getValue());
        TodoEditForm captoredTodoEditForm = editFormArgumentCaptor.getValue();
        assertEquals(1, captoredTodoEditForm.getId());
        assertEquals(title, captoredTodoEditForm.getTitle());
        assertEquals(content, captoredTodoEditForm.getContent());
        assertEquals(false, captoredTodoEditForm.getCompleted());
        assertEquals(true, captoredTodoEditForm.getArchived());
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
