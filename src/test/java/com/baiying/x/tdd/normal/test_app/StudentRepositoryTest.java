package com.baiying.x.tdd.normal.test_app;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

public class StudentRepositoryTest {

    private EntityManager manager;
    private StudentRepository repository;
    private Student zs;

    @BeforeEach
    public void setUp() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("student");
        manager = factory.createEntityManager();
        repository = new StudentRepository(manager);

        // manager.getTransaction().begin();
        // zs = repository.save(new Student("张三", "zs@lenovo.com"));
        // manager.getTransaction().commit();

    }

    @AfterEach
    public void tearDown() {
        if (manager.getTransaction().isActive()) {
            manager.getTransaction().rollback();
        }
        manager.close();
    }

    @Test
    public void testSaveStudent() {
        manager.getTransaction().begin();
        Student student = new Student("张三", "zs@lenovo.com");
        Student savedStudent = repository.save(student);
        manager.getTransaction().commit();

        assertNotNull(savedStudent);
        assertEquals("张三", savedStudent.getName());
        assertEquals("zs@lenovo.com", savedStudent.getEmail());
    }

    @Test
    public void testFindAllStudents() {
        manager.getTransaction().begin();
        repository.save(new Student("张三", "zs@lenovo.com"));
        repository.save(new Student("李四", "ls@lenovo.com"));
        manager.getTransaction().commit();

        List<Student> students = repository.all();
        assertEquals(2, students.size());
    }

    @Test
    public void testFindStudentById() {
        manager.getTransaction().begin();
        Student student = repository.save(new Student("张三", "zs@lenovo.com"));
        manager.getTransaction().commit();

        Optional<Student> foundStudent = repository.findById(student.getId());
        assertTrue(foundStudent.isPresent());
        assertEquals("张三", foundStudent.get().getName());
    }

    @Test
    public void testFindStudentByEmail() {
        manager.getTransaction().begin();
        repository.save(new Student("张三", "zs@lenovo.com"));
        manager.getTransaction().commit();

        Optional<Student> foundStudent = repository.findByEmail("zs@lenovo.com");
        assertTrue(foundStudent.isPresent());
        assertEquals("张三", foundStudent.get().getName());
    }
}