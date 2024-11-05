package com.baiying.x.tdd.normal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.baiying.x.tdd.normal.test_app.Student;
import com.baiying.x.tdd.normal.test_app.StudentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class AppTest {
    private EntityManagerFactory factory;
    private EntityManager manager;
    private StudentRepository repository;
    private Student zs;

    @BeforeEach
    public void before() {
        factory = Persistence.createEntityManagerFactory("student");
        manager = factory.createEntityManager();
        repository = new StudentRepository(manager);

        manager.getTransaction().begin();
        zs = repository.save(new Student("张三", "zs@lenovo.com"));
        manager.getTransaction().commit();
    }

    @AfterEach
    public void after() {
        manager.clear();
        manager.getTransaction().begin();
        manager.createQuery("DELETE FROM Student").executeUpdate();
        manager.getTransaction().commit();
        manager.close();
        factory.close();
    }

    @Test
    public void should_generate_id_for_save_entity() {
        Assertions.assertNotEquals(0, zs.getId());
    }

    @Test
    public void should_be_able_to_load_saved_student_by_id() {
        manager.getTransaction().begin();
        Student zs = repository.save(new Student("张三", "zs@lenovo.com"));
        manager.getTransaction().commit();
        Optional<Student> loaded = repository.findById(zs.getId());
        assertTrue(loaded.isPresent());
        assertEquals(zs.getId(), loaded.get().getId());
        assertEquals(zs.getName(), loaded.get().getName());
        assertEquals(zs.getEmail(), loaded.get().getEmail());
    }

    @Test
    public void should_be_able_to_find_student_by_email() {
        Optional<Student> loaded = repository.findByEmail(zs.getEmail());
        assertTrue(loaded.isPresent());
        assertEquals(zs.getId(), loaded.get().getId());
        assertEquals(zs.getName(), loaded.get().getName());
        assertEquals(zs.getEmail(), loaded.get().getEmail());
    }

}