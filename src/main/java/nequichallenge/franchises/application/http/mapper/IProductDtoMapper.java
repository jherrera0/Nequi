package nequichallenge.franchises.application.http.mapper;

import nequichallenge.franchises.application.http.dto.request.AddProductStockDtoRequest;
import nequichallenge.franchises.application.http.dto.request.CreateProductDtoRequest;
import nequichallenge.franchises.application.http.dto.request.DeleteProductDtoRequest;
import nequichallenge.franchises.application.http.dto.request.UpdateNameDtoRequest;
import nequichallenge.franchises.application.http.dto.response.ProductDtoResponse;
import nequichallenge.franchises.application.http.dto.response.ProductTopStockDtoResponse;
import nequichallenge.franchises.domain.model.Product;
import nequichallenge.franchises.domain.model.ProductTopStock;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IProductDtoMapper {
    ProductDtoResponse toProductDto(Product product);
    Product toProduct(DeleteProductDtoRequest dtoRequest);
    Product toProduct(AddProductStockDtoRequest dtoRequest);
    Product toProduct(CreateProductDtoRequest createProductDtoRequest);
    Product toProduct(UpdateNameDtoRequest dto);
    ProductTopStockDtoResponse toProductTopStockDto(ProductTopStock productTopStock);
}
