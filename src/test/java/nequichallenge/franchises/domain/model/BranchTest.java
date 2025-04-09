package nequichallenge.franchises.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BranchTest {

    @Test
    void shouldCreateBranchWithValidData() {
        Branch branch = new Branch(1, "Main Branch", List.of(new Product(1,"Product1",1),
                new Product(2,"Product2",2)));
        assertEquals(1, branch.getId());
        assertEquals("Main Branch", branch.getName());
        assertEquals(2, branch.getProducts().size());
    }

    @Test
    void shouldAllowSettingBranchId() {
        Branch branch = new Branch();
        branch.setId(2);
        assertEquals(2, branch.getId());
    }

    @Test
    void shouldAllowUpdatingBranchName() {
        Branch branch = new Branch();
        branch.setName("Updated Branch");
        assertEquals("Updated Branch", branch.getName());
    }

    @Test
    void shouldHandleEmptyProductList() {
        Branch branch = new Branch(1, "Empty Branch", Collections.emptyList());
        assertNotNull(branch.getProducts());
        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    void shouldAllowAddingProductsToBranch() {
        Branch branch = new Branch();
        branch.setProducts(List.of(new Product(1,"Product1",1)));
        assertEquals(1, branch.getProducts().size());
        assertEquals("Product1", branch.getProducts().get(0).getName());
    }

    @Test
    void shouldHandleNullProductsGracefully() {
        Branch branch = new Branch(1, "Null Products Branch", null);
        assertNull(branch.getProducts());
    }
}