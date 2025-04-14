package nequichallenge.franchises.application.http.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nequichallenge.franchises.application.http.dto.request.AddProductStockDtoRequest;
import nequichallenge.franchises.application.http.dto.request.CreateProductDtoRequest;
import nequichallenge.franchises.application.http.dto.request.DeleteProductDtoRequest;
import nequichallenge.franchises.application.http.handler.interfaces.IProductHandler;
import nequichallenge.franchises.application.http.mapper.IProductDtoMapper;
import nequichallenge.franchises.domain.api.IProductServicePort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductHandler implements IProductHandler {
    private final IProductServicePort productServicePort;
    private final IProductDtoMapper productDtoMapper;

    @Override
    public Mono<ServerResponse> createProduct(ServerRequest request) {
        return request.bodyToMono(CreateProductDtoRequest.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body cannot be empty")))
                .flatMap(dto -> productServicePort.createProduct(dto.getBranchId(),
                        productDtoMapper.toProduct(dto)))
                .doOnNext(product -> log.info("Created product: {}", product.getId()))
                .map(productDtoMapper::toProductDto)
                .doOnNext(productDto -> log.info("Created product: {}", productDto))
                .flatMap(dtoResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(dtoResponse));
    }

    @Override
    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        return request.bodyToMono(DeleteProductDtoRequest.class)
                .map(productDtoMapper::toProduct)
                .flatMap(productServicePort::deleteProduct)
                .map(productDtoMapper::toProductDto)
                .flatMap(dtoResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(dtoResponse));
    }

    @Override
    public Mono<ServerResponse> addProductStock(ServerRequest request) {
        return request.bodyToMono(AddProductStockDtoRequest.class)
                .map(productDtoMapper::toProduct)
                .flatMap(productServicePort::addProductStock)
                .map(productDtoMapper::toProductDto)
                .flatMap(dtoResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(dtoResponse));
    }
}
