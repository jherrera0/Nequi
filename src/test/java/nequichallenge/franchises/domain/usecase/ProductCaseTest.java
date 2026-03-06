package nequichallenge.franchises.domain.usecase;

import nequichallenge.franchises.domain.exception.*;
import nequichallenge.franchises.domain.model.Branch;
import nequichallenge.franchises.domain.model.Product;
import nequichallenge.franchises.domain.model.ProductTopStock;
import nequichallenge.franchises.domain.spi.IBranchPersistencePort;
import nequichallenge.franchises.domain.spi.IFranchisePersistencePort;
import nequichallenge.franchises.domain.spi.IProductPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCaseTest {

    @Mock
    private IProductPersistencePort productPersistencePort;

    @Mock
    private IBranchPersistencePort branchPersistencePort;

    @Mock
    private IFranchisePersistencePort franchisePersistencePort;

    @InjectMocks
    private ProductCase productCase;

    // ==================== createProduct ====================

    @Test
    void createProduct_shouldCreateProduct_whenAllValidationsPass() {
        Product product = new Product();
        product.setName("Producto A");
        product.setStock(10);

        when(branchPersistencePort.existsById(1)).thenReturn(Mono.just(true));
        when(productPersistencePort.existsByName("Producto A")).thenReturn(Mono.just(false));
        when(productPersistencePort.createProduct(eq(1), any(Product.class))).thenReturn(Mono.just(product));

        StepVerifier.create(productCase.createProduct(1, product))
                .expectNextMatches(p -> p.getName().equals("Producto A"))
                .verifyComplete();

        verify(branchPersistencePort).existsById(1);
        verify(productPersistencePort).existsByName("Producto A");
        verify(productPersistencePort).createProduct(eq(1), any(Product.class));
    }

    @Test
    void createProduct_shouldThrowProductNameEmptyException_whenNameIsNull() {
        Product product = new Product();
        product.setName(null);
        product.setStock(10);

        StepVerifier.create(productCase.createProduct(1, product))
                .expectError(ProductNameEmptyException.class)
                .verify();

        verify(branchPersistencePort, never()).existsById(any());
        verify(productPersistencePort, never()).existsByName(any());
        verify(productPersistencePort, never()).createProduct(any(), any());
    }

    @Test
    void createProduct_shouldThrowProductNameEmptyException_whenNameIsBlank() {
        Product product = new Product();
        product.setName("   ");
        product.setStock(10);

        StepVerifier.create(productCase.createProduct(1, product))
                .expectError(ProductNameEmptyException.class)
                .verify();

        verify(branchPersistencePort, never()).existsById(any());
    }

    @Test
    void createProduct_shouldThrowProductStockInvalidException_whenStockIsZero() {
        Product product = new Product();
        product.setName("Producto A");
        product.setStock(0);

        StepVerifier.create(productCase.createProduct(1, product))
                .expectError(ProductStockInvalidException.class)
                .verify();

        verify(branchPersistencePort, never()).existsById(any());
    }

    @Test
    void createProduct_shouldThrowProductStockInvalidException_whenStockIsNegative() {
        Product product = new Product();
        product.setName("Producto A");
        product.setStock(-5);

        StepVerifier.create(productCase.createProduct(1, product))
                .expectError(ProductStockInvalidException.class)
                .verify();

        verify(branchPersistencePort, never()).existsById(any());
    }

    @Test
    void createProduct_shouldThrowBranchIdInvalidException_whenBranchIdIsNull() {
        Product product = new Product();
        product.setName("Producto A");
        product.setStock(10);

        StepVerifier.create(productCase.createProduct(null, product))
                .expectError(BranchIdInvalidException.class)
                .verify();

        verify(branchPersistencePort, never()).existsById(any());
    }

    @Test
    void createProduct_shouldThrowBranchIdInvalidException_whenBranchIdIsNegative() {
        Product product = new Product();
        product.setName("Producto A");
        product.setStock(10);

        StepVerifier.create(productCase.createProduct(-1, product))
                .expectError(BranchIdInvalidException.class)
                .verify();

        verify(branchPersistencePort, never()).existsById(any());
    }

    @Test
    void createProduct_shouldThrowBranchNotFoundException_whenBranchDoesNotExist() {
        Product product = new Product();
        product.setName("Producto A");
        product.setStock(10);

        when(branchPersistencePort.existsById(1)).thenReturn(Mono.just(false));

        StepVerifier.create(productCase.createProduct(1, product))
                .expectError(BranchNotFoundException.class)
                .verify();

        verify(branchPersistencePort).existsById(1);
        verify(productPersistencePort, never()).existsByName(any());
        verify(productPersistencePort, never()).createProduct(any(), any());
    }

    @Test
    void createProduct_shouldThrowProductAlreadyExistsException_whenNameAlreadyExists() {
        Product product = new Product();
        product.setName("Producto Existente");
        product.setStock(10);

        when(branchPersistencePort.existsById(1)).thenReturn(Mono.just(true));
        when(productPersistencePort.existsByName("Producto Existente")).thenReturn(Mono.just(true));

        StepVerifier.create(productCase.createProduct(1, product))
                .expectError(ProductAlreadyExistsException.class)
                .verify();

        verify(branchPersistencePort).existsById(1);
        verify(productPersistencePort).existsByName("Producto Existente");
        verify(productPersistencePort, never()).createProduct(any(), any());
    }

    // ==================== deleteProduct ====================

    @Test
    void deleteProduct_shouldDeactivateProduct_whenProductIsActive() {
        Product active = new Product();
        active.setId(1);
        active.setName("Producto A");
        active.setStock(10);
        active.setIsActive(true);

        Product deactivated = new Product();
        deactivated.setId(1);
        deactivated.setIsActive(false);

        when(productPersistencePort.findById(1)).thenReturn(Mono.just(active));
        when(productPersistencePort.updateProduct(any(Product.class))).thenReturn(Mono.just(deactivated));

        StepVerifier.create(productCase.deleteProduct(active))
                .expectNextMatches(p -> !p.getIsActive())
                .verifyComplete();

        verify(productPersistencePort).findById(1);
        verify(productPersistencePort).updateProduct(argThat(p -> !p.getIsActive()));
    }

    @Test
    void deleteProduct_shouldThrowProductNotFoundException_whenProductDoesNotExist() {
        Product product = new Product();
        product.setId(1);

        when(productPersistencePort.findById(1)).thenReturn(Mono.empty());

        StepVerifier.create(productCase.deleteProduct(product))
                .expectError(ProductNotFoundException.class)
                .verify();

        verify(productPersistencePort).findById(1);
        verify(productPersistencePort, never()).updateProduct(any());
    }

    @Test
    void deleteProduct_shouldThrowProductNotFoundException_whenProductIsAlreadyInactive() {
        Product inactive = new Product();
        inactive.setId(1);
        inactive.setIsActive(false);

        when(productPersistencePort.findById(1)).thenReturn(Mono.just(inactive));

        StepVerifier.create(productCase.deleteProduct(inactive))
                .expectError(ProductNotFoundException.class)
                .verify();

        verify(productPersistencePort).findById(1);
        verify(productPersistencePort, never()).updateProduct(any());
    }

    // ==================== addProductStock ====================

    @Test
    void addProductStock_shouldUpdateStock_whenProductExistsAndStockIsValid() {
        Product request = new Product();
        request.setId(1);
        request.setStock(25);

        Product existing = new Product();
        existing.setId(1);
        existing.setName("Producto A");
        existing.setStock(10);

        Product updated = new Product();
        updated.setId(1);
        updated.setStock(25);

        when(productPersistencePort.findById(1)).thenReturn(Mono.just(existing));
        when(productPersistencePort.updateProduct(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(productCase.addProductStock(request))
                .expectNextMatches(p -> p.getStock().equals(25))
                .verifyComplete();

        verify(productPersistencePort).findById(1);
        verify(productPersistencePort).updateProduct(any());
    }

    @Test
    void addProductStock_shouldThrowProductNotFoundException_whenProductDoesNotExist() {
        Product request = new Product();
        request.setId(1);
        request.setStock(10);

        when(productPersistencePort.findById(1)).thenReturn(Mono.empty());

        StepVerifier.create(productCase.addProductStock(request))
                .expectError(ProductNotFoundException.class)
                .verify();

        verify(productPersistencePort).findById(1);
        verify(productPersistencePort, never()).updateProduct(any());
    }

    @Test
    void addProductStock_shouldThrowProductStockInvalidException_whenStockIsZero() {
        Product request = new Product();
        request.setId(1);
        request.setStock(0);

        Product existing = new Product();
        existing.setId(1);
        existing.setStock(10);

        when(productPersistencePort.findById(1)).thenReturn(Mono.just(existing));

        StepVerifier.create(productCase.addProductStock(request))
                .expectError(ProductStockInvalidException.class)
                .verify();

        verify(productPersistencePort).findById(1);
        verify(productPersistencePort, never()).updateProduct(any());
    }

    @Test
    void addProductStock_shouldThrowProductStockInvalidException_whenStockIsNegative() {
        Product request = new Product();
        request.setId(1);
        request.setStock(-5);

        Product existing = new Product();
        existing.setId(1);
        existing.setStock(10);

        when(productPersistencePort.findById(1)).thenReturn(Mono.just(existing));

        StepVerifier.create(productCase.addProductStock(request))
                .expectError(ProductStockInvalidException.class)
                .verify();

        verify(productPersistencePort).findById(1);
        verify(productPersistencePort, never()).updateProduct(any());
    }

    // ==================== getTopStockProductsByBranchAssociatedToFranchise ====================

    @Test
    void getTopStock_shouldReturnTopStockProducts_whenFranchiseAndBranchesExist() {
        Branch branch = new Branch(1, "Sucursal Norte", List.of());
        Product product = new Product(1, "Producto Top", 100);

        when(franchisePersistencePort.franchiseExistsById(1)).thenReturn(Mono.just(true));
        when(branchPersistencePort.getBranchesByFranchiseId(1)).thenReturn(Flux.just(branch));
        when(productPersistencePort.getTopStockProductsByBranchId(1)).thenReturn(Mono.just(product));

        StepVerifier.create(productCase.getTopStockProductsByBranchAssociatedToFranchise(1))
                .expectNextMatches(ts ->
                        ts.getBranchId().equals(1) &&
                        ts.getBranchName().equals("Sucursal Norte") &&
                        ts.getProductId().equals(1) &&
                        ts.getProductName().equals("Producto Top") &&
                        ts.getStock() == 100
                )
                .verifyComplete();
    }

    @Test
    void getTopStock_shouldThrowFranchiseNotFoundException_whenFranchiseDoesNotExist() {
        when(franchisePersistencePort.franchiseExistsById(1)).thenReturn(Mono.just(false));

        StepVerifier.create(productCase.getTopStockProductsByBranchAssociatedToFranchise(1))
                .expectError(FranchiseNotFoundException.class)
                .verify();

        verify(franchisePersistencePort).franchiseExistsById(1);
        verify(branchPersistencePort, never()).getBranchesByFranchiseId(any());
    }

    @Test
    void getTopStock_shouldReturnEmpty_whenFranchiseHasNoBranches() {
        when(franchisePersistencePort.franchiseExistsById(1)).thenReturn(Mono.just(true));
        when(branchPersistencePort.getBranchesByFranchiseId(1)).thenReturn(Flux.empty());

        StepVerifier.create(productCase.getTopStockProductsByBranchAssociatedToFranchise(1))
                .verifyComplete();

        verify(franchisePersistencePort).franchiseExistsById(1);
        verify(branchPersistencePort).getBranchesByFranchiseId(1);
        verify(productPersistencePort, never()).getTopStockProductsByBranchId(any());
    }

    @Test
    void getTopStock_shouldReturnMultipleResults_whenFranchiseHasMultipleBranches() {
        Branch branch1 = new Branch(1, "Sucursal Norte", List.of());
        Branch branch2 = new Branch(2, "Sucursal Sur", List.of());
        Product product1 = new Product(1, "Producto Top 1", 50);
        Product product2 = new Product(2, "Producto Top 2", 80);

        when(franchisePersistencePort.franchiseExistsById(1)).thenReturn(Mono.just(true));
        when(branchPersistencePort.getBranchesByFranchiseId(1)).thenReturn(Flux.just(branch1, branch2));
        when(productPersistencePort.getTopStockProductsByBranchId(1)).thenReturn(Mono.just(product1));
        when(productPersistencePort.getTopStockProductsByBranchId(2)).thenReturn(Mono.just(product2));

        StepVerifier.create(productCase.getTopStockProductsByBranchAssociatedToFranchise(1))
                .expectNextCount(2)
                .verifyComplete();
    }

    // ==================== updateProductName ====================

    @Test
    void updateProductName_shouldUpdateName_whenProductExistsAndNameIsValid() {
        Product request = new Product();
        request.setId(1);
        request.setName("Nombre Nuevo");

        Product existing = new Product();
        existing.setId(1);
        existing.setName("Nombre Viejo");

        Product updated = new Product();
        updated.setId(1);
        updated.setName("Nombre Nuevo");

        when(productPersistencePort.findById(1)).thenReturn(Mono.just(existing));
        when(productPersistencePort.updateProduct(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(productCase.updateProductName(request))
                .expectNextMatches(p -> p.getName().equals("Nombre Nuevo"))
                .verifyComplete();

        verify(productPersistencePort).findById(1);
        verify(productPersistencePort).updateProduct(any());
    }

    @Test
    void updateProductName_shouldThrowProductNameEmptyException_whenNameIsNull() {
        Product request = new Product();
        request.setId(1);
        request.setName(null);

        StepVerifier.create(productCase.updateProductName(request))
                .expectError(ProductNameEmptyException.class)
                .verify();

        verify(productPersistencePort, never()).findById(any());
        verify(productPersistencePort, never()).updateProduct(any());
    }

    @Test
    void updateProductName_shouldThrowProductNameEmptyException_whenNameIsBlank() {
        Product request = new Product();
        request.setId(1);
        request.setName("   ");

        StepVerifier.create(productCase.updateProductName(request))
                .expectError(ProductNameEmptyException.class)
                .verify();

        verify(productPersistencePort, never()).findById(any());
        verify(productPersistencePort, never()).updateProduct(any());
    }

    @Test
    void updateProductName_shouldThrowProductNotFoundException_whenProductDoesNotExist() {
        Product request = new Product();
        request.setId(1);
        request.setName("Nombre Nuevo");

        when(productPersistencePort.findById(1)).thenReturn(Mono.empty());

        StepVerifier.create(productCase.updateProductName(request))
                .expectError(ProductNotFoundException.class)
                .verify();

        verify(productPersistencePort).findById(1);
        verify(productPersistencePort, never()).updateProduct(any());
    }

    @Test
    void updateProductName_shouldTrimName_whenNameHasWhitespace() {
        Product request = new Product();
        request.setId(1);
        request.setName("  Nombre con espacios  ");

        Product existing = new Product();
        existing.setId(1);
        existing.setName("Nombre Viejo");

        Product updated = new Product();
        updated.setId(1);
        updated.setName("Nombre con espacios");

        when(productPersistencePort.findById(1)).thenReturn(Mono.just(existing));
        when(productPersistencePort.updateProduct(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(productCase.updateProductName(request))
                .expectNextMatches(p -> p.getName().equals("Nombre con espacios"))
                .verifyComplete();
    }
}

