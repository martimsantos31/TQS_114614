
import pt.ua.deti.tqs.ISimpleHttpClient;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ua.deti.tqs.Product;
import pt.ua.deti.tqs.ProductFinderService;
import java.util.Optional;


class ProductFinderServiceTest {

    private ISimpleHttpClient httpClientMock;
    private ProductFinderService productFinderService;

    @BeforeEach
    void setUp() {
        httpClientMock = mock(ISimpleHttpClient.class);
        productFinderService = new ProductFinderService(httpClientMock);
    }

    @Test
    void testFindProductDetails_ValidProduct() {
        String fakeJsonResponse = "{ \"id\": 3, \"title\": \"Mens Cotton Jacket\", \"price\": 55.99, \"description\": \"Great jacket\", \"image\": \"jacket.jpg\", \"category\": \"men's clothing\" }";

        when(httpClientMock.doHttpGet("https://fakestoreapi.com/products/3"))
                .thenReturn(fakeJsonResponse);

        Optional<Product> product = productFinderService.findProductDetails(3);

        assertTrue(product.isPresent());
        assertEquals(3, product.get().getId());
        assertEquals("Mens Cotton Jacket", product.get().getTitle());
        assertEquals(55.99, product.get().getPrice());
        assertEquals("Great jacket", product.get().getDescription());
        assertEquals("jacket.jpg", product.get().getImage());
        assertEquals("men's clothing", product.get().getCategory());
    }

    @Test
    void testFindProductDetails_ProductNotFound() {
        when(httpClientMock.doHttpGet("https://fakestoreapi.com/products/300"))
                .thenReturn("");

        Optional<Product> product = productFinderService.findProductDetails(300);

        assertFalse(product.isPresent());
    }
}


