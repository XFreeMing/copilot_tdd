package com.baiying.x.tdd.normal.debug.model;

import lombok.Getter;

@Getter
public class Student {

    private Long id;
    private String name;
    private String email;

    public Student(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
