package mate.academy.onlinebookstore;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    @ServiceConnection
    protected static final MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
}
