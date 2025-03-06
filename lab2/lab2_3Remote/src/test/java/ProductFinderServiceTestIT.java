
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ua.deti.tqs.BasicHttpClient;
import pt.ua.deti.tqs.ISimpleHttpClient;
import pt.ua.deti.tqs.Product;
import pt.ua.deti.tqs.ProductFinderService;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Optional;


class ProductFinderServiceTestIT {

    private ProductFinderService productFinderService;

    @BeforeEach
    void setUp() {
        ISimpleHttpClient httpClient = new BasicHttpClient();
        productFinderService = new ProductFinderService(httpClient);
    }

    @Test
    void testFindProductDetails_ValidProduct() throws IOException {
        Optional<Product> product = productFinderService.findProductDetails(3);

        assertTrue(product.isPresent());
        assertEquals(3, product.get().getId());
        assertNotNull(product.get().getTitle());
        assertNotNull(product.get().getPrice());
        assertNotNull(product.get().getDescription());
        assertNotNull(product.get().getImage());
        assertNotNull(product.get().getCategory());
    }

    @Test
    void testFindProductDetails_ProductNotFound() throws IOException {
        Optional<Product> product = productFinderService.findProductDetails(300);
        assertFalse(product.isPresent());
    }
}

