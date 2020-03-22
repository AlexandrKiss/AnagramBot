package ua.kiev.prog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class AnagramApplication {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(AnagramApplication.class, args);

    }
}
