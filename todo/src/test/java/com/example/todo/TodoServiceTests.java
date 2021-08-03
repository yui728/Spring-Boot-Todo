package com.example.todo;

import com.example.todo.contoller.TodoEditForm;
import com.example.todo.contoller.TodoForm;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoRepository;
import com.example.todo.service.TodoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TodoServiceTests {
    @MockBean
    private TodoRepository repository;

    @Autowired
    private TodoService service;

    @Test
    @DisplayName("Todoのregistration処理が成功する場合")
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

    @Test
    @DisplayName("Todoのupdate処理が成功する場合")
    public void updateSuccessTest() throws Exception {
        // updateメソッドのパラメータ
        TodoEditForm updateForm = new TodoEditForm();
        updateForm.setTitle("updated Todo Title");
        updateForm.setContent("updated Todo Content");
        updateForm.setArchived(true);
        updateForm.setCompleted(true);
        updateForm.setId(1);

        // findByIdメソッドの戻り値
        Todo presentTodo = new Todo();
        presentTodo.setId(1);
        presentTodo.setTitle("Todo Title");
        presentTodo.setContent("Todo Content");
        presentTodo.setArchived(false);
        presentTodo.setCompleted(false);
        presentTodo.setCreatedDateTime(Date.from(LocalDateTime.of(2021, 1, 1, 12, 30, 0).atZone(ZoneId.systemDefault()).toInstant()));
        presentTodo.setUpdatedDateTime(Date.from(LocalDateTime.of(2021, 1, 2, 11, 25, 0).atZone(ZoneId.systemDefault()).toInstant()));

        // saveメソッドの戻り値
        Todo resultTodo = new Todo();
        resultTodo.setId(1);
        resultTodo.setTitle("updated Todo Title");
        resultTodo.setContent("updated Todo Content");
        resultTodo.setArchived(true);
        resultTodo.setCompleted(true);
        resultTodo.setCreatedDateTime(
                Date.from(
                        LocalDateTime.of(
                                2021,
                                1,
                                1,
                                12,
                                30,
                                0)
                                .atZone(
                                        ZoneId.systemDefault())
                                .toInstant())
        );
        resultTodo.setUpdatedDateTime(
                Date.from(
                        LocalDateTime.of(
                                2021,
                                7,
                                20,
                                6,
                                30,
                                40)
                                .atZone(
                                        ZoneId.systemDefault())
                                .toInstant())
        );

        LocalDateTime localDateTime = LocalDateTime.of(2021, 7, 20, 6, 30, 40);
        Date expectDate = Date.from(LocalDateTime.of(2021, 7, 20, 6, 30, 40).atZone(ZoneId.systemDefault()).toInstant());

        // LocalDateTimeをMockする
        try(MockedStatic localDateTimeMockedStatic = mockStatic(LocalDateTime.class)) {
            localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(localDateTime);

            // repositoryのメソッドをMock
            when(repository.findById(1)).thenReturn(Optional.of(presentTodo));
            when(repository.save(any(Todo.class))).thenReturn(resultTodo);

            // テストしたいメソッドの実行
            Todo updatedTodo = service.update(updateForm);

            // findByIdメソッドとsaveメソッドの引数をキャプチャ
            ArgumentCaptor<Todo> todoArgumentCaptor = ArgumentCaptor.forClass(Todo.class);
            ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(repository, times(1)).findById(integerArgumentCaptor.capture());
            verify(repository, times(1)).save(todoArgumentCaptor.capture());

            // findByIdメソッドの引数のチェック
            assertEquals(1, integerArgumentCaptor.getValue());

            // saveメソッドの引数をチェック
            Todo todoArgument = todoArgumentCaptor.getValue();
            assertEquals(1, todoArgument.getId());
            assertEquals("updated Todo Title", todoArgument.getTitle());
            assertEquals("updated Todo Content", todoArgument.getContent());
            assertEquals(true, todoArgument.getArchived());
            assertEquals(true, todoArgument.getCompleted());
            assertEquals(expectDate, todoArgument.getUpdatedDateTime());
            assertEquals(presentTodo.getCreatedDateTime(), todoArgument.getCreatedDateTime());

            // updateメソッドの戻り値をチェック
            assertEquals(1, updatedTodo.getId());
            assertEquals("updated Todo Title", updatedTodo.getTitle());
            assertEquals("updated Todo Content", updatedTodo.getContent());
            assertEquals(true, updatedTodo.getArchived());
            assertEquals(true, updatedTodo.getCompleted());
            assertEquals(presentTodo.getCreatedDateTime(), updatedTodo.getCreatedDateTime());
            assertEquals(expectDate, updatedTodo.getUpdatedDateTime());
        }
    }

    @Test
    @DisplayName("Todoのupdate処理時に該当するTodoが存在しない場合")
    public void updateTodoNotFoundTest() throws Exception {
        // updateメソッドのパラメータ
        TodoEditForm updateForm = new TodoEditForm();
        updateForm.setTitle("updated Todo Title");
        updateForm.setContent("updated Todo Content");
        updateForm.setArchived(true);
        updateForm.setCompleted(true);
        updateForm.setId(1);

        // repositoryのメソッドをMock
        when(repository.findById(1)).thenReturn(Optional.empty());

        // テストしたいメソッドの実行
        assertThrows(NotFoundException.class, () -> service.update(updateForm));

        // 呼び出し回数の検知
        verify(repository, times(1)).findById(1);
        verify(repository, never()).save(any(Todo.class));
        // findByIdメソッドの引数をキャプチャ
        ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(repository, times(1)).findById(integerArgumentCaptor.capture());

        // findByIdメソッドの引数のチェック
        assertEquals(1, integerArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("Todoの存在チェックでTodoが存在する場合")
    public void existTodoReturnTrueTest() throws Exception {
        // findByIdでTodoが存在するようにする
        when(repository.findById(anyInt())).thenReturn(Optional.of(new Todo()));

        // existTodoはtrueが戻る
        assertTrue(service.existTodo(1));

        // findByIdの呼び出しを検知する
        verify(repository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Todoの存在チェックでTodoが存在しない場合")
    public void existTodoReturnFalseTest() throws Exception {
        // findByIdでTodoが存在しないようにする
        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        // existTodoはfalseが戻る
        assertFalse(service.existTodo(1));

        // findByIdの呼び出しを検知する
        verify(repository, times(1)).findById(1);
    }

    @Test
    @DisplayName("アーカイブ処理が成功する場合")
    public void archivedTodoCompleteTest() throws Exception {
        Todo presentTodo = new Todo();
        presentTodo.setId(1);
        presentTodo.setTitle("Todo Title");
        presentTodo.setContent("Todo Content");
        presentTodo.setArchived(false);
        presentTodo.setCompleted(false);
        presentTodo.setCreatedDateTime(
                Date.from(
                        LocalDateTime.of(
                                        2021,
                                        1,
                                        1,
                                        12,
                                        30,
                                        0)
                                .atZone(
                                        ZoneId.systemDefault())
                                .toInstant())
        );
        presentTodo.setUpdatedDateTime(
                Date.from(
                        LocalDateTime.of(
                                        2021,
                                        1,
                                        2,
                                        11,
                                        25,
                                        30)
                                .atZone(
                                        ZoneId.systemDefault())
                                .toInstant())
        );
        // findByIdでTodoが存在するようにする
        when(repository.findById(1)).thenReturn(Optional.of(presentTodo));

        // モックしたときの戻り値用のLocalDateTime、判定用のDateオブジェクトをあらかじめ作っておく
        LocalDateTime localDateTime = LocalDateTime.of(2021, 7, 20, 6, 30, 40);
        Date expectDate = Date.from(LocalDateTime.of(2021, 7, 20, 6, 30, 40).atZone(ZoneId.systemDefault()).toInstant());

        // 更新日時のモックのため、LocalDateTimeをインラインモックする
        try(MockedStatic<LocalDateTime> localDateTimeMockedStatic = mockStatic(LocalDateTime.class)) {
            // LocalDateTime.nowの戻り値を設定する
            localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(localDateTime);

            // repositoryのメソッドをMockする
            when(repository.save(any(Todo.class))).thenReturn(new Todo());

            service.archiveTodo(1);

            // 呼び出し回数と実行時引数のチェック
            ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(repository, times(1)).findById(integerArgumentCaptor.capture());
            assertEquals(1, integerArgumentCaptor.getValue());
            ArgumentCaptor<Todo> todoArgumentCaptor = ArgumentCaptor.forClass(Todo.class);
            verify(repository, times(1)).save(todoArgumentCaptor.capture());
            Todo catptoredTodo = todoArgumentCaptor.getValue();
            assertEquals(1, catptoredTodo.getId());
            assertEquals("Todo Title", catptoredTodo.getTitle());
            assertEquals("Todo Content", catptoredTodo.getContent());
            assertTrue(catptoredTodo.getArchived());
            assertFalse(catptoredTodo.getCompleted());
            assertEquals(presentTodo.getCreatedDateTime(), catptoredTodo.getCreatedDateTime());
            assertEquals(expectDate, catptoredTodo.getUpdatedDateTime());
        }
    }

    @Test
    @DisplayName("アーカイブ時に対象のTodoがすでにアーカイブ済の場合")
    public void archivedTodoAlreadyArchivedTest() throws Exception {
        Todo presentTodo = new Todo();
        presentTodo.setId(1);
        presentTodo.setTitle("Todo Title");
        presentTodo.setContent("Todo Content");
        presentTodo.setArchived(true);
        presentTodo.setCompleted(false);
        presentTodo.setCreatedDateTime(
                Date.from(
                        LocalDateTime.of(
                                        2021,
                                        1,
                                        1,
                                        12,
                                        30,
                                        0)
                                .atZone(
                                        ZoneId.systemDefault())
                                .toInstant())
        );
        presentTodo.setUpdatedDateTime(
                Date.from(
                        LocalDateTime.of(
                                        2021,
                                        1,
                                        2,
                                        11,
                                        25,
                                        30)
                                .atZone(
                                        ZoneId.systemDefault())
                                .toInstant())
        );
        // findByIdでTodoが存在するようにする
        when(repository.findById(1)).thenReturn(Optional.of(presentTodo));

        // テスト対象のメソッド呼び出し
        service.archiveTodo(1);

        // 呼び出し回数と実行時引数のチェック
        ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(repository, times(1)).findById(integerArgumentCaptor.capture());
        assertEquals(1, integerArgumentCaptor.getValue());
        verify(repository, never()).save(any(Todo.class));

    }

    @Test
    @DisplayName("アーカイブ時に該当するTodoが存在しない場合")
    public void archivedTodoNotPresentTest() throws Exception {
        // findByIdでTodoが存在しないようにする
        when(repository.findById(1)).thenReturn(Optional.empty());

        service.archiveTodo(1);

        // 呼び出し回数と実行時引数のチェック
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(repository, times(1)).findById(argumentCaptor.capture());
        assertEquals(1, argumentCaptor.getValue());
        verify(repository, never()).save(any(Todo.class));
    }

    @Test
    @DisplayName("アーカイブ解除処理が成功する場合")
    public void unArchivedTodoCompleteTest() throws Exception {
        Todo presentTodo = new Todo();
        presentTodo.setId(1);
        presentTodo.setTitle("Todo Title");
        presentTodo.setContent("Todo Content");
        presentTodo.setArchived(true);
        presentTodo.setCompleted(false);
        presentTodo.setCreatedDateTime(
                Date.from(
                        LocalDateTime.of(
                                        2021,
                                        1,
                                        1,
                                        12,
                                        30,
                                        0)
                                .atZone(
                                        ZoneId.systemDefault())
                                .toInstant())
        );
        presentTodo.setUpdatedDateTime(
                Date.from(
                        LocalDateTime.of(
                                        2021,
                                        1,
                                        2,
                                        11,
                                        25,
                                        30)
                                .atZone(
                                        ZoneId.systemDefault())
                                .toInstant())
        );
        // findByIdでTodoが存在するようにする
        when(repository.findById(1)).thenReturn(Optional.of(presentTodo));

        // モックしたときの戻り値用のLocalDateTime、判定用のDateオブジェクトをあらかじめ作っておく
        LocalDateTime localDateTime = LocalDateTime.of(2021, 7, 20, 6, 30, 40);
        Date expectDate = Date.from(LocalDateTime.of(2021, 7, 20, 6, 30, 40).atZone(ZoneId.systemDefault()).toInstant());

        // 更新日時のモックのため、LocalDateTimeをインラインモックする
        try(MockedStatic<LocalDateTime> localDateTimeMockedStatic = mockStatic(LocalDateTime.class)) {
            // LocalDateTime.nowの戻り値を設定する
            localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(localDateTime);

            // repositoryのメソッドをMockする
            when(repository.save(any(Todo.class))).thenReturn(new Todo());

            service.unarchiveTodo(1);

            // 呼び出し回数と実行時引数のチェック
            ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(repository, times(1)).findById(integerArgumentCaptor.capture());
            assertEquals(1, integerArgumentCaptor.getValue());
            ArgumentCaptor<Todo> todoArgumentCaptor = ArgumentCaptor.forClass(Todo.class);
            verify(repository, times(1)).save(todoArgumentCaptor.capture());
            Todo catptoredTodo = todoArgumentCaptor.getValue();
            assertEquals(1, catptoredTodo.getId());
            assertEquals("Todo Title", catptoredTodo.getTitle());
            assertEquals("Todo Content", catptoredTodo.getContent());
            assertFalse(catptoredTodo.getArchived());
            assertFalse(catptoredTodo.getCompleted());
            assertEquals(presentTodo.getCreatedDateTime(), catptoredTodo.getCreatedDateTime());
            assertEquals(expectDate, catptoredTodo.getUpdatedDateTime());
        }
    }

    @Test
    @DisplayName("アーカイブ解除時に対象のTodoがすでにアーカイブ解除済の場合")
    public void unArchivedTodoAlreadyArchivedTest() throws Exception {
        Todo presentTodo = new Todo();
        presentTodo.setId(1);
        presentTodo.setTitle("Todo Title");
        presentTodo.setContent("Todo Content");
        presentTodo.setArchived(false);
        presentTodo.setCompleted(false);
        presentTodo.setCreatedDateTime(
                Date.from(
                        LocalDateTime.of(
                                        2021,
                                        1,
                                        1,
                                        12,
                                        30,
                                        0)
                                .atZone(
                                        ZoneId.systemDefault())
                                .toInstant())
        );
        presentTodo.setUpdatedDateTime(
                Date.from(
                        LocalDateTime.of(
                                        2021,
                                        1,
                                        2,
                                        11,
                                        25,
                                        30)
                                .atZone(
                                        ZoneId.systemDefault())
                                .toInstant())
        );
        // findByIdでTodoが存在するようにする
        when(repository.findById(1)).thenReturn(Optional.of(presentTodo));

        // テスト対象のメソッド呼び出し
        service.unarchiveTodo(1);

        // 呼び出し回数と実行時引数のチェック
        ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(repository, times(1)).findById(integerArgumentCaptor.capture());
        assertEquals(1, integerArgumentCaptor.getValue());
        verify(repository, never()).save(any(Todo.class));

    }

    @Test
    @DisplayName("アーカイブ解除時に該当するTodoが存在しない場合")
    public void unArchivedTodoNotPresentTest() throws Exception {
        // findByIdでTodoが存在しないようにする
        when(repository.findById(1)).thenReturn(Optional.empty());

        service.unarchiveTodo(1);

        // 呼び出し回数と実行時引数のチェック
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(repository, times(1)).findById(argumentCaptor.capture());
        assertEquals(1, argumentCaptor.getValue());
        verify(repository, never()).save(any(Todo.class));
    }

}
