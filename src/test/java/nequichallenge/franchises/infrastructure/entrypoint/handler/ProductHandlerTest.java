package nequichallenge.franchises.infrastructure.entrypoint.handler;

import nequichallenge.franchises.domain.exception.*;
import nequichallenge.franchises.infrastructure.entrypoint.dto.request.AddProductStockDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.dto.request.CreateProductDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.dto.request.DeleteProductDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.dto.request.UpdateNameDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.dto.response.ProductDtoResponse;
import nequichallenge.franchises.infrastructure.entrypoint.dto.response.ProductTopStockDtoResponse;
import nequichallenge.franchises.infrastructure.entrypoint.mapper.IProductDtoMapper;
import nequichallenge.franchises.domain.api.IProductServicePort;
import nequichallenge.franchises.domain.model.Product;
import nequichallenge.franchises.domain.model.ProductTopStock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProductHandlerTest {

    @Mock
    private IProductServicePort productServicePort;


    @Mock
    private IProductDtoMapper productDtoMapper;

    @InjectMocks
    private ProductHandler productHandler;

    @Test
    void createProductReturnsOkResponseWhenValidRequest() {
        // Arrange
        Integer branchId = 1;
        Integer productId = 10;

        CreateProductDtoRequest requestDto = new CreateProductDtoRequest("Latte", 5, branchId);
        Product domainProduct = new Product(productId, "Latte", 5);
        ProductDtoResponse responseDto = new ProductDtoResponse(productId, "Latte", 5, true);

        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        when(productDtoMapper.toProduct(requestDto)).thenReturn(domainProduct);
        when(productServicePort.createProduct(branchId, domainProduct)).thenReturn(Mono.just(domainProduct));
        when(productDtoMapper.toProductDto(domainProduct)).thenReturn(responseDto);

        // Act & Assert
        StepVerifier.create(productHandler.createProduct(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void createProductReturnsErrorWhenBranchIdIsNull() {
        // Arrange
        CreateProductDtoRequest requestDto = new CreateProductDtoRequest("Latte", 5, null);
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        when(productDtoMapper.toProduct(requestDto))
                .thenThrow(new IllegalArgumentException("Branch ID cannot be null"));

        // Act & Assert
        StepVerifier.create(productHandler.createProduct(serverRequest))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException &&
                        error.getMessage().equals("Branch ID cannot be null"))
                .verify();
    }

    @Test
    void deleteProductReturnsOkResponseWhenValidRequest() {
        DeleteProductDtoRequest requestDto = new DeleteProductDtoRequest(10);
        Product domainProduct = new Product(10, "Latte", 5);
        ProductDtoResponse responseDto = new ProductDtoResponse(10, "Latte", 5, false);

        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        when(productDtoMapper.toProduct(requestDto)).thenReturn(domainProduct);
        when(productServicePort.deleteProduct(domainProduct)).thenReturn(Mono.just(domainProduct));
        when(productDtoMapper.toProductDto(domainProduct)).thenReturn(responseDto);

        StepVerifier.create(productHandler.deleteProduct(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void addProductStockReturnsOkResponseWhenValidRequest() {
        AddProductStockDtoRequest requestDto = new AddProductStockDtoRequest(10, 5);
        Product domainProduct = new Product(10, "Latte", 10);
        Product updatedProduct = new Product(10, "Latte", 5);
        ProductDtoResponse responseDto = new ProductDtoResponse(10, "Latte", 5, true);

        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        when(productDtoMapper.toProduct(requestDto)).thenReturn(domainProduct);
        when(productServicePort.addProductStock(domainProduct)).thenReturn(Mono.just(updatedProduct));
        when(productDtoMapper.toProductDto(updatedProduct)).thenReturn(responseDto);

        StepVerifier.create(productHandler.addProductStock(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }
    @Test
    void getTopStockProductsByBranchAssociatedToFranchiseReturnsOkResponseWhenValidFranchiseId() {
        Integer franchiseId = 1;
        List<ProductTopStock> domainProducts = List.of(
                new ProductTopStock(1,"1",1, "Latte", 10),
                new ProductTopStock(1,"1", 2, "Espresso", 8)
        );
        List<ProductTopStockDtoResponse> responseDtos = List.of(
                new ProductTopStockDtoResponse(1,"1",1, "Latte", 10),
                new ProductTopStockDtoResponse(1,"1",2, "Espresso", 8)
        );

        when(productServicePort.getTopStockProductsByBranchAssociatedToFranchise(franchiseId))
                .thenReturn(Flux.fromIterable(domainProducts));
        when(productDtoMapper.toProductTopStockDto(domainProducts.get(0))).thenReturn(responseDtos.get(0));
        when(productDtoMapper.toProductTopStockDto(domainProducts.get(1))).thenReturn(responseDtos.get(1));

        StepVerifier.create(productHandler.getTopStockProductsByBranchAssociatedToFranchise(
                        MockServerRequest.builder().pathVariable("franchiseId", "1").build()))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }
    @Test
    void updateProductNameReturnsOkResponseWhenValidRequest() {
        UpdateNameDtoRequest requestDto = new UpdateNameDtoRequest(10, "Cappuccino");
        Product domainProduct = new Product(10, "Latte", 5);
        Product updatedProduct = new Product(10, "Cappuccino", 5);
        ProductDtoResponse responseDto = new ProductDtoResponse(10, "Cappuccino", 5, true);

        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        when(productDtoMapper.toProduct(requestDto)).thenReturn(domainProduct);
        when(productServicePort.updateProductName(domainProduct)).thenReturn(Mono.just(updatedProduct));
        when(productDtoMapper.toProductDto(updatedProduct)).thenReturn(responseDto);

        StepVerifier.create(productHandler.updateProductName(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void createProductReturnsErrorWhenProductNameIsEmpty() {
        CreateProductDtoRequest requestDto = new CreateProductDtoRequest("", 5, 1);
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        when(productDtoMapper.toProduct(requestDto))
                .thenThrow(new ProductNameEmptyException());

        StepVerifier.create(productHandler.createProduct(serverRequest))
                .expectNextMatches(response -> response.statusCode().is4xxClientError() &&
                        response.statusCode().value() == 400)
                .verifyComplete();
    }

    @Test
    void createProductReturnsErrorWhenProductStockIsInvalid() {
        CreateProductDtoRequest requestDto = new CreateProductDtoRequest("Latte", -1, 1);
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        when(productDtoMapper.toProduct(requestDto))
                .thenThrow(new ProductStockInvalidException());

        StepVerifier.create(productHandler.createProduct(serverRequest))
                .expectNextMatches(response -> response.statusCode().is4xxClientError() &&
                        response.statusCode().value() == 400)
                .verifyComplete();
    }

    @Test
    void createProductReturnsErrorWhenBranchIdIsInvalid() {
        CreateProductDtoRequest requestDto = new CreateProductDtoRequest("Latte", 5, -1);
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        when(productDtoMapper.toProduct(requestDto))
                .thenThrow(new BranchIdInvalidException());

        StepVerifier.create(productHandler.createProduct(serverRequest))
                .expectNextMatches(response -> response.statusCode().is4xxClientError() &&
                        response.statusCode().value() == 400)
                .verifyComplete();
    }
    @Test
    void createProductReturnsErrorWhenBranchNotFound() {
        CreateProductDtoRequest requestDto = new CreateProductDtoRequest("Latte", 5, 999);
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        when(productDtoMapper.toProduct(requestDto))
                .thenThrow(new BranchNotFoundException());

        StepVerifier.create(productHandler.createProduct(serverRequest))
                .expectNextMatches(response -> response.statusCode().is4xxClientError() &&
                        response.statusCode().value() == 404)
                .verifyComplete();
    }

    @Test
    void createProductReturnsErrorWhenProductAlreadyExists() {
        CreateProductDtoRequest requestDto = new CreateProductDtoRequest("Latte", 5, 1);
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        when(productDtoMapper.toProduct(requestDto))
                .thenThrow(new ProductAlreadyExistsException());

        StepVerifier.create(productHandler.createProduct(serverRequest))
                .expectNextMatches(response -> response.statusCode().is4xxClientError() &&
                        response.statusCode().value() == 400)
                .verifyComplete();
    }
    @Test
    void deleteProductReturnsErrorWhenProductNotFound() {
        DeleteProductDtoRequest requestDto = new DeleteProductDtoRequest(999);
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        Product nonExistentProduct = new Product(999, "NonExistentProduct", 0);

        when(productDtoMapper.toProduct(requestDto)).thenReturn(nonExistentProduct);
        when(productServicePort.deleteProduct(org.mockito.ArgumentMatchers.any(Product.class)))
                .thenThrow(new ProductNotFoundException());

        StepVerifier.create(productHandler.deleteProduct(serverRequest))
                .expectNextMatches(response -> response.statusCode().is4xxClientError() &&
                        response.statusCode().value() == 404)
                .verifyComplete();
    }

    @Test
    void addProductStockReturnsErrorWhenProductNotFound() {
        AddProductStockDtoRequest requestDto = new AddProductStockDtoRequest(999, 10);
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        when(productDtoMapper.toProduct(requestDto))
                .thenReturn(new Product(999, "NonExistentProduct", 0));
        when(productServicePort.addProductStock(org.mockito.ArgumentMatchers.any(Product.class)))
                .thenThrow(new ProductNotFoundException());

        StepVerifier.create(productHandler.addProductStock(serverRequest))
                .expectNextMatches(response -> response.statusCode().is4xxClientError() &&
                        response.statusCode().value() == 404)
                .verifyComplete();
    }
    @Test
    void updateProductNameReturnsErrorWhenProductNameIsEmpty() {
        UpdateNameDtoRequest requestDto = new UpdateNameDtoRequest(10, "");
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        when(productDtoMapper.toProduct(requestDto))
                .thenThrow(new ProductNameEmptyException());

        StepVerifier.create(productHandler.updateProductName(serverRequest))
                .expectNextMatches(response -> response.statusCode().is4xxClientError() &&
                        response.statusCode().value() == 400)
                .verifyComplete();
    }

    @Test
    void addProductStockReturnsErrorWhenStockIsInvalid() {
        AddProductStockDtoRequest requestDto = new AddProductStockDtoRequest(10, -5);
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        when(productDtoMapper.toProduct(requestDto))
                .thenThrow(new ProductStockInvalidException());

        StepVerifier.create(productHandler.addProductStock(serverRequest))
                .expectNextMatches(response -> response.statusCode().is4xxClientError() &&
                        response.statusCode().value() == 400)
                .verifyComplete();
    }

    @Test
    void getTopStockProductsByBranchAssociatedToFranchiseReturnsErrorWhenFranchiseNotFound() {
        Integer franchiseId = 999;

        when(productServicePort.getTopStockProductsByBranchAssociatedToFranchise(franchiseId))
                .thenReturn(Flux.error(new FranchiseNotFoundException()));

        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", franchiseId.toString())
                .build();

        StepVerifier.create(productHandler.getTopStockProductsByBranchAssociatedToFranchise(request))
                .expectNextMatches(response ->
                        response.statusCode() == HttpStatus.NOT_FOUND
                )
                .verifyComplete();
    }


    @Test
    void updateProductNameReturnsErrorWhenProductNotFound() {
        UpdateNameDtoRequest requestDto = new UpdateNameDtoRequest(999, "Cappuccino");
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(requestDto));

        when(productDtoMapper.toProduct(requestDto))
                .thenReturn(new Product(999, "NonExistentProduct", 0));
        when(productServicePort.updateProductName(org.mockito.ArgumentMatchers.any(Product.class)))
                .thenThrow(new ProductNotFoundException());

        StepVerifier.create(productHandler.updateProductName(serverRequest))
                .expectNextMatches(response -> response.statusCode().is4xxClientError() &&
                        response.statusCode().value() == 400)
                .verifyComplete();
    }
}
