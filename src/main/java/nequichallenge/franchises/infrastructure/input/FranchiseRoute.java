package nequichallenge.franchises.infrastructure.input;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import nequichallenge.franchises.application.http.handler.IFranchiseHandler;
import nequichallenge.franchises.domain.util.ConstRoute;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class FranchiseRoute {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/franchise/create",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = IFranchiseHandler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Crear una nueva franquicia",
                            description = "Crea una nueva franquicia en el sistema",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            examples = {
                                                    @ExampleObject(
                                                            name = "FranquiciaEjemplo",
                                                            summary = "Ejemplo de franquicia",
                                                            value = "Franquicia Caribe"
                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Franquicia creada exitosamente",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    examples = @ExampleObject(
                                                            name = "FranquiciaCreada",
                                                            summary = "Respuesta exitosa",
                                                            value = """
                                    {
                                      "name": "Franquicia Caribe"
                                    }
                                    """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Solicitud inválida debido a datos incorrectos o " +
                                                    "a incumplimiento de restricciones de negocio"

                                    )
                            }
                    )
            )
    })

    public RouterFunction<ServerResponse> franchiseRoutes(IFranchiseHandler franchiseHandler) {
        return route(POST(ConstRoute.FRANCHISE+ConstRoute.CREATE),
                franchiseHandler::createFranchise);
    }
}
