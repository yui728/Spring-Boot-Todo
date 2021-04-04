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

    @NotNull
    private Boolean completed;

    @NotNull
    private Boolean archived;

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

    public Boolean getCompleted() {
        return this.completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getArchived() {
        return this.archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }
}
