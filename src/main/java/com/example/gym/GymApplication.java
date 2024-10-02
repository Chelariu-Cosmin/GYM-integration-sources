package com.example.gym;

import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.logging.Logger;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GymApplication {
    private static Logger logger = Logger.getLogger(GymApplication.class.getName());
	public static void main(String[] args) {
        logger.info("Loading ... GYM DS Default Settings ... ");
		SpringApplication.run(GymApplication.class, args);
	}

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return (mapperBuilder) -> mapperBuilder.modulesToInstall(new JaxbAnnotationModule());
    }
}
