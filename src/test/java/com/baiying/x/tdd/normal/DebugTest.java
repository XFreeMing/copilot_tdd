package com.baiying.x.tdd.normal;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import jakarta.ws.rs.core.Application;

public class DebugTest extends JerseyTest {
    @Override
    protected Application configure() {
        return new com.baiying.x.tdd.normal.run_it.Application();
    }

    @Test
    public void should_create_stundet_via_api() {
        // Student student = new Student(4, "4", "
    }
}
