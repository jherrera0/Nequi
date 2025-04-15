package nequichallenge.franchises.infrastructure.entrypoint.mapper;

import nequichallenge.franchises.infrastructure.entrypoint.dto.request.UpdateNameDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.dto.response.BranchCustomDtoResponse;
import nequichallenge.franchises.infrastructure.entrypoint.dto.response.BranchDtoResponse;
import nequichallenge.franchises.domain.model.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {IProductDtoMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IBranchDtoMapper {
    BranchDtoResponse toBranchDto(Branch branch);
    BranchCustomDtoResponse toBranchCustomDto(Branch branch);
    Branch toDomain(UpdateNameDtoRequest updateNameDtoRequest);
}
