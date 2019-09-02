package com.gusuchen.docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Docker for {@link SpringApplication}
 *
 * @author gusuchen
 * @since 2019-09-02
 */
@SpringBootApplication
public class DockerSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(DockerSpringApplication.class, args);
    }
}
