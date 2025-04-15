package nequichallenge.franchises.infrastructure.adapters.persistence.repository;

import nequichallenge.franchises.infrastructure.adapters.persistence.entity.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IFranchiseRepository extends ReactiveCrudRepository<FranchiseEntity, Integer> {
    Mono<Boolean> existsByName(String name);
}
