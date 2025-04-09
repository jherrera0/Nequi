package nequichallenge.franchises.domain.spi;

import nequichallenge.franchises.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface IFranchisePersistencePort {
    Mono<Franchise> createFranchise(Franchise franchise);
}
