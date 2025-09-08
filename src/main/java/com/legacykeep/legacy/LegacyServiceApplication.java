package com.legacykeep.legacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * LegacyKeep Legacy Service Application
 * 
 * This service manages multi-generational family legacy preservation including:
 * - Content management (text, audio, video, image, document)
 * - Legacy inheritance across generations
 * - Cultural and religious content preservation
 * - Family collaboration and content continuity
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableKafka
public class LegacyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LegacyServiceApplication.class, args);
    }
}
