package nequichallenge.franchises.infrastructure.entrypoint.input;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import nequichallenge.franchises.domain.util.ConstRoute;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
@Configuration
public class HealthCheckRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/health",
                    produces = {MediaType.TEXT_PLAIN_VALUE},
                    method = RequestMethod.GET,
                    beanClass = HealthCheckRouter.class,
                    beanMethod = "healthCheck",
                    operation = @Operation(
                            operationId = "healthCheck",
                            summary = "Verificación de estado del servicio",
                            description = "Endpoint utilizado por ALB o herramientas de monitoreo para verificar que el servicio esté en funcionamiento.",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "El servicio está saludable",
                                            content = @Content(
                                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                                    examples = @ExampleObject(
                                                            name = "RespuestaExitosaHealthCheck",
                                                            summary = "Respuesta exitosa",
                                                            value = "OK"
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error inesperado al verificar el estado del servicio",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    examples = @ExampleObject(
                                                            name = "ErrorInternoHealthCheck",
                                                            summary = "Error interno",
                                                            value = """
                                        {
                                          "status": 500,
                                          "error": "Internal Server Error",
                                          "message": "Unexpected error during health check",
                                          "path": "/health"
                                        }
                                        """
                                                    )
                                            )
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> healthRoute() {
        return  route(GET(ConstRoute.HEALTH_CHECK), request -> ServerResponse.ok().bodyValue("OK"));
    }
}
