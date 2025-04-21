package nequichallenge.franchises.infrastructure.entrypoint.handler;

import nequichallenge.franchises.domain.exception.FranchiseAlreadyExistsException;
import nequichallenge.franchises.domain.exception.FranchiseNameAlreadyExist;
import nequichallenge.franchises.domain.exception.FranchiseNameEmptyException;
import nequichallenge.franchises.domain.exception.FranchiseNotFoundException;
import nequichallenge.franchises.infrastructure.entrypoint.dto.request.CreateFranchiseDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.dto.request.UpdateNameDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.dto.response.FranchiseCustomDtoResponse;
import nequichallenge.franchises.infrastructure.entrypoint.dto.response.FranchiseDtoResponse;
import nequichallenge.franchises.infrastructure.entrypoint.mapper.IFranchiseDtoMapper;
import nequichallenge.franchises.domain.api.IFranchiseServicePort;
import nequichallenge.franchises.domain.model.Franchise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
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

    @Test
    @DisplayName("returns not found when updating a non-existent franchise")
    void returnsNotFoundWhenUpdatingNonExistentFranchise() {
        UpdateNameDtoRequest requestDto = new UpdateNameDtoRequest();
        Franchise domain = new Franchise();
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(UpdateNameDtoRequest.class)).thenReturn(Mono.just(requestDto));
        when(franchiseDtoMapper.toDomain(requestDto)).thenReturn(domain);
        when(franchiseServicePort.updateName(domain)).thenReturn(Mono.error(new FranchiseNotFoundException()));

        Mono<ServerResponse> response = franchiseHandler.updateFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.NOT_FOUND))
                .verifyComplete();

        verify(franchiseDtoMapper).toDomain(requestDto);
        verify(franchiseServicePort).updateName(domain);
        verify(franchiseDtoMapper, never()).toCustomDtoResponse(any());
    }

    @Test
    @DisplayName("returns bad request when franchise name already exists during update")
    void returnsBadRequestWhenFranchiseNameAlreadyExistsDuringUpdate() {
        UpdateNameDtoRequest requestDto = new UpdateNameDtoRequest();
        Franchise domain = new Franchise();
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(UpdateNameDtoRequest.class)).thenReturn(Mono.just(requestDto));
        when(franchiseDtoMapper.toDomain(requestDto)).thenReturn(domain);
        when(franchiseServicePort.updateName(domain)).thenReturn(Mono.error(new FranchiseNameAlreadyExist()));

        Mono<ServerResponse> response = franchiseHandler.updateFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();

        verify(franchiseDtoMapper).toDomain(requestDto);
        verify(franchiseServicePort).updateName(domain);
        verify(franchiseDtoMapper, never()).toCustomDtoResponse(any());
    }
    @Test
    @DisplayName("returns bad request when franchise name is empty during creation")
    void returnsBadRequestWhenFranchiseNameIsEmptyDuringCreation() {
        CreateFranchiseDtoRequest requestDto = new CreateFranchiseDtoRequest();
        requestDto.setName("");
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(CreateFranchiseDtoRequest.class)).thenReturn(Mono.just(requestDto));
        when(franchiseDtoMapper.toDomain(requestDto)).thenThrow(new FranchiseNameEmptyException());

        Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();

        verify(franchiseDtoMapper).toDomain(requestDto);
        verify(franchiseServicePort, never()).createFranchise(any());
        verify(franchiseDtoMapper, never()).toDtoResponse(any());
    }

    @Test
    @DisplayName("returns bad request when franchise already exists during creation")
    void returnsBadRequestWhenFranchiseAlreadyExistsDuringCreation() {
        CreateFranchiseDtoRequest requestDto = new CreateFranchiseDtoRequest();
        requestDto.setName("Existing Franchise");
        Franchise domain = new Franchise();
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(CreateFranchiseDtoRequest.class)).thenReturn(Mono.just(requestDto));
        when(franchiseDtoMapper.toDomain(requestDto)).thenReturn(domain);
        when(franchiseServicePort.createFranchise(domain)).thenReturn(Mono.error(new FranchiseAlreadyExistsException()));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();

        verify(franchiseDtoMapper).toDomain(requestDto);
        verify(franchiseServicePort).createFranchise(domain);
        verify(franchiseDtoMapper, never()).toDtoResponse(any());
    }
    @Test
    @DisplayName("returns bad request when franchise name is null during update")
    void returnsBadRequestWhenFranchiseNameIsNullDuringUpdate() {
        UpdateNameDtoRequest requestDto = new UpdateNameDtoRequest();
        requestDto.setName(null);
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(UpdateNameDtoRequest.class)).thenReturn(Mono.just(requestDto));
        when(franchiseDtoMapper.toDomain(requestDto)).thenThrow(new FranchiseNameEmptyException());

        Mono<ServerResponse> response = franchiseHandler.updateFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();

        verify(franchiseDtoMapper).toDomain(requestDto);
        verify(franchiseServicePort, never()).updateName(any());
        verify(franchiseDtoMapper, never()).toCustomDtoResponse(any());
    }
}