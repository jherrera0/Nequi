package nequichallenge.franchises.infrastructure.entrypoint.handler.interfaces;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface IProductHandler {
    Mono<ServerResponse> createProduct(ServerRequest request);
    Mono<ServerResponse> deleteProduct(ServerRequest request);
    Mono<ServerResponse> addProductStock(ServerRequest request);
    Mono<ServerResponse> getTopStockProductsByBranchAssociatedToFranchise(ServerRequest request);
    Mono<ServerResponse> updateProductName(ServerRequest request);
}
