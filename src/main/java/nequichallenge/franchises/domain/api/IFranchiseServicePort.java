package nequichallenge.franchises.domain.api;

import nequichallenge.franchises.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface IFranchiseServicePort {
    Mono<Franchise> createFranchise(Franchise franchise);

    Mono<Franchise> updateName(Franchise franchise);
}
