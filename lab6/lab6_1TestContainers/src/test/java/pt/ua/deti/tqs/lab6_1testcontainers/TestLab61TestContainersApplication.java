package pt.ua.deti.tqs.lab6_1testcontainers;

import org.springframework.boot.SpringApplication;

public class TestLab61TestContainersApplication {

    public static void main(String[] args) {
        SpringApplication.from(Lab61TestContainersApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
