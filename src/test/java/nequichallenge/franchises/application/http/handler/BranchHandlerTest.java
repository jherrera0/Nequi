package nequichallenge.franchises.application.http.handler;
import nequichallenge.franchises.application.http.dto.request.AddBranchDtoRequest;
import nequichallenge.franchises.application.http.dto.response.BranchDtoResponse;
import nequichallenge.franchises.application.http.mapper.IBranchDtoMapper;
import nequichallenge.franchises.domain.api.IBranchServicePort;
import nequichallenge.franchises.domain.model.Branch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchHandlerTest {

    @Mock
    private IBranchServicePort branchService;

    @Mock
    private IBranchDtoMapper branchDtoMapper;

    @InjectMocks
    private BranchHandler branchHandler;

    @Test
    void addBranchReturnsOkResponseWhenValidRequest() {
        AddBranchDtoRequest requestDto = new AddBranchDtoRequest(1, "Branch Name");
        BranchDtoResponse responseDto = new BranchDtoResponse(1, "Branch Name", List.of());
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        when(branchService.addBranch(1, "Branch Name")).
                thenReturn(Mono.just(new Branch(1, "Branch Name", List.of())));
        when(branchDtoMapper.toBranchDto(any(Branch.class))).thenReturn(responseDto);

        StepVerifier.create(branchHandler.addBranch(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void addBranchReturnsBadRequestWhenInvalidRequestBody() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.error(new IllegalArgumentException("Invalid body")));

        StepVerifier.create(branchHandler.addBranch(serverRequest))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Invalid body"))
                .verify();
    }
}