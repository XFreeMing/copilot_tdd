package com.baiying.x.tdd.normal.run_it;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.baiying.x.tdd.normal.run_it.model.Student;

import jakarta.ws.rs.core.Application;

public class RunItTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new com.baiying.x.tdd.normal.run_it.Application();
    }

    @Test
    public void should_fetch_all_students_from_api() {
        Student[] students = target("students").request().get(Student[].class);

        assertEquals(3, students.length);
        assertEquals("1", students[0].getName());
        assertEquals("1@lenovo.com", students[0].getEmail());
        assertEquals(1, students[0].getId());

    }

}
