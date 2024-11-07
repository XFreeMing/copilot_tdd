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
    public void testAll() {
        manager.getTransaction().begin();
        Student student1 = new Student();
        Student student2 = new Student();
        student1.setName("Student One");
        student1.setEmail("student1@example.com");
        student2.setName("Student Two");
        student2.setEmail("student2@example.com");
        manager.persist(student1);
        manager.persist(student2);
        manager.getTransaction().commit();

        List<Student> students = repository.all();

        assertEquals(2, students.size());
    }

    @Test
    public void testSave() {
        Student student = new Student();
        manager.getTransaction().begin();
        repository.save(student);
        manager.getTransaction().commit();

        Student found = manager.find(Student.class, student.getId());
        assertNotNull(found);
    }

    @Test
    public void testFindById() {
        manager.getTransaction().begin();
        Student student = new Student();
        manager.persist(student);
        manager.getTransaction().commit();

        Optional<Student> result = repository.findById(student.getId());

        assertTrue(result.isPresent());
        assertEquals(student, result.get());
    }

    @Test
    public void testFindByIdNotFound() {
        Optional<Student> result = repository.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByEmail() {
        manager.getTransaction().begin();
        Student student = new Student();
        student.setEmail("test@example.com");
        manager.persist(student);
        manager.getTransaction().commit();

        Optional<Student> result = repository.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(student, result.get());
    }

    @Test
    public void testFindByEmailNotFound() {
        Optional<Student> result = repository.findByEmail("test@example.com");

        assertFalse(result.isPresent());
    }
}