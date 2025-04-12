package nequichallenge.franchises.domain.usecase;

import nequichallenge.franchises.domain.api.IProductServicePort;
import nequichallenge.franchises.domain.exception.ProductAlreadyExistsException;
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
                    return Mono.error(new ProductAlreadyExistsException());
                });
    }
}
