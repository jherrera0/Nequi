package nequichallenge.franchises.domain.usecase;

import nequichallenge.franchises.domain.api.IProductServicePort;
import nequichallenge.franchises.domain.exception.*;
import nequichallenge.franchises.domain.model.Product;
import nequichallenge.franchises.domain.model.ProductTopStock;
import nequichallenge.franchises.domain.spi.IBranchPersistencePort;
import nequichallenge.franchises.domain.spi.IFranchisePersistencePort;
import nequichallenge.franchises.domain.spi.IProductPersistencePort;
import nequichallenge.franchises.domain.util.ConstValidations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ProductCase implements IProductServicePort {
    private final IProductPersistencePort productPersistencePort;
    private final IBranchPersistencePort branchPersistencePort;
    private final IFranchisePersistencePort franchisePersistencePort;

    public ProductCase(IProductPersistencePort productPersistencePort,
                       IBranchPersistencePort branchPersistencePort,
                       IFranchisePersistencePort franchisePersistencePort) {
        this.productPersistencePort = productPersistencePort;
        this.branchPersistencePort = branchPersistencePort;
        this.franchisePersistencePort = franchisePersistencePort;
    }

    @Override
    public Mono<Product> createProduct(Integer branchId, Product product) {
        return validateParams(branchId, product)
                .switchIfEmpty(Mono.just(product))
                .flatMap(validProduct -> {
                    validProduct.setIsActive(true);
                    return branchPersistencePort.existsById(branchId)
                            .filter(branchExists -> branchExists.compareTo(Boolean.TRUE) == ConstValidations.ZERO)
                            .switchIfEmpty(Mono.error(new BranchNotFoundException()))
                            .flatMap(branchValidated ->
                                    productPersistencePort.existsByName(validProduct.getName())
                                            .filter(productNotExists -> productNotExists.compareTo(Boolean.FALSE)
                                                    == ConstValidations.ZERO)
                                            .switchIfEmpty(Mono.error(new ProductAlreadyExistsException()))
                                            .flatMap(productValidated ->
                                                    productPersistencePort.createProduct(branchId, validProduct)
                                            )
                            );
                });
    }


    @Override
    public Mono<Product> deleteProduct(Product product) {
        return productPersistencePort.findById(product.getId())
                .filter(Product::getIsActive)
                .switchIfEmpty(Mono.error(new ProductNotFoundException()))
                .flatMap(existingProduct -> {
                    existingProduct.setIsActive(false);
                    return productPersistencePort.updateProduct(existingProduct);
                });
    }

    @Override
    public Mono<Product> addProductStock(Product product) {
        return productPersistencePort.findById(product.getId())
                .switchIfEmpty(Mono.error(new ProductNotFoundException()))
                .filter(existingProduct -> product.getStock() > ConstValidations.ZERO)
                .switchIfEmpty(Mono.error(new ProductStockInvalidException()))
                .flatMap(existingProduct -> {
                    existingProduct.setStock(product.getStock());
                    return productPersistencePort.updateProduct(existingProduct);
                });
    }


    @Override
    public Flux<ProductTopStock> getTopStockProductsByBranchAssociatedToFranchise(Integer franchiseId) {
        return franchisePersistencePort.franchiseExistsById(franchiseId)
                .filter(exists -> exists.compareTo(Boolean.TRUE) == ConstValidations.ZERO)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
                .flatMapMany(valid -> branchPersistencePort.getBranchesByFranchiseId(franchiseId))
                .flatMap(branch -> productPersistencePort.getTopStockProductsByBranchId(branch.getId())
                        .map(product -> new ProductTopStock(
                                branch.getId(),
                                branch.getName(),
                                product.getId(),
                                product.getName(),
                                product.getStock()
                        ))
                );
    }

    @Override
    public Mono<Product> updateProductName(Product product) {
        return Mono.justOrEmpty(product.getName())
                .filter(name -> !name.isEmpty())
                .switchIfEmpty(Mono.error(new ProductNameEmptyException()))
                .flatMap(validName -> productPersistencePort.findById(product.getId())
                        .switchIfEmpty(Mono.error(new ProductNotFoundException()))
                        .flatMap(existingProduct -> {
                            existingProduct.setName(validName);
                            return productPersistencePort.updateProduct(existingProduct);
                        })
                );
    }


    private static Mono<Product> validateParams(Integer branchId, Product product) {
        if (product.getName() == null || product.getName().isEmpty()) {
            return Mono.error(new ProductNameEmptyException());
        }
        if (product.getStock() == null || product.getStock() <= ConstValidations.ZERO) {
            return Mono.error(new ProductStockInvalidException());
        }
        if (branchId == null || branchId <= ConstValidations.ZERO) {
            return Mono.error(new BranchIdInvalidException());
        }
        return Mono.empty();
    }

}
