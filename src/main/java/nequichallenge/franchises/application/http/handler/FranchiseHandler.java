package nequichallenge.franchises.application.http.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nequichallenge.franchises.application.http.dto.request.CreateFranchiseDtoRequest;
import nequichallenge.franchises.application.http.mapper.IFranchiseDtoMapper;
import nequichallenge.franchises.domain.api.IFranchiseServicePort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class FranchiseHandler implements IFranchiseHandler {
    private final IFranchiseServicePort franchiseServicePort;
    private final IFranchiseDtoMapper franchiseDtoMapper;

    @Override
    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(CreateFranchiseDtoRequest.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body cannot be empty")))
                .map(franchiseDtoMapper::toDomain)
                .flatMap(franchiseServicePort::createFranchise)
                .map(franchiseDtoMapper::toDtoResponse)
                .flatMap(franchiseDtoResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(franchiseDtoResponse))
                .onErrorResume(
                        error -> {
                            log.error("Error al crear franchise: {}", error.getMessage());
                            return ServerResponse.badRequest()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(error.getMessage());
                        }
                );

    }
}
