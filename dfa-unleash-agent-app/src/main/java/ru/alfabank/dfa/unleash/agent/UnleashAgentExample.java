package ru.alfabank.dfa.unleash.agent;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class UnleashAgentExample {

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(UnleashAgentExample.class).run(args);
    }

}