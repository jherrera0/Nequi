package nequichallenge.franchises.infrastructure.adapters.persistence.adapter;

import lombok.AllArgsConstructor;
import nequichallenge.franchises.infrastructure.adapters.persistence.mapper.IFranchiseEntityMapper;
import nequichallenge.franchises.infrastructure.adapters.persistence.repository.IFranchiseRepository;
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

    @Override
    public Mono<Boolean> franchiseExistsById(Integer id) {
        return franchiseRepository.existsById(id);
    }

    @Override
    public Mono<Franchise> findById(Integer id) {
        return franchiseRepository.findById(id).map(franchiseEntityMapper::toFranchise);
    }

    @Override
    public Mono<Franchise> updateFranchise(Franchise existedFranchise) {
        return franchiseRepository.findById(existedFranchise.getId())
                .flatMap(franchiseEntity -> {
                    franchiseEntity.setName(existedFranchise.getName());
                    return franchiseRepository.save(franchiseEntity);
                })
                .map(franchiseEntityMapper::toFranchise);
    }
}
