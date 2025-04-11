package nequichallenge.franchises.application.persistence.adapter;

import lombok.AllArgsConstructor;
import nequichallenge.franchises.application.persistence.mapper.IFranchiseEntityMapper;
import nequichallenge.franchises.application.persistence.repository.IFranchiseRepository;
import nequichallenge.franchises.domain.model.Franchise;
import nequichallenge.franchises.domain.spi.IFranchisePersistencePort;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class FranchiseAdapter implements IFranchisePersistencePort {
    private final IFranchiseEntityMapper franchiseEntityMapper;
    private final IFranchiseRepository franchiseRepository;

    @Override
    public Mono<Franchise> createFranchise(Franchise franchise) {
        return franchiseRepository.save(franchiseEntityMapper.toFranchiseEntity(franchise))
                .map(franchiseEntityMapper::toFranchise);
    }

    @Override
    public Mono<Boolean> franchiseExistsByName(String name) {
        return franchiseRepository.existsByName(name);
    }
}
