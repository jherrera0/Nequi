package nequichallenge.franchises.infrastructure.input;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import nequichallenge.franchises.application.http.handler.interfaces.IProductHandler;
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
public class ProductRoute {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/product/create",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = IProductHandler.class,
                    beanMethod = "createProduct",
                    operation = @Operation(
                            operationId = "createProduct",
                            summary = "Crear un nuevo producto",
                            description = "Crea un nuevo producto asociado a una sucursal existente",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            examples = {
                                                    @ExampleObject(
                                                            name = "ProductoEjemplo",
                                                            summary = "Ejemplo de producto",
                                                            value = """
                                                                {
                                                                  "branchId": 1,
                                                                  "name": "Latte",
                                                                  "stock": 10
                                                                }
                                                                """
                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Producto creado exitosamente",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    examples = @ExampleObject(
                                                            name = "ProductoCreado",
                                                            summary = "Respuesta exitosa",
                                                            value = """
                                                                {
                                                                  "branchId": 1,
                                                                  "name": "Latte",
                                                                  "price": 5
                                                                }
                                                                """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Solicitud inválida debido a datos incorrectos o restricciones de negocio",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    examples = @ExampleObject(
                                                            name = "ErrorSolicitudInvalida",
                                                            summary = "Error en la solicitud",
                                                            value = """
                                                                {
                                                                  "status": 400,
                                                                  "error": "Bad Request",
                                                                  "message": "Product already exists or invalid input",
                                                                  "path": "/product/create"
                                                                }
                                                                """
                                                    )
                                            )
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> productRoutes(IProductHandler productHandler) {
        return route(POST(ConstRoute.PRODUCT+ConstRoute.CREATE), productHandler::createProduct);
    }
}
