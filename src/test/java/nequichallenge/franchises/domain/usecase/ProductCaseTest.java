package nequichallenge.franchises.domain.usecase;

import nequichallenge.franchises.domain.exception.*;
import nequichallenge.franchises.domain.model.Product;
import nequichallenge.franchises.domain.spi.IBranchPersistencePort;
import nequichallenge.franchises.domain.spi.IProductPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ProductCaseTest {

    @Mock
    private IProductPersistencePort productPersistencePort;

    @Mock
    private IBranchPersistencePort branchPersistencePort;

    @InjectMocks
    private ProductCase productCase;

    @Test
    void createProduct_createsProductWhenBranchExistsAndProductDoesNotExist() {
        Integer branchId = 1;
        Product product = new Product();
        product.setStock(10);
        product.setName("New Product");

        Mockito.when(branchPersistencePort.existsById(branchId)).thenReturn(Mono.just(true));
        Mockito.when(productPersistencePort.existsByName(product.getName())).thenReturn(Mono.just(false));
        Mockito.when(productPersistencePort.createProduct(branchId, product)).thenReturn(Mono.just(product));

        StepVerifier.create(productCase.createProduct(branchId, product))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void createProduct_throwsErrorWhenBranchDoesNotExist() {
        Integer branchId = 1;
        Product product = new Product();
        product.setName("New Product");
        product.setStock(10);

        Mockito.when(branchPersistencePort.existsById(branchId)).thenReturn(Mono.just(false));

        StepVerifier.create(productCase.createProduct(branchId, product))
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    @Test
    void createProduct_throwsErrorWhenProductAlreadyExists() {
        Integer branchId = 1;
        Product product = new Product();
        product.setStock(1);
        product.setName("Existing Product");

        Mockito.when(branchPersistencePort.existsById(branchId)).thenReturn(Mono.just(true));
        Mockito.when(productPersistencePort.existsByName(product.getName())).thenReturn(Mono.just(true));

        StepVerifier.create(productCase.createProduct(branchId, product))
                .expectError(ProductAlreadyExistsException.class)
                .verify();
    }

    @Test
    void createProductThrowsErrorWhenBranchIdIsInvalid() {
        Integer branchId = -1;
        Product product = new Product();
        product.setName("Invalid Branch Product");
        product.setStock(10);

        StepVerifier.create(productCase.createProduct(branchId, product))
                .expectError(BranchIdInvalidException.class)
                .verify();
    }

    @Test
    void createProductThrowsErrorWhenProductNameIsEmpty() {
        Integer branchId = 1;
        Product product = new Product();
        product.setName("");
        product.setStock(10);

        StepVerifier.create(productCase.createProduct(branchId, product))
                .expectError(ProductNameEmptyException.class)
                .verify();
    }

    @Test
    void createProductThrowsErrorWhenProductStockIsInvalid() {
        Integer branchId = 1;
        Product product = new Product();
        product.setName("Invalid Stock Product");
        product.setStock(0);

        StepVerifier.create(productCase.createProduct(branchId, product))
                .expectError(ProductStockInvalidException.class)
                .verify();
    }
    @Test
    void deleteProductDeactivatesProductWhenProductExists() {
        Product product = new Product();
        product.setId(1);
        product.setIsActive(true);

        Product updatedProduct = new Product();
        updatedProduct.setId(1);
        updatedProduct.setIsActive(false);

        Mockito.when(productPersistencePort.findById(product.getId())).thenReturn(Mono.just(product));
        Mockito.when(productPersistencePort.updateProduct(Mockito.any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(productCase.deleteProduct(product))
                .expectNextMatches(result -> result.getId().equals(product.getId()) && !result.getIsActive())
                .verifyComplete();

        Mockito.verify(productPersistencePort).updateProduct(Mockito.argThat(arg ->
                arg.getId().equals(product.getId()) && !arg.getIsActive()
        ));
    }

    @Test
    void deleteProductThrowsErrorWhenProductDoesNotExist() {
        Product product = new Product();
        product.setId(1);

        Mockito.when(productPersistencePort.findById(product.getId())).thenReturn(Mono.empty());

        StepVerifier.create(productCase.deleteProduct(product))
                .expectError(ProductNotFoundException.class)
                .verify();
    }
}