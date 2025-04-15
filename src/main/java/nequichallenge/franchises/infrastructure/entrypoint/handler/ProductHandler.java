package nequichallenge.franchises.infrastructure.entrypoint.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nequichallenge.franchises.domain.exception.*;
import nequichallenge.franchises.infrastructure.entrypoint.dto.request.AddProductStockDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.dto.request.CreateProductDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.dto.request.DeleteProductDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.dto.request.UpdateNameDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.handler.interfaces.IProductHandler;
import nequichallenge.franchises.infrastructure.entrypoint.mapper.IProductDtoMapper;
import nequichallenge.franchises.domain.api.IProductServicePort;
import org.springframework.http.HttpStatus;
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
                .flatMap(dto -> productServicePort.createProduct(dto.getBranchId(),
                        productDtoMapper.toProduct(dto)))
                .map(productDtoMapper::toProductDto)
                .flatMap(dtoResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(dtoResponse))
                .onErrorResume(ProductNameEmptyException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                ).onErrorResume(ProductStockInvalidException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                ).onErrorResume(BranchIdInvalidException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                ).onErrorResume(BranchNotFoundException.class, ex ->
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                ).onErrorResume(ProductAlreadyExistsException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                );
    }

    @Override
    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        return request.bodyToMono(DeleteProductDtoRequest.class)
                .map(productDtoMapper::toProduct)
                .flatMap(productServicePort::deleteProduct)
                .map(productDtoMapper::toProductDto)
                .flatMap(dtoResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(dtoResponse))
                .onErrorResume(ProductNotFoundException.class, ex ->
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                );
    }

    @Override
    public Mono<ServerResponse> addProductStock(ServerRequest request) {
        return request.bodyToMono(AddProductStockDtoRequest.class)
                .map(productDtoMapper::toProduct)
                .flatMap(productServicePort::addProductStock)
                .map(productDtoMapper::toProductDto)
                .flatMap(dtoResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(dtoResponse))
                .onErrorResume(ProductNotFoundException.class, ex ->
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                ).onErrorResume(ProductStockInvalidException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                );
    }

    @Override
    public Mono<ServerResponse> getTopStockProductsByBranchAssociatedToFranchise(ServerRequest request) {
        Integer franchiseId = Integer.valueOf(request.pathVariable("franchiseId"));
        return productServicePort.getTopStockProductsByBranchAssociatedToFranchise(franchiseId)
                .map(productDtoMapper::toProductTopStockDto)
                .collectList()
                .flatMap(products -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(products))
                .onErrorResume(FranchiseNotFoundException.class, ex ->
                        ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                );
    }

    @Override
    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        return request.bodyToMono(UpdateNameDtoRequest.class)
                .map(productDtoMapper::toProduct)
                .flatMap(productServicePort::updateProductName)
                .map(productDtoMapper::toProductDto)
                .flatMap(dtoResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(dtoResponse))
                .onErrorResume(ProductNameEmptyException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                ).onErrorResume(ProductNotFoundException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ex.getMessage())
                );
    }
}
