package pt.ua.deti.tqs;

import java.util.Optional;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductFinderService {
    private static final Logger logger = Logger.getLogger(ProductFinderService.class.getName());
    private static final String API_PRODUCTS = "https://fakestoreapi.com/products/";
    private final ISimpleHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON parser

    public ProductFinderService(ISimpleHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Optional<Product> findProductDetails(Integer id) {
        String url = API_PRODUCTS + id;
        String jsonResponse = httpClient.doHttpGet(url);

        try {
            Product product = objectMapper.readValue(jsonResponse, Product.class);
            return Optional.of(product);
        } catch (Exception e) {
            logger.severe("JSON Parsing Error: " + e.getMessage());
            return Optional.empty();
        }
    }
}

