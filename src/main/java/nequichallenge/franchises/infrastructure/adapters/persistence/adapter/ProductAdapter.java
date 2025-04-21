package nequichallenge.franchises.infrastructure.adapters.persistence.adapter;

import lombok.RequiredArgsConstructor;
import nequichallenge.franchises.infrastructure.adapters.persistence.entity.ProductEntity;
import nequichallenge.franchises.infrastructure.adapters.persistence.mapper.IProductEntityMapper;
import nequichallenge.franchises.infrastructure.adapters.persistence.repository.IProductRepository;
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

    @Override
    public Mono<Product> findById(Integer id) {
        return productRepository.findById(id)
                .map(productEntityMapper::toProduct);
    }

    @Override
    public Mono<Product> updateProduct(Product existingProduct) {
        return productRepository.findById(existingProduct.getId())
                .flatMap(productOnDb ->{
                    productOnDb.setName(existingProduct.getName());
                    productOnDb.setStock(existingProduct.getStock());
                    productOnDb.setIsActive(existingProduct.getIsActive());
                    return productRepository.save(productOnDb).map(productEntityMapper::toProduct);
                });
    }

    @Override
    public Mono<Product> getTopStockProductsByBranchId(Integer id) {
        return productRepository.findFirstByBranchIdAndIsActiveTrueOrderByStockDesc(id, true)
                .map(productEntityMapper::toProduct);
    }
}
