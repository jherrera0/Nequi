package nequichallenge.franchises.domain.spi;

import nequichallenge.franchises.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface IFranchisePersistencePort {
    Mono<Franchise> createFranchise(Franchise franchise);

    Mono<Boolean> franchiseExistsByName(String name);
    Mono<Boolean> franchiseExistsById(Integer id);

    Mono<Franchise> findById(Integer id);

    Mono<Franchise> updateFranchise(Franchise existedFranchise);
}
