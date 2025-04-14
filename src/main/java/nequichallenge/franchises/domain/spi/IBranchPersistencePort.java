package nequichallenge.franchises.domain.spi;

import nequichallenge.franchises.domain.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBranchPersistencePort {
    Mono<Branch> addBranch(Integer franchiseId, String name);
    Mono<Boolean> existsByName(String name);

    Mono<Boolean> existsById(Integer branchId);

    Flux<Branch> getBranchesByFranchiseId(Integer franchiseId);

    Mono<Branch> findById(Integer id);

    Mono<Branch> updateBranch(Branch existedBranch);
}
