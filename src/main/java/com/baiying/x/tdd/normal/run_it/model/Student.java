package com.baiying.x.tdd.normal.run_it.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {

    private Long id;
    private String name;
    private String email;

    // Default constructor
    public Student() {
    }

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
