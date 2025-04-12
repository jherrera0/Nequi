package nequichallenge.franchises.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    @Test
    void shouldCreateProductWithValidData() {
        Product product = new Product(1, "Product1", 10);
        assertEquals(1, product.getId());
        assertEquals("Product1", product.getName());
        assertEquals(10, product.getStock());
    }

    @Test
    void shouldCreateProductWithDefaultConstructor() {
        Product product = new Product();
        product.setId(2);
        product.setName("Product2");
        product.setStock(15);

        assertEquals(2, product.getId());
        assertEquals("Product2", product.getName());
        assertEquals(15, product.getStock());
    }

    @Test
    void shouldAllowUpdatingProductName() {
        Product product = new Product();
        product.setName("Updated Product");
        assertEquals("Updated Product", product.getName());
    }

    @Test
    void shouldHandleNullNameGracefully() {
        Product product = new Product(1, null, 5);
        assertNull(product.getName());
    }

    @Test
    void shouldAllowUpdatingProductStock() {
        Product product = new Product();
        product.setStock(20);
        assertEquals(20, product.getStock());
    }
    @Test
    void shouldAllowUpdatingProductIsActive() {
        Product product = new Product();
        product.setIsActive(true);
        assertTrue(product.getIsActive());
    }
}