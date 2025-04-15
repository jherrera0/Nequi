package nequichallenge.franchises.infrastructure.entrypoint.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nequichallenge.franchises.domain.exception.BranchAlreadyExistException;
import nequichallenge.franchises.domain.exception.BranchNameEmptyException;
import nequichallenge.franchises.domain.exception.BranchNotFoundException;
import nequichallenge.franchises.domain.exception.FranchiseNameAlreadyExist;
import nequichallenge.franchises.infrastructure.entrypoint.dto.request.AddBranchDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.dto.request.UpdateNameDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.handler.interfaces.IBranchHandler;
import nequichallenge.franchises.infrastructure.entrypoint.mapper.IBranchDtoMapper;
import nequichallenge.franchises.domain.api.IBranchServicePort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class BranchHandler implements IBranchHandler {
    private final IBranchServicePort branchService;
    private final IBranchDtoMapper branchDtoMapper;
    @Override
    public Mono<ServerResponse> addBranch(ServerRequest request) {
        return request.bodyToMono(AddBranchDtoRequest.class)
                .flatMap(addBranchDtoRequest ->
                        branchService.addBranch(addBranchDtoRequest.getFranchiseId(), addBranchDtoRequest.getName())
                )
                .map(branchDtoMapper::toBranchDto)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response)
                ).onErrorResume(FranchiseNameAlreadyExist.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                ).onErrorResume(BranchAlreadyExistException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                ).onErrorResume(BranchNameEmptyException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                );
    }

    @Override
    public Mono<ServerResponse> updateName(ServerRequest request) {
        return request.bodyToMono(UpdateNameDtoRequest.class)
                .map(branchDtoMapper::toDomain)
                .flatMap(branchService::updateName)
                .map(branchDtoMapper::toBranchCustomDto)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response)
                ).onErrorResume(BranchNameEmptyException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                ).onErrorResume(BranchNotFoundException.class, ex ->
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                );
    }
}
