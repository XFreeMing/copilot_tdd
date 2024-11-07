package com.baiying.x.tdd.normal.debug;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.baiying.x.tdd.normal.run_it.model.Student;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class DebugTest extends JerseyTest {
    @Override
    protected Application configure() {
        return new com.baiying.x.tdd.normal.debug.Application();
    }

    @Test
    public void testSave() {
        Student student = new Student(4, "4", "4@lenovo.com");
        Response response = target("students").request(MediaType.APPLICATION_JSON)
                .post(Entity.json(student));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Student savedStudent = response.readEntity(Student.class);
        assertEquals(student.getId(), savedStudent.getId());
        assertEquals(student.getEmail(), savedStudent.getEmail());
    }
}
