package com.example.minisocial;

import com.example.minisocial.model.Test;
import com.example.minisocial.model.User;
import com.example.minisocial.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
//lol//
@Path("/hello-world")
public class HelloResource {

    @PersistenceContext(unitName = "MiniSocialPU")
    private EntityManager em;


    @GET
    @Produces("text/plain")
    @Transactional
    public String hello() {
        Test t = new Test("Database is working!!");
        em.persist(t);
        return "Hello, World!";
    }


}
