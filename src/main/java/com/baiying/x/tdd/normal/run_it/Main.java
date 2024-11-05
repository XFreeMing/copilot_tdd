package com.baiying.x.tdd.normal.run_it;

import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;

import org.glassfish.jersey.jetty.JettyHttpContainerFactory;

public class Main {

    public static void main(String[] args) throws Exception {
        URI baseUri = UriBuilder.fromUri("http://localhost/")
                .port(8080).build();
        JettyHttpContainerFactory.createServer(baseUri, new Application()).start();
    }
}