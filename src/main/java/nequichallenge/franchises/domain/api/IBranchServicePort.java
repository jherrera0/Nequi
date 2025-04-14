package nequichallenge.franchises.domain.api;

import nequichallenge.franchises.domain.model.Branch;
import reactor.core.publisher.Mono;

public interface IBranchServicePort {
    Mono<Branch> addBranch(Integer franchiseId, String name);

    Mono<Branch> updateName(Branch branch);
}
