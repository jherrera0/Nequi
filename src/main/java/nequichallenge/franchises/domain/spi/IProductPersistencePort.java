package nequichallenge.franchises.domain.spi;

import nequichallenge.franchises.domain.model.Product;
import reactor.core.publisher.Mono;

public interface IProductPersistencePort {
    Mono<Product> createProduct(Integer branchId,Product product);

    Mono<Boolean> existsByName(String name);

}
