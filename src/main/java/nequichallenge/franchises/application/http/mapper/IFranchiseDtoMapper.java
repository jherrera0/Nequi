package nequichallenge.franchises.application.http.mapper;

import nequichallenge.franchises.application.http.dto.request.CreateFranchiseDtoRequest;
import nequichallenge.franchises.application.http.dto.response.FranchiseDtoResponse;
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
    @Mapping(target = "name", source = "name")
    Franchise toDomain(CreateFranchiseDtoRequest franchiseDto);

}
