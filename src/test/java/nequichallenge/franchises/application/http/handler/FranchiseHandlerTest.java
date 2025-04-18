package nequichallenge.franchises.application.http.handler;

import nequichallenge.franchises.application.http.dto.request.CreateFranchiseDtoRequest;
import nequichallenge.franchises.application.http.dto.response.FranchiseDtoResponse;
import nequichallenge.franchises.application.http.mapper.IFranchiseDtoMapper;
import nequichallenge.franchises.domain.api.IFranchiseServicePort;
import nequichallenge.franchises.domain.model.Franchise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class FranchiseHandlerTest {

    @Mock
    private IFranchiseServicePort franchiseServicePort;

    @Mock
    private IFranchiseDtoMapper franchiseDtoMapper;

    @InjectMocks
    private FranchiseHandler franchiseHandler;

    AutoCloseable closeable;
    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("createFranchise")
    class CreateFranchise {

        @Test
        @DisplayName("returns ok response when franchise is created successfully")
        void returnsOkResponseWhenFranchiseIsCreatedSuccessfully() {
            CreateFranchiseDtoRequest requestDto = new CreateFranchiseDtoRequest();
            Franchise domain = new Franchise();
            FranchiseDtoResponse responseDto = new FranchiseDtoResponse();

            when(franchiseDtoMapper.toDomain(requestDto)).thenReturn(domain);
            when(franchiseServicePort.createFranchise(domain)).thenReturn(Mono.just(domain));
            when(franchiseDtoMapper.toDtoResponse(domain)).thenReturn(responseDto);

            ServerRequest serverRequest = mock(ServerRequest.class);
            when(serverRequest.bodyToMono(CreateFranchiseDtoRequest.class)).thenReturn(Mono.just(requestDto));

            Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

            StepVerifier.create(response)
                    .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                    .verifyComplete();

            verify(franchiseDtoMapper).toDomain(requestDto);
            verify(franchiseServicePort).createFranchise(domain);
            verify(franchiseDtoMapper).toDtoResponse(domain);
        }

        @Test
        @DisplayName("returns bad request when request body is empty")
        void returnsBadRequestWhenRequestBodyIsEmpty() {
            ServerRequest serverRequest = mock(ServerRequest.class);
            when(serverRequest.bodyToMono(CreateFranchiseDtoRequest.class)).thenReturn(Mono.empty());

            Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

            StepVerifier.create(response)
                    .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                    .verifyComplete();
        }

        @Test
        @DisplayName("returns bad request when an error occurs during franchise creation")
        void returnsBadRequestWhenErrorOccursDuringFranchiseCreation() {
            CreateFranchiseDtoRequest requestDto = new CreateFranchiseDtoRequest();
            Franchise domain = new Franchise();

            when(franchiseDtoMapper.toDomain(requestDto)).thenReturn(domain);
            when(franchiseServicePort.createFranchise(domain)).thenReturn(Mono.error(new RuntimeException("Error")));

            ServerRequest serverRequest = mock(ServerRequest.class);
            when(serverRequest.bodyToMono(CreateFranchiseDtoRequest.class)).thenReturn(Mono.just(requestDto));

            Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

            StepVerifier.create(response)
                    .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                    .verifyComplete();

            verify(franchiseDtoMapper).toDomain(requestDto);
            verify(franchiseServicePort).createFranchise(domain);
        }
    }
}