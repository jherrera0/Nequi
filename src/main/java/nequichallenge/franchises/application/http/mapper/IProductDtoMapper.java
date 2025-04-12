package nequichallenge.franchises.application.http.mapper;

import nequichallenge.franchises.application.http.dto.request.CreateProductDtoRequest;
import nequichallenge.franchises.application.http.dto.response.ProductDtoResponse;
import nequichallenge.franchises.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IProductDtoMapper {
    ProductDtoResponse toProductDto(Product product);
    Product toProduct(CreateProductDtoRequest createProductDtoRequest);
}
