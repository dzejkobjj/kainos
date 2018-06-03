package pl.jakubmichalowski.kainos;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.jakubmichalowski.kainos.utilities.DataGenerator;

import java.io.IOException;

/**
 * Created by Jakub Michałowski on 01.06.2018.
 * All rights reserved.
 */
@Configuration
public class AppConfig {

    @Bean
    DataGenerator dataGenerator(){
        return new DataGenerator();
    }
}
