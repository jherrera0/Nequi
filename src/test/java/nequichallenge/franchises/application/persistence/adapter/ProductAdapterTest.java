package nequichallenge.franchises.application.persistence.adapter;

import static org.mockito.Mockito.*;

import nequichallenge.franchises.application.persistence.entity.ProductEntity;
import nequichallenge.franchises.application.persistence.mapper.IProductEntityMapper;
import nequichallenge.franchises.application.persistence.repository.IProductRepository;
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
}