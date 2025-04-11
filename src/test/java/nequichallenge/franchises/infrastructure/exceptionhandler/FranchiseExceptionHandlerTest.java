package nequichallenge.franchises.infrastructure.exceptionhandler;

import nequichallenge.franchises.domain.exception.FranchiseAlreadyExistsException;
import nequichallenge.franchises.domain.util.ConstExceptions;
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

class FranchiseExceptionHandlerTest {

    private FranchiseExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new FranchiseExceptionHandler();
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
        assertThat(body).contains("Ocurrió un error inesperado")
                .contains("\"status\": 500");
    }
}
