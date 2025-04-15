package nequichallenge.franchises.infrastructure.adapters.persistence.mapper;

import nequichallenge.franchises.infrastructure.adapters.persistence.entity.BranchEntity;
import nequichallenge.franchises.domain.model.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {IProductEntityMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IBranchEntityMapper {
    BranchEntity toEntity(Branch branch);

    Branch toModel(BranchEntity branchEntity);
}
