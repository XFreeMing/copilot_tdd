package com.baiying.x.tdd.normal.test_app;

import java.util.List;
import java.util.Optional;

import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@Singleton
public class StudentRepository {

    private EntityManager manager;

    public StudentRepository(EntityManager manager) {
        this.manager = manager;
    }

    public List<Student> all() {
        return manager.createQuery("select s from Student s", Student.class).getResultList();
    }

    public Student save(Student student) {
        manager.persist(student);
        return student;
    }

    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(manager.find(Student.class, id));
    }

    public Optional<Student> findByEmail(String email) {
        TypedQuery<Student> query = manager.createQuery("select s from Student s where s.email = :email",
                Student.class);
        return query.setParameter("email", email).getResultList().stream().findFirst();
    }
}
