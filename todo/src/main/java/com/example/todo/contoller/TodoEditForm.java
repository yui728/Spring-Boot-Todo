package com.example.todo.contoller;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class TodoEditForm extends TodoForm{
    @NotNull
    @Min(1)
    @Max(Integer.MAX_VALUE)
    private Integer id;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }
}
