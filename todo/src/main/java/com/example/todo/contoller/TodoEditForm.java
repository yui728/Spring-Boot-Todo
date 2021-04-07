package com.example.todo.contoller;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class TodoEditForm extends TodoForm{
    @NotNull
    @Min(1)
    @Max(Integer.MAX_VALUE)
    private Integer id;

    @NotNull
    private Boolean completed;

    @NotNull
    private Boolean archived;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
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

    @Override
    public String toString() {
        return "TodoEditForm:"
                + " ID = " + this.getId()
                + " title = " + this.getTitle()
                + " content = " + this.getContent()
                + " completed = " + this.getCompleted()
                + " archived = " + this.getArchived();
    }
}
