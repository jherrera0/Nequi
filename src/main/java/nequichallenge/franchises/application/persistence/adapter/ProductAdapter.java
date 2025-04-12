package nequichallenge.franchises.application.persistence.adapter;

import lombok.RequiredArgsConstructor;
import nequichallenge.franchises.application.persistence.entity.ProductEntity;
import nequichallenge.franchises.application.persistence.mapper.IProductEntityMapper;
import nequichallenge.franchises.application.persistence.repository.IProductRepository;
import nequichallenge.franchises.domain.model.Product;
import nequichallenge.franchises.domain.spi.IProductPersistencePort;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductAdapter implements IProductPersistencePort {
    private final IProductRepository productRepository;
    private final IProductEntityMapper productEntityMapper;

    @Override
    public Mono<Product> createProduct(Integer branchId, Product product) {
        ProductEntity productEntity = productEntityMapper.toProductEntity(product);
        productEntity.setBranchId(branchId);
        return productRepository.save(productEntity)
                .map(productEntityMapper::toProduct);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return productRepository.existsByName(name);
    }
}
