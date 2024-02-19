package com.project.playlist.integration_testing.util;

import org.junit.ClassRule;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Transactional
@Testcontainers
public class MySqlIntegrationTest {

    @ClassRule
    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8")
            .withDatabaseName("db_playlist")
            .withUsername("root")
            .withPassword("Pass.123")
            .withExposedPorts(3307);

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:tc:mysql:8:///db_playlist");
        registry.add("spring.datasource.username", () -> "root");
        registry.add("spring.datasource.password", () -> "Pass.123");
    }

}
