package nequichallenge.franchises.infrastructure.adapters.persistence.adapter;

import static org.mockito.Mockito.*;

import nequichallenge.franchises.infrastructure.adapters.persistence.entity.ProductEntity;
import nequichallenge.franchises.infrastructure.adapters.persistence.mapper.IProductEntityMapper;
import nequichallenge.franchises.infrastructure.adapters.persistence.repository.IProductRepository;
import nequichallenge.franchises.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ProductAdapterTest {

    private IProductRepository productRepository;
    private IProductEntityMapper productEntityMapper;
    private ProductAdapter productAdapter;

    @BeforeEach
    void setUp() {
        productRepository = mock(IProductRepository.class);
        productEntityMapper = mock(IProductEntityMapper.class);
        productAdapter = new ProductAdapter(productRepository, productEntityMapper);
    }

    @Nested
    @DisplayName("createProduct")
    class CreateProduct {

        @Test
        @DisplayName("creates a product successfully")
        void createsProductSuccessfully() {
            Product product = new Product(null, "Description1", 100);
            ProductEntity productEntity = new ProductEntity();
            productEntity.setName("Product1");
            productEntity.setStock(100);
            productEntity.setBranchId(1);

            when(productEntityMapper.toProductEntity(product)).thenReturn(productEntity);
            when(productRepository.save(productEntity)).thenReturn(Mono.just(productEntity));
            when(productEntityMapper.toProduct(productEntity)).thenReturn(product);

            StepVerifier.create(productAdapter.createProduct(1, product))
                    .expectNext(product)
                    .verifyComplete();

            verify(productEntityMapper).toProductEntity(product);
            verify(productRepository).save(productEntity);
            verify(productEntityMapper).toProduct(productEntity);
        }

    }

    @Nested
    @DisplayName("existsByName")
    class ExistsByName {

        @Test
        @DisplayName("returns true when product exists by name")
        void returnsTrueWhenProductExistsByName() {
            when(productRepository.existsByName("Product1")).thenReturn(Mono.just(true));

            StepVerifier.create(productAdapter.existsByName("Product1"))
                    .expectNext(true)
                    .verifyComplete();

            verify(productRepository).existsByName("Product1");
        }

        @Test
        @DisplayName("returns false when product does not exist by name")
        void returnsFalseWhenProductDoesNotExistByName() {
            when(productRepository.existsByName("Product1")).thenReturn(Mono.just(false));

            StepVerifier.create(productAdapter.existsByName("Product1"))
                    .expectNext(false)
                    .verifyComplete();

            verify(productRepository).existsByName("Product1");
        }
    }
    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("returns product when found by id")
        void returnsProductWhenFoundById() {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setId(1);
            productEntity.setName("Product1");
            productEntity.setStock(100);
            productEntity.setBranchId(1);

            Product product = new Product(1, "Product1", 100);

            when(productRepository.findById(1)).thenReturn(Mono.just(productEntity));
            when(productEntityMapper.toProduct(productEntity)).thenReturn(product);

            StepVerifier.create(productAdapter.findById(1))
                    .expectNext(product)
                    .verifyComplete();

            verify(productRepository).findById(1);
            verify(productEntityMapper).toProduct(productEntity);
        }

        @Test
        @DisplayName("returns empty when product not found by id")
        void returnsEmptyWhenProductNotFoundById() {
            when(productRepository.findById(1)).thenReturn(Mono.empty());

            StepVerifier.create(productAdapter.findById(1))
                    .verifyComplete();

            verify(productRepository).findById(1);
            verifyNoInteractions(productEntityMapper);
        }
    }

    @Nested
    @DisplayName("updateProduct")
    class UpdateProduct {

        @Test
        @DisplayName("updates product successfully")
        void updatesProductSuccessfully() {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setId(1);
            productEntity.setName("Product1");
            productEntity.setStock(100);
            productEntity.setBranchId(1);
            productEntity.setIsActive(false);

            Product existingProduct = new Product(1, "Product1", 100);
            existingProduct.setIsActive(true);

            Product updatedProduct = new Product(1, "Product1", 100);
            updatedProduct.setIsActive(true);

            when(productRepository.findById(1)).thenReturn(Mono.just(productEntity));
            when(productRepository.save(productEntity)).thenReturn(Mono.just(productEntity));
            when(productEntityMapper.toProduct(productEntity)).thenReturn(updatedProduct);

            StepVerifier.create(productAdapter.updateProduct(existingProduct))
                    .expectNext(updatedProduct)
                    .verifyComplete();

            verify(productRepository).findById(1);
            verify(productRepository).save(productEntity);
            verify(productEntityMapper).toProduct(productEntity);
        }

        @Test
        @DisplayName("returns empty when product to update not found")
        void returnsEmptyWhenProductToUpdateNotFound() {
            Product existingProduct = new Product(1, "Product1", 100);
            existingProduct.setIsActive(true);

            when(productRepository.findById(1)).thenReturn(Mono.empty());

            StepVerifier.create(productAdapter.updateProduct(existingProduct))
                    .verifyComplete();

            verify(productRepository).findById(1);
            verifyNoMoreInteractions(productRepository);
            verifyNoInteractions(productEntityMapper);
        }
    }
    @Nested
    @DisplayName("getTopStockProductsByBranchId")
    class GetTopStockProductsByBranchId {

        @Test
        @DisplayName("returns product with highest stock for a given branch id")
        void returnsProductWithHighestStockForGivenBranchId() {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setId(1);
            productEntity.setName("Product1");
            productEntity.setStock(200);
            productEntity.setBranchId(1);
            productEntity.setIsActive(true);

            Product product = new Product(1, "Product1", 200);

            when(productRepository.findFirstByBranchIdAndIsActiveTrueOrderByStockDesc(1, true))
                    .thenReturn(Mono.just(productEntity));
            when(productEntityMapper.toProduct(productEntity)).thenReturn(product);

            StepVerifier.create(productAdapter.getTopStockProductsByBranchId(1))
                    .expectNext(product)
                    .verifyComplete();

            verify(productRepository).findFirstByBranchIdAndIsActiveTrueOrderByStockDesc(1, true);
            verify(productEntityMapper).toProduct(productEntity);
        }

        @Test
        @DisplayName("returns empty when no active products exist for a given branch id")
        void returnsEmptyWhenNoActiveProductsExistForGivenBranchId() {
            when(productRepository.findFirstByBranchIdAndIsActiveTrueOrderByStockDesc(1, true))
                    .thenReturn(Mono.empty());

            StepVerifier.create(productAdapter.getTopStockProductsByBranchId(1))
                    .verifyComplete();

            verify(productRepository).findFirstByBranchIdAndIsActiveTrueOrderByStockDesc(1, true);
            verifyNoInteractions(productEntityMapper);
        }
    }
}