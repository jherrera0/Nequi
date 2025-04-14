package nequichallenge.franchises.application.persistence.adapter;

import lombok.RequiredArgsConstructor;
import nequichallenge.franchises.application.persistence.entity.BranchEntity;
import nequichallenge.franchises.application.persistence.mapper.IBranchEntityMapper;
import nequichallenge.franchises.application.persistence.repository.IBranchRepository;
import nequichallenge.franchises.domain.model.Branch;
import nequichallenge.franchises.domain.spi.IBranchPersistencePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchAdapter implements IBranchPersistencePort {
    private final IBranchRepository branchRepository;
    private final IBranchEntityMapper branchEntityMapper;


    @Override
    public Mono<Branch> addBranch(Integer franchiseId, String name) {
        BranchEntity entity = new BranchEntity();
        entity.setName(name);
        entity.setFranchiseId(franchiseId);
        return branchRepository.save(entity)
                .map(branchEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return branchRepository.existsByName(name);
    }

    @Override
    public Mono<Boolean> existsById(Integer branchId) {
        return branchRepository.existsById(branchId);
    }

    @Override
    public Flux<Branch> getBranchesByFranchiseId(Integer franchiseId) {
        return branchRepository.findAllByFranchiseId((franchiseId)).map(branchEntityMapper::toModel);
    }
}
