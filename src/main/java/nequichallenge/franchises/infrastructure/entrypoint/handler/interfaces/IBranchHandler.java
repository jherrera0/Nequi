package nequichallenge.franchises.infrastructure.entrypoint.handler.interfaces;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface IBranchHandler {
    Mono<ServerResponse> addBranch(ServerRequest request);

    Mono<ServerResponse> updateName(ServerRequest request);
}
