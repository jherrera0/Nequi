package nequichallenge.franchises.infrastructure.entrypoint.mapper;

import nequichallenge.franchises.infrastructure.entrypoint.dto.request.CreateFranchiseDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.dto.request.UpdateNameDtoRequest;
import nequichallenge.franchises.infrastructure.entrypoint.dto.response.FranchiseCustomDtoResponse;
import nequichallenge.franchises.infrastructure.entrypoint.dto.response.FranchiseDtoResponse;
import nequichallenge.franchises.domain.model.Franchise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {IBranchDtoMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IFranchiseDtoMapper {
    FranchiseDtoResponse toDtoResponse(Franchise franchise);
    Franchise toDomain(UpdateNameDtoRequest franchiseDto);
    @Mapping(target = "name", source = "name")
    Franchise toDomain(CreateFranchiseDtoRequest franchiseDto);
    FranchiseCustomDtoResponse toCustomDtoResponse(Franchise franchise);
}
