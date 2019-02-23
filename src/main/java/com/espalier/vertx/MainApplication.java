package com.espalier.vertx;

import static io.vertx.core.Vertx.vertx;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.vertx.core.VertxOptions;

@SpringBootApplication
public class MainApplication {
    @Autowired
    private TwintVerticle verticle;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MainApplication.class, args);
    }

    @PostConstruct
    public void deployVerticle() {
        vertx(new VertxOptions().setBlockedThreadCheckInterval(10000))
                .deployVerticle(verticle);
    }
}
