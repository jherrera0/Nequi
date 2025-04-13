package nequichallenge.franchises.domain.usecase;

import nequichallenge.franchises.domain.api.IProductServicePort;
import nequichallenge.franchises.domain.exception.*;
import nequichallenge.franchises.domain.model.Product;
import nequichallenge.franchises.domain.spi.IBranchPersistencePort;
import nequichallenge.franchises.domain.spi.IProductPersistencePort;
import nequichallenge.franchises.domain.util.ConstValidations;
import reactor.core.publisher.Mono;

public class ProductCase implements IProductServicePort {
    private final IProductPersistencePort productPersistencePort;
    private final IBranchPersistencePort branchPersistencePort;

    public ProductCase(IProductPersistencePort productPersistencePort,
                       IBranchPersistencePort branchPersistencePort) {
        this.productPersistencePort = productPersistencePort;
        this.branchPersistencePort = branchPersistencePort;
    }

    @Override
    public Mono<Product> createProduct(Integer branchId,Product product) {
        Mono<Product> error = validateParams(branchId, product);
        if (error != null) return error;
        product.setIsActive(true);
        return branchPersistencePort.existsById(branchId)
                .flatMap(exists -> {
                    if (exists.compareTo(Boolean.TRUE) == ConstValidations.ZERO) {
                        return productPersistencePort.existsByName(product.getName())
                                .flatMap(existsProduct -> {
                                    if (existsProduct.compareTo(Boolean.TRUE) == ConstValidations.ZERO) {
                                        return Mono.error(new ProductAlreadyExistsException());
                                    }
                                    return productPersistencePort.createProduct(branchId, product);
                                });
                    }
                    return Mono.error(new BranchNotFoundException());
                });
    }

    @Override
    public Mono<Product> deleteProduct(Product product) {
        return productPersistencePort.findById(product.getId())
                .flatMap(existingProduct -> {
                    existingProduct.setIsActive(false);
                    return productPersistencePort.updateProduct(existingProduct);
                })
                .switchIfEmpty(Mono.error(new ProductNotFoundException()));
    }

    private static Mono<Product> validateParams(Integer branchId, Product product) {
        if (product.getName() == null || product.getName().isEmpty()) {
            return Mono.error(new ProductNameEmptyException());
        }
        if (product.getStock() == null || product.getStock() <= ConstValidations.ZERO) {
            return Mono.error(new ProductStockInvalidException());
        }
        if(branchId == null|| branchId <= ConstValidations.ZERO) {
            return Mono.error(new BranchIdInvalidException());
        }
        return null;
    }
}
