package com.baiying.x.tdd.normal.debug.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StudentRepository {

    private List<Student> students;

    public StudentRepository(Student... students) {
        this.students = new ArrayList<>(Arrays.asList(students));
    }

    public List<Student> all() {
        return Collections.unmodifiableList(students);
    }

    public Optional<Student> findById(Long id) {
        return students.stream().filter(s -> s.getId().equals(id)).findFirst();
    }

    public void save(Student student) {
        students.add(student);
    }
}
