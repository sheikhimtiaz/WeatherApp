package com.sheikhimtiaz.data.generator;

import com.sheikhimtiaz.data.entity.User;
import com.sheikhimtiaz.data.service.UserRepository;
import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(UserRepository userRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 100 Sample Person entities...");
            ExampleDataGenerator<User> userExampleDataGenerator = new ExampleDataGenerator<>(
                    User.class, LocalDateTime.of(2022, 2, 24, 0, 0, 0));
            
            userExampleDataGenerator.setData(User::setFirstName, DataType.FIRST_NAME);
            userExampleDataGenerator.setData(User::setLastName, DataType.LAST_NAME);
            userExampleDataGenerator.setData(User::setEmail, DataType.EMAIL);
            userRepository.saveAll(userExampleDataGenerator.create(100, seed));

            logger.info("Generated demo data");
        };
    }

}