package nequichallenge.franchises.infrastructure.exceptionhandler;

import nequichallenge.franchises.domain.exception.BranchAlreadyExistException;
import nequichallenge.franchises.domain.exception.BranchNameEmptyException;
import nequichallenge.franchises.domain.exception.FranchiseAlreadyExistsException;
import nequichallenge.franchises.domain.exception.FranchiseNotFoundException;
import nequichallenge.franchises.domain.util.ConstExceptions;
import nequichallenge.franchises.domain.util.ConstRoute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldReturnBadRequestWhenFranchiseAlreadyExistsException() {
        // Given
        FranchiseAlreadyExistsException ex = new FranchiseAlreadyExistsException();
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/franchise/create"));

        // When
        Mono<Void> result = handler.handle(exchange, ex);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        MockServerHttpResponse response = (MockServerHttpResponse) exchange.getResponse();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        String body = response.getBodyAsString().block();
        assertThat(body).contains(ConstExceptions.FRANCHISE_ALREADY_EXISTS)
                .contains("\"status\": 400");
    }
    @Test
    void shouldReturnBadRequestWhenBranchAlreadyExistsException() {
        // Given
        BranchAlreadyExistException ex = new BranchAlreadyExistException();
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(
                ConstRoute.BRANCH_REST_ROUTE+ConstRoute.ADD_BRANCH_REST_ROUTE));

        // When
        Mono<Void> result = handler.handle(exchange, ex);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        MockServerHttpResponse response = (MockServerHttpResponse) exchange.getResponse();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        String body = response.getBodyAsString().block();
        assertThat(body).contains(ConstExceptions.BRANCH_ALREADY_EXIST)
                .contains("\"status\": 400");
    }

    @Test
    void shouldReturnBadRequestWhenBranchNameEmptyException() {
        // Given
        BranchNameEmptyException ex = new BranchNameEmptyException();
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(
                ConstRoute.BRANCH_REST_ROUTE+ConstRoute.ADD_BRANCH_REST_ROUTE));

        // When
        Mono<Void> result = handler.handle(exchange, ex);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        MockServerHttpResponse response = (MockServerHttpResponse) exchange.getResponse();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        String body = response.getBodyAsString().block();
        assertThat(body).contains(ConstExceptions.BRANCH_NAME_EMPTY)
                .contains("\"status\": 400");
    }

    @Test
    void ShouldReturnNotFoundWhenFranchiseNotFoundException() {
        // Given
        FranchiseNotFoundException ex = new FranchiseNotFoundException();
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/franchise"));

        // When
        Mono<Void> result = handler.handle(exchange, ex);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        MockServerHttpResponse response = (MockServerHttpResponse) exchange.getResponse();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        String body = response.getBodyAsString().block();
        assertThat(body).contains(ConstExceptions.FRANCHISE_NOT_FOUND)
                .contains("\"status\": 404");
    }

    @Test
    void shouldReturnInternalServerErrorForUnhandledExceptions() {
        // Given
        RuntimeException ex = new RuntimeException("Error interno");
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/franchise"));

        // When
        Mono<Void> result = handler.handle(exchange, ex);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        MockServerHttpResponse response = (MockServerHttpResponse) exchange.getResponse();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        String body = response.getBodyAsString().block();
        assertThat(body).contains("Internal Server Error")
                .contains("\"status\": 500");
    }
}
