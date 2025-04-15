package nequichallenge.franchises.infrastructure.adapters.persistence.adapter;

import lombok.RequiredArgsConstructor;
import nequichallenge.franchises.infrastructure.adapters.persistence.entity.BranchEntity;
import nequichallenge.franchises.infrastructure.adapters.persistence.mapper.IBranchEntityMapper;
import nequichallenge.franchises.infrastructure.adapters.persistence.repository.IBranchRepository;
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

    @Override
    public Mono<Branch> findById(Integer id) {
        return branchRepository.findById(id).map(branchEntityMapper::toModel);
    }

    @Override
    public Mono<Branch> updateBranch(Branch existedBranch) {
        return branchRepository.findById(existedBranch.getId())
                .flatMap(entity -> {
                    entity.setName(existedBranch.getName());
                    return branchRepository.save(entity);
                })
                .map(branchEntityMapper::toModel);
    }
}
