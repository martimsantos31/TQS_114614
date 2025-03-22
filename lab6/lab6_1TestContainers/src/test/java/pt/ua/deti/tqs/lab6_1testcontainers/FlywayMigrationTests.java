package pt.ua.deti.tqs.lab6_1testcontainers;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pt.ua.deti.tqs.lab6_1testcontainers.entity.Customer;
import pt.ua.deti.tqs.lab6_1testcontainers.repository.CustomerRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FlywayMigrationTests {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @Order(1)
    void testDatabaseHasMigratedData() {
        List<Customer> customers = customerRepository.findAll();

        assertEquals(2, customers.size());

        Customer firstCustomer = customers.get(0);
        assertEquals("Alice Johnson", firstCustomer.getName());
        assertEquals("alice@example.com", firstCustomer.getEmail());

        Customer secondCustomer = customers.get(1);
        assertEquals("Bob Smith", secondCustomer.getName());
        assertEquals("bob@example.com", secondCustomer.getEmail());
    }
}