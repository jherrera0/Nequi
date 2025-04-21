package nequichallenge.franchises.infrastructure.adapters.persistence.repository;

import nequichallenge.franchises.infrastructure.adapters.persistence.entity.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBranchRepository extends ReactiveCrudRepository<BranchEntity, Integer> {
    Mono<Boolean> existsByName(String name);

    Flux<BranchEntity> findAllByFranchiseId(Integer franchiseId);
}
