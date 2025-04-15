package nequichallenge.franchises.infrastructure.entrypoint.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nequichallenge.franchises.domain.exception.*;
import nequichallenge.franchises.infrastructure.entrypoint.dto.request.CreateFranchiseDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.dto.request.UpdateNameDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.handler.interfaces.IFranchiseHandler;
import nequichallenge.franchises.infrastructure.entrypoint.mapper.IFranchiseDtoMapper;
import nequichallenge.franchises.domain.api.IFranchiseServicePort;
import org.springframework.http.HttpStatus;
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
                .map(franchiseDtoMapper::toDomain)
                .flatMap(franchiseServicePort::createFranchise)
                .map(franchiseDtoMapper::toDtoResponse)
                .flatMap(franchiseDtoResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(franchiseDtoResponse))
                .onErrorResume(FranchiseAlreadyExistsException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                ).onErrorResume(FranchiseNameEmptyException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                );

    }

    @Override
    public Mono<ServerResponse> updateFranchise(ServerRequest request) {
        return request.bodyToMono(UpdateNameDtoRequest.class)
                .map(franchiseDtoMapper::toDomain)
                .flatMap(franchiseServicePort::updateName)
                .map(franchiseDtoMapper::toCustomDtoResponse)
                .flatMap(franchiseDtoResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(franchiseDtoResponse))
                .onErrorResume(FranchiseNameEmptyException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                ).onErrorResume(FranchiseNotFoundException.class, ex ->
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                ).onErrorResume(FranchiseNameAlreadyExist.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                );
    }
}
