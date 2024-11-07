package com.baiying.x.tdd.normal.run_it;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.baiying.x.tdd.normal.run_it.model.Student;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class RunItTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new com.baiying.x.tdd.normal.run_it.Application();
    }

    @Test
    public void testAll() {
        Student[] students = target("students").request().get(Student[].class);
        assertEquals(3, students.length);
        assertEquals("1", students[0].getName());
        assertEquals("2", students[1].getName());
        assertEquals("3", students[2].getName());
    }

    @Test
    public void testFindById_Found() {
        Student student = target("students/1").request().get(Student.class);
        assertEquals("1", student.getName());
    }

    @Test
    public void testFindById_NotFound() {
        int status = target("students/4").request().get().getStatus();
        assertEquals(404, status);
    }

}
