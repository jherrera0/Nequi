package nequichallenge.franchises.domain.usecase;

import nequichallenge.franchises.domain.exception.ProductAlreadyExistsException;
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

        Mockito.when(branchPersistencePort.existsById(branchId)).thenReturn(Mono.just(false));

        StepVerifier.create(productCase.createProduct(branchId, product))
                .expectError(ProductAlreadyExistsException.class)
                .verify();
    }

    @Test
    void createProduct_throwsErrorWhenProductAlreadyExists() {
        Integer branchId = 1;
        Product product = new Product();
        product.setName("Existing Product");

        Mockito.when(branchPersistencePort.existsById(branchId)).thenReturn(Mono.just(true));
        Mockito.when(productPersistencePort.existsByName(product.getName())).thenReturn(Mono.just(true));

        StepVerifier.create(productCase.createProduct(branchId, product))
                .expectError(ProductAlreadyExistsException.class)
                .verify();
    }
}