package com.example.todo.contoller;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class TodoEditForm extends TodoForm{
    @NotNull
    @Min(1)
    @Max(Integer.MAX_VALUE)
    private Integer id;

    @NotNull
    private Boolean completed;

    @NotNull
    private Boolean archived;

    private Date createdDateTime;

    private Date updatedDateTime;

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

    public void setCreatedDateTime(Date createdDateTime) { this.createdDateTime = createdDateTime; }

    public Date getCreatedDateTime() { return this.createdDateTime; }

    public void setUpdatedDateTime(Date updatedDateTime) { this.updatedDateTime = updatedDateTime; }

    public Date getUpdatedDateTime() { return this.updatedDateTime; }

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
