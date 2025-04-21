package nequichallenge.franchises.domain.api;

import nequichallenge.franchises.domain.model.Product;
import nequichallenge.franchises.domain.model.ProductTopStock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductServicePort {
    Mono<Product> createProduct(Integer branchId,Product product);
    Mono<Product> deleteProduct(Product product);
    Mono<Product> addProductStock(Product product);

    Flux<ProductTopStock> getTopStockProductsByBranchAssociatedToFranchise(Integer franchiseId);

    Mono<Product> updateProductName(Product product);
}
