package ru.baldenna.unleashagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.baldenna.unleashagent.config.FeignConfig;

@ConfigurationPropertiesScan
@Import({FeignConfig.class})
@SpringBootApplication
public class UnleashAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnleashAgentApplication.class, args);
	}

}
