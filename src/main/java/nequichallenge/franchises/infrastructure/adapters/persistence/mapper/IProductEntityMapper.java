package nequichallenge.franchises.infrastructure.adapters.persistence.mapper;

import nequichallenge.franchises.infrastructure.adapters.persistence.entity.ProductEntity;
import nequichallenge.franchises.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IProductEntityMapper {
    @Mapping(target = "isActive", source = "isActive")
    ProductEntity toProductEntity(Product product);
    Product toProduct(ProductEntity productEntity);
}
