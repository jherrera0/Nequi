package nequichallenge.franchises.application.http.handler;

import nequichallenge.franchises.application.http.dto.request.CreateProductDtoRequest;
import nequichallenge.franchises.application.http.dto.response.ProductDtoResponse;
import nequichallenge.franchises.application.http.mapper.IProductDtoMapper;
import nequichallenge.franchises.domain.api.IProductServicePort;
import nequichallenge.franchises.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
    void createProductReturnsErrorWhenRequestBodyIsEmpty() {
        // Arrange
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.empty());

        // Act & Assert
        StepVerifier.create(productHandler.createProduct(serverRequest))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException &&
                        error.getMessage().equals("Request body cannot be empty"))
                .verify();
    }
}
