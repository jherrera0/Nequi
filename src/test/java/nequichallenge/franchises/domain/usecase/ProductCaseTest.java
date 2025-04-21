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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

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

    @Test
    void addProductStockUpdatesStockWhenProductExistsAndStockIsValid() {
        Product product = new Product();
        product.setId(1);
        product.setStock(20);

        Product updatedProduct = new Product();
        updatedProduct.setId(1);
        updatedProduct.setStock(20);

        Mockito.when(productPersistencePort.findById(product.getId())).thenReturn(Mono.just(new Product()));
        Mockito.when(productPersistencePort.updateProduct(Mockito.any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(productCase.addProductStock(product))
                .expectNextMatches(result -> result.getStock().equals(20))
                .verifyComplete();
    }

    @Test
    void addProductStockThrowsErrorWhenStockIsInvalid() {
        Product product = new Product(1, "Test Product", 10);
        product.setIsActive(true);
        product.setId(1);
        product.setStock(0);

        Mockito.when(productPersistencePort.findById(product.getId())).thenReturn(Mono.just(product));

        StepVerifier.create(productCase.addProductStock(product))
                .expectError(ProductStockInvalidException.class)
                .verify();
    }

    @Test
    void addProductStockThrowsErrorWhenProductDoesNotExist() {
        Product product = new Product();
        product.setId(1);
        product.setStock(10);

        Mockito.when(productPersistencePort.findById(product.getId())).thenReturn(Mono.empty());

        StepVerifier.create(productCase.addProductStock(product))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void getTopStockProductsByBranchAssociatedToFranchiseReturnsProductsWhenFranchiseExists() {
        Integer franchiseId = 1;
        Branch branch = new Branch(1, "Branch 1", List.of());
        Product product = new Product(1, "Product 1", 50);
        ProductTopStock productTopStock = new ProductTopStock(1, "Branch 1", 1, "Product 1", 50);

        Mockito.when(franchisePersistencePort.franchiseExistsById(franchiseId)).thenReturn(Mono.just(true));
        Mockito.when(branchPersistencePort.getBranchesByFranchiseId(franchiseId)).thenReturn(Flux.just(branch));
        Mockito.when(productPersistencePort.getTopStockProductsByBranchId(branch.getId())).thenReturn(Mono.just(product));

        StepVerifier.create(productCase.getTopStockProductsByBranchAssociatedToFranchise(franchiseId))
                .expectNextMatches(actual ->
                        actual.getBranchId().equals(productTopStock.getBranchId()) &&
                                actual.getBranchName().equals(productTopStock.getBranchName()) &&
                                actual.getProductId().equals(productTopStock.getProductId()) &&
                                actual.getProductName().equals(productTopStock.getProductName()) &&
                                actual.getStock() == productTopStock.getStock()
                )
                .verifyComplete();
    }

    @Test
    void getTopStockProductsByBranchAssociatedToFranchiseThrowsErrorWhenFranchiseDoesNotExist() {
        Integer franchiseId = 1;

        Mockito.when(franchisePersistencePort.franchiseExistsById(franchiseId)).thenReturn(Mono.just(false));

        StepVerifier.create(productCase.getTopStockProductsByBranchAssociatedToFranchise(franchiseId))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    @Test
    void getTopStockProductsByBranchAssociatedToFranchiseReturnsEmptyWhenNoBranchesExist() {
        Integer franchiseId = 1;

        Mockito.when(franchisePersistencePort.franchiseExistsById(franchiseId)).thenReturn(Mono.just(true));
        Mockito.when(branchPersistencePort.getBranchesByFranchiseId(franchiseId)).thenReturn(Flux.empty());

        StepVerifier.create(productCase.getTopStockProductsByBranchAssociatedToFranchise(franchiseId))
                .verifyComplete();
    }
    @Test
    void updateProductNameUpdatesNameWhenProductExistsAndNameIsValid() {
        Product product = new Product();
        product.setId(1);
        product.setName("Updated Name");

        Product existingProduct = new Product();
        existingProduct.setId(1);
        existingProduct.setName("Old Name");

        Mockito.when(productPersistencePort.findById(product.getId())).thenReturn(Mono.just(existingProduct));
        Mockito.when(productPersistencePort.updateProduct(Mockito.any(Product.class))).thenReturn(Mono.just(product));

        StepVerifier.create(productCase.updateProductName(product))
                .expectNextMatches(updatedProduct -> updatedProduct.getName().equals("Updated Name"))
                .verifyComplete();
    }

    @Test
    void updateProductNameThrowsErrorWhenProductDoesNotExist() {
        Product product = new Product();
        product.setId(1);
        product.setName("Nonexistent Product");

        Mockito.when(productPersistencePort.findById(product.getId())).thenReturn(Mono.empty());

        StepVerifier.create(productCase.updateProductName(product))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void updateProductNameThrowsErrorWhenNameIsEmpty() {
        Product product = new Product();
        product.setId(1);
        product.setName("");

        StepVerifier.create(productCase.updateProductName(product))
                .expectError(ProductNameEmptyException.class)
                .verify();
    }

    @Test
    void updateProductNameThrowsErrorWhenNameIsNull() {
        Product product = new Product();
        product.setId(1);
        product.setName(null);

        StepVerifier.create(productCase.updateProductName(product))
                .expectError(ProductNameEmptyException.class)
                .verify();
    }

}