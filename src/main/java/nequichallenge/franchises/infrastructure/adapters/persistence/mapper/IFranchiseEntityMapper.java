package nequichallenge.franchises.infrastructure.adapters.persistence.mapper;

import nequichallenge.franchises.infrastructure.adapters.persistence.entity.FranchiseEntity;
import nequichallenge.franchises.domain.model.Franchise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {IBranchEntityMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IFranchiseEntityMapper {
    @Mapping(target = "id", ignore = true)
    FranchiseEntity toFranchiseEntity(Franchise franchise);

    Franchise toFranchise(FranchiseEntity franchiseEntity);
}
