package nequichallenge.franchises.domain.api;

import nequichallenge.franchises.domain.model.Product;
import reactor.core.publisher.Mono;

public interface IProductServicePort {
    Mono<Product> createProduct(Integer branchId,Product product);
    Mono<Product> deleteProduct(Product product);
}
