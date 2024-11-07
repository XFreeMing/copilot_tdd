package com.baiying.x.tdd.normal.debug;

import org.glassfish.jersey.server.ResourceConfig;

import com.baiying.x.tdd.normal.run_it.model.Student;
import com.baiying.x.tdd.normal.run_it.model.StudentRepository;

import org.glassfish.jersey.jackson.JacksonFeature;

import org.glassfish.jersey.internal.inject.AbstractBinder;

public class Application extends ResourceConfig {
    public Application() {
        packages("com.baiying.x.tdd.normal.debug.resources");
        register(JacksonFeature.class);

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                StudentRepository studentRepository = new StudentRepository(
                        new Student(1, "1", "1@lenovo.com"),
                        new Student(2, "2", "2@lenovo.com"),
                        new Student(3, "3", "3@lenovo.com"));
                bind(studentRepository).to(StudentRepository.class);
            }
        });
    }
}
