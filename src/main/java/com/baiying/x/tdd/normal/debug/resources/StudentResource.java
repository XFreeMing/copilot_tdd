package com.baiying.x.tdd.normal.debug.resources;

import java.util.List;

import com.baiying.x.tdd.normal.run_it.model.Student;
import com.baiying.x.tdd.normal.run_it.model.StudentRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/students")
public class StudentResource {

    private StudentRepository repository;

    @Inject
    public StudentResource(StudentRepository repository) {
        this.repository = repository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Student> all() {
        return repository.all();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") long id) {
        return repository.findById(id).map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(Student student) {
        repository.save(student);
        return Response.status(Response.Status.CREATED).entity(student).build();
    }
}
