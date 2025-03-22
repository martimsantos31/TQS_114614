package pt.ua.deti.tqs.lab6_1testcontainers;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pt.ua.deti.tqs.lab6_1testcontainers.entity.Customer;
import pt.ua.deti.tqs.lab6_1testcontainers.repository.CustomerRepository;

import java.awt.print.Book;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Define ordem dos testes
public class PostgreInitTests {

    @Container
    @SuppressWarnings("rawtypes")
    public static PostgreSQLContainer container = new PostgreSQLContainer("postgres:latest")
            .withUsername("tqs")
            .withPassword("tqs")
            .withDatabaseName("customers");

    @Autowired
    private CustomerRepository customerRepository;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    private static Long customerId;

    @Test
    @Order(1)
    void testInsertCustomer() {
        Customer customer = new Customer(null, "Jane Doe", "jane@ua.pt", "Lisboa", "987654321");
        Customer savedCustomer = customerRepository.save(customer);
        customerId = savedCustomer.getId();

        assertNotNull(customerId);
        assertEquals("Jane Doe", savedCustomer.getName());
    }

    @Test
    @Order(2)
    void testRetrieveCustomer() {
        Optional<Customer> retrievedCustomer = customerRepository.findCustomerById(customerId);

        assertTrue(retrievedCustomer.isPresent());
        assertEquals("Jane Doe", retrievedCustomer.get().getName());
    }

    @Test
    @Order(3)
    void testUpdateCustomer() {
        Optional<Customer> retrievedCustomer = customerRepository.findCustomerById(customerId);
        assertTrue(retrievedCustomer.isPresent());

        Customer customer = retrievedCustomer.get();
        customer.setName("Jane Smith");
        customerRepository.save(customer);

        Optional<Customer> updatedCustomer = customerRepository.findCustomerById(customerId);
        assertTrue(updatedCustomer.isPresent());
        assertEquals("Jane Smith", updatedCustomer.get().getName());
    }

    @Test
    @Order(4)
    void testDeleteCustomer() {
        customerRepository.deleteById(customerId);
        Optional<Customer> deletedCustomer = customerRepository.findCustomerById(customerId);
        assertFalse(deletedCustomer.isPresent());
    }
}