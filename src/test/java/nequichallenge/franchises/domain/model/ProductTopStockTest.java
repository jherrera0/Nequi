package nequichallenge.franchises.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTopStockTest {

    @Test
    void shouldCreateProductTopStockWithAllFields() {
        ProductTopStock product = new ProductTopStock(1, "Branch A", 101, "Product X", 50);

        assertEquals(1, product.getBranchId());
        assertEquals("Branch A", product.getBranchName());
        assertEquals(101, product.getProductId());
        assertEquals("Product X", product.getProductName());
        assertEquals(50, product.getStock());
    }

    @Test
    void shouldAllowUpdatingFields() {
        ProductTopStock product = new ProductTopStock();
        product.setBranchId(2);
        product.setBranchName("Branch B");
        product.setProductId(202);
        product.setProductName("Product Y");
        product.setStock(100);

        assertEquals(2, product.getBranchId());
        assertEquals("Branch B", product.getBranchName());
        assertEquals(202, product.getProductId());
        assertEquals("Product Y", product.getProductName());
        assertEquals(100, product.getStock());
    }

    @Test
    void shouldHandleNullValuesForOptionalFields() {
        ProductTopStock product = new ProductTopStock(null, null, null, null, 0);

        assertNull(product.getBranchId());
        assertNull(product.getBranchName());
        assertNull(product.getProductId());
        assertNull(product.getProductName());
        assertEquals(0, product.getStock());
    }

    @Test
    void shouldHandleNegativeStockValue() {
        ProductTopStock product = new ProductTopStock(3, "Branch C", 303, "Product Z", -10);

        assertEquals(-10, product.getStock());
    }
}