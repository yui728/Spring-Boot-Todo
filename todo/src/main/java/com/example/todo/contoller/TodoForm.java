package com.example.todo.contoller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TodoForm {
    @NotNull
    @Size(min=1, max=50)
    private String title;

    @NotNull
    @Size(min=1, max=500)
    private String content;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
