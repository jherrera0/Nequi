package nequichallenge.franchises.application.http.mapper;

import nequichallenge.franchises.application.http.dto.response.BranchDtoResponse;
import nequichallenge.franchises.domain.model.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {IProductDtoMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IBranchDtoMapper {
    BranchDtoResponse toBranchDto(Branch branch);
}
