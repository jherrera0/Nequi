package nequichallenge.franchises.infrastructure.exceptionhandler;

import lombok.AllArgsConstructor;
import nequichallenge.franchises.domain.exception.FranchiseAlreadyExistsException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@AllArgsConstructor
@Order(-2)
public class FranchiseExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        int status;
        String message;

        if (ex instanceof FranchiseAlreadyExistsException) {
            status = HttpStatus.BAD_REQUEST.value();
            message = ex.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            message = "Ocurrió un error inesperado";
        }

        String path = exchange.getRequest().getPath().value();
        String json = """
            {
              "status": %d,
              "error": "%s",
              "message": "%s",
              "path": "%s"
            }
            """.formatted(
                status,
                HttpStatus.valueOf(status).getReasonPhrase(),
                message,
                path
        );

        exchange.getResponse().setStatusCode(HttpStatus.valueOf(status));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8))));
    }

}
