package com.assisisolutions.blog.wiremockexamples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class WiremockExamplesApplication implements CommandLineRunner {


    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        new SpringApplication(WiremockExamplesApplication.class).run();

    }

    @Override
    public void run(String... args) {
        System.out.println(" Users");
        System.out.println("-----------------------");
        List<User> users = userService.getUsers();
        for (User user : users) {
            System.out.println(user.getUsername());
        }
        System.out.println("-----------------------");
        System.out.println(users.size() + " user(s)");
    }
}
