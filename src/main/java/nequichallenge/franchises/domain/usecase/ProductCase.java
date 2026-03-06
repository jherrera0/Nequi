package nequichallenge.franchises.domain.usecase;

import lombok.extern.slf4j.Slf4j;
import nequichallenge.franchises.domain.api.IProductServicePort;
import nequichallenge.franchises.domain.exception.*;
import nequichallenge.franchises.domain.model.Product;
import nequichallenge.franchises.domain.model.ProductTopStock;
import nequichallenge.franchises.domain.spi.IBranchPersistencePort;
import nequichallenge.franchises.domain.spi.IFranchisePersistencePort;
import nequichallenge.franchises.domain.spi.IProductPersistencePort;
import nequichallenge.franchises.domain.util.ConstValidations;
import nequichallenge.franchises.domain.util.ReactiveLogger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class ProductCase implements IProductServicePort {

    private static final int BRANCH_CONCURRENCY = 10;

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
        return validateCreateParams(branchId, product)
                .then(Mono.defer(() -> validateBranchExists(branchId)))
                .then(Mono.defer(() -> validateProductNameAvailable(product.getName())))
                .doOnSuccess(v -> product.setIsActive(true))
                .then(Mono.defer(() -> productPersistencePort.createProduct(branchId, product)))
                .transform(flow -> ReactiveLogger.logResult(log, flow, "Producto creado exitosamente", "Error creando producto"));
    }

    @Override
    public Mono<Product> deleteProduct(Product product) {
        return productPersistencePort.findById(product.getId())
                .filter(Product::getIsActive)
                .switchIfEmpty(Mono.error(new ProductNotFoundException()))
                .map(p -> p.withActiveStatus(false))
                .flatMap(productPersistencePort::updateProduct)
                .transform(flow -> ReactiveLogger.logResult(log, flow, "Producto eliminado exitosamente", "Error eliminando producto"));
    }

    @Override
    public Mono<Product> addProductStock(Product product) {
        return productPersistencePort.findById(product.getId())
                .switchIfEmpty(Mono.error(new ProductNotFoundException()))
                .flatMap(existing -> {
                    if (product.getStock() <= ConstValidations.ZERO) {
                        return Mono.error(new ProductStockInvalidException());
                    }
                    return productPersistencePort.updateProduct(existing.withStock(product.getStock()));
                })
                .transform(flow -> ReactiveLogger.logResult(log, flow, "Stock actualizado exitosamente", "Error actualizando stock"));
    }

    @Override
    public Flux<ProductTopStock> getTopStockProductsByBranchAssociatedToFranchise(Integer franchiseId) {
        return validateFranchiseExists(franchiseId)
                .thenMany(Flux.defer(() -> branchPersistencePort.getBranchesByFranchiseId(franchiseId)))
                .flatMap(branch -> productPersistencePort.getTopStockProductsByBranchId(branch.getId())
                        .map(product -> product.toTopStock(branch)),
                        BRANCH_CONCURRENCY
                );
    }

    @Override
    public Mono<Product> updateProductName(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            return Mono.error(new ProductNameEmptyException());
        }
        return productPersistencePort.findById(product.getId())
                .switchIfEmpty(Mono.error(new ProductNotFoundException()))
                .flatMap(existing -> productPersistencePort.updateProduct(existing.withName(product.getName().trim())))
                .transform(flow -> ReactiveLogger.logResult(log, flow, "Nombre de producto actualizado exitosamente", "Error actualizando nombre de producto"));
    }

    // ==================== VALIDACIONES PRIVADAS ====================

    private Mono<Void> validateCreateParams(Integer branchId, Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
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
        private Mono<Void> validateBranchExists(Integer branchId) {
        return branchPersistencePort.existsById(branchId)
                .filter(exists -> exists)
                .switchIfEmpty(Mono.error(new BranchNotFoundException()))
                .then();
    }

    private Mono<Void> validateProductNameAvailable(String name) {
        return productPersistencePort.existsByName(name)
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new ProductAlreadyExistsException()))
                .then();
    }

    private Mono<Void> validateFranchiseExists(Integer franchiseId) {
        return franchisePersistencePort.franchiseExistsById(franchiseId)
                .filter(exists -> exists)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
                .then();
    }
}
