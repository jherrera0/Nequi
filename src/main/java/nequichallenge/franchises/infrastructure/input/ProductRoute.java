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
                                                                  "id": 10,
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
            ),
            @RouterOperation(
                    path = "/product/delete",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = IProductHandler.class,
                    beanMethod = "deleteProduct",
                    operation = @Operation(
                            operationId = "deleteProduct",
                            summary = "Eliminar producto",
                            description = "Elimina un producto existente del sistema usando su ID",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            examples = {
                                                    @ExampleObject(
                                                            name = "EliminarProductoEjemplo",
                                                            summary = "Ejemplo de solicitud para eliminar producto",
                                                            value = """
                                                                {
                                                                  "id": 10
                                                                }
                                                                """
                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "204",
                                            description = "Producto eliminado exitosamente",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    examples = @ExampleObject(
                                                            name = "ErrorEliminacionProducto",
                                                            summary = "Error al eliminar producto",
                                                            value = """
                                                                {
                                                                    "id": 1,
                                                                    "name": "1",
                                                                    "stock": 10,
                                                                    "isActive": false
                                                                }
                                                                """
                                                    )
                                            )

                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Solicitud inválida debido a datos incorrectos",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    examples = @ExampleObject(
                                                            name = "ErrorEliminacionProducto",
                                                            summary = "Error al eliminar producto",
                                                            value = """
                                                                {
                                                                  "status": 400,
                                                                  "error": "Bad Request",
                                                                  "message": "Product does not exist",
                                                                  "path": "/product/delete"
                                                                }
                                                                """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno al intentar eliminar producto",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    examples = @ExampleObject(
                                                            name = "ErrorInternoServidor",
                                                            summary = "Error interno",
                                                            value = """
                                                                {
                                                                  "status": 500,
                                                                  "error": "Internal Server Error",
                                                                  "message": "Foreign key constraint fails",
                                                                  "path": "/product/delete"
                                                                }
                                                                """
                                                    )
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/product/add-product-stock",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = IProductHandler.class,
                    beanMethod = "addProductStock",
                    operation = @Operation(
                            operationId = "addProductStock",
                            summary = "Aumentar stock de un producto",
                            description = "Agrega una cantidad específica al stock de un producto existente",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            examples = @ExampleObject(
                                                    name = "AgregarStockEjemplo",
                                                    summary = "Ejemplo para agregar stock",
                                                    value = """
                                                        {
                                                          "id": 10,
                                                          "quantity": 5
                                                        }
                                                        """
                                            )
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Stock actualizado exitosamente",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    examples = @ExampleObject(
                                                            name = "StockActualizado",
                                                            summary = "Respuesta exitosa",
                                                            value = """
                                                                {
                                                                  "id": 10,
                                                                  "name": "Latte",
                                                                  "stock": 15,
                                                                  "isActive": true
                                                                }
                                                                """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Solicitud inválida por datos incorrectos o inexistencia del producto",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    examples = @ExampleObject(
                                                            name = "ErrorStock",
                                                            summary = "Error al actualizar stock",
                                                            value = """
                                                                {
                                                                  "status": 400,
                                                                  "error": "Bad Request",
                                                                  "message": "Product does not exist or invalid quantity",
                                                                  "path": "/product/add-product-stock"
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
        return route(POST(ConstRoute.PRODUCT + ConstRoute.CREATE), productHandler::createProduct)
                .andRoute(POST(ConstRoute.PRODUCT + ConstRoute.DELETE), productHandler::deleteProduct)
                .andRoute(POST(ConstRoute.PRODUCT+ConstRoute.ADD_PRODUCT_STOCK_REST_ROUTE),
                        productHandler::addProductStock);
    }
}
