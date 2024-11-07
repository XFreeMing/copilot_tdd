package com.baiying.x.tdd.normal.test_app;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestApplication {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("student");

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();

        StudentRepository repository = new StudentRepository(manager);

        Student zs = repository.save(new Student("张三", "zs@lenovo.com"));

        manager.getTransaction().commit();

        System.out.println("保存成功, id: " + zs.getId());

        Optional<Student> student = repository.findById(zs.getId());

        System.out.println(student);

        System.out.println(repository.findByEmail("zs@lenovo.com"));
        System.out.println(repository.findByEmail("zs1@lenovo.com"));

    }

}
