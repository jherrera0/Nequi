package nequichallenge.franchises.application.http.handler.interfaces;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface IProductHandler {
    Mono<ServerResponse> createProduct(ServerRequest request);
}
