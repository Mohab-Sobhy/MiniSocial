package com.example.minisocial;

import com.example.minisocial.model.Test;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;

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