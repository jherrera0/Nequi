package nequichallenge.franchises.application.http.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nequichallenge.franchises.application.http.dto.request.AddBranchDtoRequest;
import nequichallenge.franchises.application.http.handler.interfaces.IBranchHandler;
import nequichallenge.franchises.application.http.mapper.IBranchDtoMapper;
import nequichallenge.franchises.domain.api.IBranchServicePort;
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
                );
    }
}
