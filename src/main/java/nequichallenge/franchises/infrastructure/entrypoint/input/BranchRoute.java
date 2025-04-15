package nequichallenge.franchises.infrastructure.entrypoint.input;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import nequichallenge.franchises.infrastructure.entrypoint.handler.interfaces.IBranchHandler;
import nequichallenge.franchises.domain.util.ConstRoute;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BranchRoute {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/branch/addBranch",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = IBranchHandler.class,
                    beanMethod = "addBranch",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Crear una nueva sucursal",
                            description = "Crea una nueva sucursal asociada a una franquicia en el sistema",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            examples = {
                                                    @ExampleObject(
                                                            name = "Sucursal Ejemplo",
                                                            summary = "Ejemplo de sucursal",
                                                            value = """
                                                                    {
                                                                      "franchiseId": 1,
                                                                      "name": "Sucursal Cartagena"
                                                                    }
                                                                    """
                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Sucursal creada exitosamente",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    examples = @ExampleObject(
                                                            name = "Sucursal Creada",
                                                            summary = "Respuesta exitosa",
                                                            value = """
                                                                    {
                                                                      "franchiseId": 1,
                                                                      "name": "Sucursal Cartagena"
                                                                    }
                                                                    """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Solicitud inválida debido a datos incorrectos o a incumplimiento de restricciones de negocio"
                                    )
                            }
                    )
            ), @RouterOperation(
            path = "/branch/updateName",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.POST,
            beanClass = IBranchHandler.class,
            beanMethod = "updateName",
            operation = @Operation(
                    operationId = "updateBranchName",
                    summary = "Actualizar el nombre de una sucursal",
                    description = "Actualiza el nombre de una sucursal existente mediante su ID",
                    requestBody = @RequestBody(
                            required = true,
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "Actualizar Nombre",
                                                    summary = "Ejemplo de actualización de nombre de sucursal",
                                                    value = """
                                                            {
                                                              "id": 1,
                                                              "name": "Sucursal Bocagrande"
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Nombre de sucursal actualizado exitosamente",
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            examples = @ExampleObject(
                                                    name = "Sucursal Actualizada",
                                                    summary = "Respuesta exitosa",
                                                    value = """
                                                            {
                                                              "id": 1,
                                                              "name": "Sucursal Bocagrande"
                                                            }
                                                            """
                                            )
                                    )
                            ),
                            @ApiResponse(
                                    responseCode = "404",
                                    description = "Sucursal no encontrada"
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "Datos inválidos"
                            )
                    }
            )
    )
    })

    public RouterFunction<ServerResponse> branchRoutes(IBranchHandler branchHandler) {
        return route(POST(ConstRoute.BRANCH_REST_ROUTE + ConstRoute.ADD_BRANCH_REST_ROUTE),
                branchHandler::addBranch)
                .andRoute(POST(ConstRoute.BRANCH_REST_ROUTE + ConstRoute.UPDATE_NAME),
                        branchHandler::updateName);
    }
}
