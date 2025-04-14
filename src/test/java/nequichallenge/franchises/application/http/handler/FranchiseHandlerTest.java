package nequichallenge.franchises.application.http.handler;

import nequichallenge.franchises.application.http.dto.request.CreateFranchiseDtoRequest;
import nequichallenge.franchises.application.http.dto.request.UpdateNameDtoRequest;
import nequichallenge.franchises.application.http.dto.response.FranchiseCustomDtoResponse;
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
    }

    @Test
    @DisplayName("returns ok response when franchise is updated successfully")
    void returnsOkResponseWhenFranchiseIsUpdatedSuccessfully() {
        UpdateNameDtoRequest requestDto = new UpdateNameDtoRequest();
        Franchise domain = new Franchise();
        FranchiseCustomDtoResponse responseDto = new FranchiseCustomDtoResponse();

        when(franchiseDtoMapper.toDomain(requestDto)).thenReturn(domain);
        when(franchiseServicePort.updateName(domain)).thenReturn(Mono.just(domain));
        when(franchiseDtoMapper.toCustomDtoResponse(domain)).thenReturn(responseDto);

        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(UpdateNameDtoRequest.class)).thenReturn(Mono.just(requestDto));

        Mono<ServerResponse> response = franchiseHandler.updateFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(franchiseDtoMapper).toDomain(requestDto);
        verify(franchiseServicePort).updateName(domain);
        verify(franchiseDtoMapper).toCustomDtoResponse(domain);
    }
}